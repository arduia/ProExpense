#!/bin/bash
#
# Cursor sessionStart hook — bootstrap Android SDK before the agent runs Gradle.
# Mirrors the Claude Code session-start hook, adapted for Cursor's JSON hook protocol.
#
set -euo pipefail

cat > /dev/null

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=ensure-android-sdk.sh
source "${SCRIPT_DIR}/ensure-android-sdk.sh"
ensure_android_sdk

sdk_root="${ENSURE_ANDROID_SDK_ROOT:-}"
status="${ENSURE_ANDROID_SDK_STATUS:-unknown}"

context=""
if [ "${status}" = "installed" ]; then
  context="Android SDK was installed at ${sdk_root} (platform 36, build-tools 35.0.0). Gradle verification is available."
elif [ "${status}" = "existing" ]; then
  context="Android SDK is configured at ${sdk_root}. Gradle verification is available."
fi

python3 - <<PY
import json
import os

payload = {
    "env": {
        "ANDROID_HOME": os.environ.get("ANDROID_HOME", "${sdk_root}"),
        "ANDROID_SDK_ROOT": os.environ.get("ANDROID_SDK_ROOT", "${sdk_root}"),
        "PATH": f"{os.environ.get('ANDROID_HOME', '${sdk_root}')}/cmdline-tools/latest/bin:"
              f"{os.environ.get('ANDROID_HOME', '${sdk_root}')}/platform-tools:"
              + os.environ.get("PATH", ""),
    }
}
context = """${context}"""
if context:
    payload["additional_context"] = context
print(json.dumps(payload))
PY
