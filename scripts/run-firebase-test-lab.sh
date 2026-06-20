#!/usr/bin/env bash
# Build devDebug + androidTest APKs and run instrumentation tests on Firebase Test Lab.
#
# Required env:
#   FIREBASE_PROJECT_ID — GCP project linked to Firebase
#
# Optional env:
#   FIREBASE_TEST_RESULTS_BUCKET — GCS bucket for result artifacts (without gs://)
#   FIREBASE_TEST_RESULTS_DIR    — path prefix inside the bucket (default: firebase-test-lab/<sha>)
#   FIREBASE_DEVICE_MATRIX       — comma-separated device specs (default: virtual MediumPhone API 34)
#   FIREBASE_TEST_TIMEOUT        — per-matrix timeout (default: 10m)
#   FIREBASE_FLAVOR              — product flavor (default: dev)
#   FIREBASE_BUILD_TYPE          — build type (default: debug)
#   CI_COMMIT                    — label for results dir when FIREBASE_TEST_RESULTS_DIR is unset
#
# CI auth: authenticate gcloud before invoking (service account JSON via
# google-github-actions/auth or `gcloud auth activate-service-account`).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

FLAVOR="${FIREBASE_FLAVOR:-dev}"
BUILD_TYPE="${FIREBASE_BUILD_TYPE:-debug}"
PROJECT_ID="${FIREBASE_PROJECT_ID:?FIREBASE_PROJECT_ID is required}"
RESULTS_BUCKET="${FIREBASE_TEST_RESULTS_BUCKET:-}"
RESULTS_DIR="${FIREBASE_TEST_RESULTS_DIR:-firebase-test-lab/${CI_COMMIT:-local}-$(date +%s)}"
DEVICE_MATRIX="${FIREBASE_DEVICE_MATRIX:-model=MediumPhone.arm,version=34,locale=en,orientation=portrait}"
TIMEOUT="${FIREBASE_TEST_TIMEOUT:-10m}"

capitalize() {
  local value="$1"
  printf '%s%s' "${value:0:1}" "${value:1}"
}

FLAVOR_CAP="$(capitalize "$FLAVOR")"
BUILD_TYPE_CAP="$(capitalize "$BUILD_TYPE")"
GRADLE_VARIANT="${FLAVOR_CAP}${BUILD_TYPE_CAP}"

log() { printf '==> %s\n' "$*"; }

if ! command -v gcloud >/dev/null 2>&1; then
  log "gcloud CLI not found. Install Google Cloud SDK before running Firebase Test Lab."
  exit 1
fi

log "Building app and instrumentation APKs (:app:assemble${GRADLE_VARIANT}*)"
./gradlew --no-daemon --stacktrace \
  ":app:assemble${GRADLE_VARIANT}" \
  ":app:assemble${GRADLE_VARIANT}AndroidTest"

APP_APK="app/build/outputs/apk/${FLAVOR}/${BUILD_TYPE}/app-${FLAVOR}-${BUILD_TYPE}.apk"
TEST_APK="app/build/outputs/apk/androidTest/${FLAVOR}/${BUILD_TYPE}/app-${FLAVOR}-${BUILD_TYPE}-androidTest.apk"

for artifact in "$APP_APK" "$TEST_APK"; do
  if [[ ! -f "$artifact" ]]; then
    log "Expected artifact missing: $artifact"
    exit 1
  fi
done

log "App APK: $APP_APK"
log "Test APK: $TEST_APK"
log "Device matrix: $DEVICE_MATRIX"

GCLOUD_ARGS=(
  firebase test android run
  --type instrumentation
  --app "$APP_APK"
  --test "$TEST_APK"
  --device "$DEVICE_MATRIX"
  --timeout "$TIMEOUT"
  --project "$PROJECT_ID"
  --no-record-video
  --no-performance-metrics
)

if [[ -n "$RESULTS_BUCKET" ]]; then
  GCLOUD_ARGS+=(--results-bucket "gs://${RESULTS_BUCKET}" --results-dir "$RESULTS_DIR")
fi

log "Submitting to Firebase Test Lab (project: $PROJECT_ID)"
gcloud "${GCLOUD_ARGS[@]}"
