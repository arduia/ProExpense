#!/usr/bin/env bash
# Step 5.5 automation — verify Roborazzi baselines exist and optionally diff against
# design_handoff_v2 reference PNGs (see design_handoff_v2/screenshot-alignment.yaml).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MAPPING="$ROOT/design_handoff_v2/screenshot-alignment.yaml"
BASELINES="$ROOT/app/src/test/screenshots"
REFERENCES="$ROOT/design_handoff_v2/reference-images"
THRESHOLD="${DESIGN_ALIGNMENT_THRESHOLD:-0.15}" # normalized RMSE; lower = stricter

log() { printf 'verify-design-alignment: %s\n' "$*"; }
fail() { log "ERROR: $*"; exit 1; }

[[ -f "$MAPPING" ]] || fail "missing $MAPPING"
[[ -d "$BASELINES" ]] || fail "missing baseline dir $BASELINES"
[[ -d "$REFERENCES" ]] || fail "missing reference dir $REFERENCES"

python3 - "$MAPPING" "$BASELINES" "$REFERENCES" "$THRESHOLD" <<'PY'
import re
import subprocess
import sys
from pathlib import Path

mapping_path, baselines_dir, references_dir, threshold_s = sys.argv[1:5]
threshold = float(threshold_s)
baselines = Path(baselines_dir)
references = Path(references_dir)

text = Path(mapping_path).read_text()
entries = []
for block in re.split(r"\n\s*-\s+baseline:\s*", text)[1:]:
    baseline_m = re.match(r"(\S+\.png)", block)
    reference_m = re.search(r"reference:\s*(\S+)", block)
    crop_m = re.search(
        r"crop:\s*\{\s*width:\s*(\d+),\s*height:\s*(\d+),\s*anchor:\s*(\w+)\s*\}",
        block,
    )
    if not baseline_m or not reference_m:
        continue
    entries.append(
        {
            "baseline": baseline_m.group(1),
            "reference": reference_m.group(1),
            "crop": crop_m.groups() if crop_m else None,
        }
    )

if not entries:
    sys.exit("no alignments parsed from screenshot-alignment.yaml")

has_compare = subprocess.run(["which", "compare"], capture_output=True).returncode == 0
has_convert = subprocess.run(["which", "convert"], capture_output=True).returncode == 0
can_diff = has_compare and has_convert

missing = []
diff_failures = []
checked = 0

for entry in entries:
    baseline = baselines / entry["baseline"]
    reference = references / entry["reference"]
    if not baseline.is_file():
        missing.append(f"baseline missing: {entry['baseline']}")
        continue
    if not reference.is_file():
        missing.append(f"reference missing: {entry['reference']}")
        continue
    checked += 1
    if not can_diff:
        continue

    ref_for_compare = reference
    tmp_ref = None
    if entry["crop"]:
        width, height, anchor = entry["crop"]
        width, height = int(width), int(height)
        tmp_ref = Path(f"/tmp/design-align-{entry['baseline']}")
        gravity = "South" if anchor == "bottom" else "Center"
        subprocess.run(
            [
                "convert",
                str(reference),
                "-gravity",
                gravity,
                "-crop",
                f"{width}x{height}+0+0",
                "+repage",
                str(tmp_ref),
            ],
            check=True,
            capture_output=True,
        )
        ref_for_compare = tmp_ref

    # Resize baseline to reference dimensions for fair compare when viewport matches artboard.
    proc = subprocess.run(
        [
            "compare",
            "-metric",
            "RMSE",
            "-subimage-search",
            str(ref_for_compare),
            str(baseline),
            "null:",
        ],
        capture_output=True,
        text=True,
    )
    # compare writes metric to stderr, e.g. "1234 (0.0189)"
    metric = proc.stderr.strip()
    m = re.search(r"\(([\d.]+)\)", metric)
    score = float(m.group(1)) if m else 1.0
    print(f"  diff {entry['baseline']} vs {entry['reference']}: RMSE={score:.4f}")
    if score > threshold:
        diff_failures.append(
            f"{entry['baseline']} vs {entry['reference']} RMSE {score:.4f} > {threshold}"
        )
    if tmp_ref:
        tmp_ref.unlink(missing_ok=True)

print(f"checked {checked} baseline/reference pairs")
if missing:
    print("MISSING:")
    print("\n".join(f"  - {m}" for m in missing))
if diff_failures:
    print("DIFF THRESHOLD EXCEEDED:")
    print("\n".join(f"  - {d}" for d in diff_failures))
if not can_diff:
    print("note: ImageMagick not installed — existence check only (install imagemagick for pixel diff)")

if missing or diff_failures:
    sys.exit(1)
PY

log "design alignment check passed"
