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
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# shellcheck source=../../.cursor/hooks/ensure-android-sdk.sh
source "${PROJECT_ROOT}/.cursor/hooks/ensure-android-sdk.sh"
ensure_android_sdk

# Bootstrap Android agent skills when missing — mirrors the Cursor session-start hook
# so the same skill set (.agents/skills/) is available in Claude Code web sessions.
skills_status="present"
if [ ! -f "${PROJECT_ROOT}/.agents/skills/android-cli/SKILL.md" ]; then
  skills_status="missing"
  if [ -x "${PROJECT_ROOT}/scripts/install-android-skills.sh" ]; then
    if "${PROJECT_ROOT}/scripts/install-android-skills.sh" >/dev/null 2>&1; then
      skills_status="installed"
    else
      skills_status="install-failed"
    fi
  fi
fi

echo "Android SDK ready at ${ENSURE_ANDROID_SDK_ROOT} (${ENSURE_ANDROID_SDK_STATUS}). Agent skills: ${skills_status}."
