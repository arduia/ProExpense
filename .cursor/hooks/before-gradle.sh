#!/bin/bash
#
# Cloud-agent fallback: sessionStart is not available in cloud VMs, so ensure the
# Android SDK is ready before the first ./gradlew invocation.
#
set -euo pipefail

cat > /dev/null

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=ensure-android-sdk.sh
source "${SCRIPT_DIR}/ensure-android-sdk.sh"
ensure_android_sdk

printf '%s\n' '{"permission":"allow"}'
