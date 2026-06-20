#!/usr/bin/env bash
# Sync design handoff reference PNGs into Roborazzi golden files (414×868 artboard).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC="$ROOT/design_handoff_pro_expense/reference-images"
DEST="$ROOT/app/src/test/screenshots"
GOLDEN_PREFIX="com.arduia.expense.ui.handoff.HandoffReferenceScreenshotTest"

if [[ ! -d "$SRC" ]]; then
  echo "Missing handoff reference images: $SRC" >&2
  exit 1
fi

declare -A TEST_TO_REFERENCE=(
  [flow_04_splash]=flow-04-splash.png
  [flow_04_onb_1]=flow-04-onb-1.png
  [flow_04_onb_5]=flow-04-onb-5.png
  [flow_04_prof_name]=flow-04-prof-name.png
  [flow_04_prof_currency]=flow-04-prof-currency.png
  [flow_05_pin_setup]=flow-05-pin-setup.png
  [flow_05_pin_entry]=flow-05-pin-entry.png
  [flow_01_home_empty]=flow-01-home-empty.png
  [flow_01_home_casual]=flow-01-home-casual.png
  [flow_01_home_budget]=flow-01-home-budget.png
  [flow_01_home_event]=flow-01-home-event.png
  [flow_01_add_zero]=flow-01-add-zero.png
  [flow_01_add_amount]=flow-01-add-amount.png
  [flow_01_add_details]=flow-01-add-details.png
  [flow_02_journal_list]=flow-02-journal-list.png
  [flow_02_journal_detail]=flow-02-journal-detail.png
  [flow_03_more_hub]=flow-03-more-hub.png
  [flow_03_reports]=flow-03-reports.png
  [flow_03_categories]=flow-03-categories.png
  [flow_03_more_export]=flow-03-more-export.png
  [flow_03_more_clear]=flow-03-more-clear.png
  [flow_06_event_list]=flow-06-event-list.png
  [flow_06_event_empty]=flow-06-event-empty.png
  [flow_06_event_create]=flow-06-event-create.png
  [flow_06_event_detail]=flow-06-event-detail.png
  [flow_07_debt_lent]=flow-07-debt-lent.png
  [flow_07_debt_add]=flow-07-debt-add.png
  [flow_07_debt_lent_detail]=flow-07-debt-lent-detail.png
  [flow_08_shared_input]=flow-08-shared-input.png
  [flow_08_shared_summary]=flow-08-shared-summary.png
  [flow_08_shared_history]=flow-08-shared-history.png
)

count=0
for test_method in "${!TEST_TO_REFERENCE[@]}"; do
  reference="${TEST_TO_REFERENCE[$test_method]}"
  source_file="$SRC/$reference"
  if [[ ! -f "$source_file" ]]; then
    echo "Missing reference PNG: $source_file" >&2
    exit 1
  fi
  cp "$source_file" "$DEST/${GOLDEN_PREFIX}.${test_method}.png"
  count=$((count + 1))
done

echo "Synced $count handoff reference goldens to $DEST (${GOLDEN_PREFIX}.*.png)"
