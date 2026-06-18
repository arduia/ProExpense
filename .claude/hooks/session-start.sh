#!/bin/bash
#
# SessionStart hook: install the Android SDK so Gradle can compile, build and
# test ProExpense in Claude Code on the web sessions.
#
# Delegates to the shared Cursor bootstrap script at .cursor/hooks/.
#
set -euo pipefail

if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=../../.cursor/hooks/ensure-android-sdk.sh
source "${SCRIPT_DIR}/../../.cursor/hooks/ensure-android-sdk.sh"
ensure_android_sdk

echo "Android SDK ready at ${ENSURE_ANDROID_SDK_ROOT} (${ENSURE_ANDROID_SDK_STATUS})."
