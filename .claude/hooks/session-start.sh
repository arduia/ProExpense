#!/bin/bash
#
# SessionStart hook: install the Android SDK so Gradle can compile, build and
# test ProExpense in Claude Code on the web sessions.
#
# Runs synchronously so the SDK is guaranteed ready before the agent starts.
# Idempotent: safe to re-run; the installed SDK is cached with the container.
#
set -euo pipefail

# Only needed in the remote (web) environment. Locally the developer already
# has their own Android SDK configured.
if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

PROJECT_DIR="${CLAUDE_PROJECT_DIR:-$(pwd)}"
SDK_ROOT="${PROJECT_DIR}/android-sdk"   # gitignored (see .gitignore)

# Pinned Google command-line tools build (linux). Update the number to bump.
CMDLINE_TOOLS_VERSION="15641748"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"

# Keep these in sync with gradle/libs.versions.toml (compileSdk / targetSdk = 36).
SDK_PACKAGES=(
  "platform-tools"
  "platforms;android-36"
  "build-tools;35.0.0"
)

SDKMANAGER="${SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager"

# 1. Install the command-line tools (skip if already present).
if [ ! -x "${SDKMANAGER}" ]; then
  echo "Installing Android command-line tools..."
  mkdir -p "${SDK_ROOT}/cmdline-tools"
  TMP_ZIP="$(mktemp /tmp/cmdline-tools-XXXXXX.zip)"
  curl -fsSL "${CMDLINE_TOOLS_URL}" -o "${TMP_ZIP}"
  rm -rf "${SDK_ROOT}/cmdline-tools/latest" "${SDK_ROOT}/cmdline-tools/.tmp"
  mkdir -p "${SDK_ROOT}/cmdline-tools/.tmp"
  unzip -q "${TMP_ZIP}" -d "${SDK_ROOT}/cmdline-tools/.tmp"
  # The archive extracts to a top-level "cmdline-tools/"; sdkmanager expects it
  # under "cmdline-tools/latest/".
  mv "${SDK_ROOT}/cmdline-tools/.tmp/cmdline-tools" "${SDK_ROOT}/cmdline-tools/latest"
  rm -rf "${SDK_ROOT}/cmdline-tools/.tmp" "${TMP_ZIP}"
fi

export ANDROID_HOME="${SDK_ROOT}"
export ANDROID_SDK_ROOT="${SDK_ROOT}"

# 2. Accept licenses, then install/update the required packages (idempotent —
#    sdkmanager skips anything already installed).
yes | "${SDKMANAGER}" --sdk_root="${SDK_ROOT}" --licenses >/dev/null 2>&1 || true
if ! "${SDKMANAGER}" --sdk_root="${SDK_ROOT}" "${SDK_PACKAGES[@]}" >/tmp/sdkmanager-install.log 2>&1; then
  echo "Android SDK package install failed:" >&2
  cat /tmp/sdkmanager-install.log >&2
  exit 1
fi

# 3. Point Gradle at the SDK (local.properties is gitignored).
LOCAL_PROPS="${PROJECT_DIR}/local.properties"
if ! grep -qs "^sdk.dir=" "${LOCAL_PROPS}" 2>/dev/null; then
  echo "sdk.dir=${SDK_ROOT}" >> "${LOCAL_PROPS}"
fi

# 4. Persist the SDK environment for the rest of the session.
if [ -n "${CLAUDE_ENV_FILE:-}" ]; then
  {
    echo "export ANDROID_HOME=\"${SDK_ROOT}\""
    echo "export ANDROID_SDK_ROOT=\"${SDK_ROOT}\""
    echo "export PATH=\"${SDK_ROOT}/cmdline-tools/latest/bin:${SDK_ROOT}/platform-tools:\${PATH}\""
  } >> "${CLAUDE_ENV_FILE}"
fi

echo "Android SDK ready at ${SDK_ROOT} (platform 36, build-tools 35.0.0)."
