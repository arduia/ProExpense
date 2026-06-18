#!/bin/bash
#
# Idempotent Android SDK bootstrap for ProExpense agent sessions.
# Installs command-line tools + platform 36 into ./android-sdk (gitignored)
# and writes sdk.dir to local.properties when missing.
#
# Safe to source or run directly. Skips when a working SDK is already configured.
#
set -euo pipefail

ensure_android_sdk() {
  local project_dir="${CURSOR_PROJECT_DIR:-${CLAUDE_PROJECT_DIR:-$(pwd)}}"
  local sdk_root="${project_dir}/android-sdk"
  local local_props="${project_dir}/local.properties"
  local cmdline_tools_version="15641748"
  local cmdline_tools_url="https://dl.google.com/android/repository/commandlinetools-linux-${cmdline_tools_version}_latest.zip"
  local sdk_packages=(
    "platform-tools"
    "platforms;android-36"
    "build-tools;35.0.0"
  )
  local sdkmanager="${sdk_root}/cmdline-tools/latest/bin/sdkmanager"
  local existing_sdk=""

  if [ -f "${local_props}" ]; then
    existing_sdk="$(grep '^sdk.dir=' "${local_props}" 2>/dev/null | head -1 | cut -d= -f2- | tr -d '\r' || true)"
  fi

  if [ -n "${existing_sdk}" ] && [ -d "${existing_sdk}" ] && [ -x "${existing_sdk}/platform-tools/adb" ]; then
    export ANDROID_HOME="${existing_sdk}"
    export ANDROID_SDK_ROOT="${existing_sdk}"
    ENSURE_ANDROID_SDK_ROOT="${existing_sdk}"
    ENSURE_ANDROID_SDK_STATUS="existing"
    return 0
  fi

  if [ -n "${ANDROID_HOME:-}" ] && [ -d "${ANDROID_HOME}" ] && [ -x "${ANDROID_HOME}/platform-tools/adb" ]; then
    ENSURE_ANDROID_SDK_ROOT="${ANDROID_HOME}"
    ENSURE_ANDROID_SDK_STATUS="existing"
    if [ ! -f "${local_props}" ] || ! grep -qs "^sdk.dir=" "${local_props}"; then
      echo "sdk.dir=${ANDROID_HOME}" >> "${local_props}"
    fi
    return 0
  fi

  if [ ! -x "${sdkmanager}" ]; then
    echo "Installing Android command-line tools..."
    mkdir -p "${sdk_root}/cmdline-tools"
    local tmp_zip
    tmp_zip="$(mktemp /tmp/cmdline-tools-XXXXXX.zip)"
    curl -fsSL "${cmdline_tools_url}" -o "${tmp_zip}"
    rm -rf "${sdk_root}/cmdline-tools/latest" "${sdk_root}/cmdline-tools/.tmp"
    mkdir -p "${sdk_root}/cmdline-tools/.tmp"
    unzip -q "${tmp_zip}" -d "${sdk_root}/cmdline-tools/.tmp"
    mv "${sdk_root}/cmdline-tools/.tmp/cmdline-tools" "${sdk_root}/cmdline-tools/latest"
    rm -rf "${sdk_root}/cmdline-tools/.tmp" "${tmp_zip}"
  fi

  export ANDROID_HOME="${sdk_root}"
  export ANDROID_SDK_ROOT="${sdk_root}"

  yes | "${sdkmanager}" --sdk_root="${sdk_root}" --licenses >/dev/null 2>&1 || true
  if ! "${sdkmanager}" --sdk_root="${sdk_root}" "${sdk_packages[@]}" >/tmp/sdkmanager-install.log 2>&1; then
    echo "Android SDK package install failed:" >&2
    cat /tmp/sdkmanager-install.log >&2
    return 1
  fi

  if ! grep -qs "^sdk.dir=" "${local_props}" 2>/dev/null; then
    echo "sdk.dir=${sdk_root}" >> "${local_props}"
  fi

  if [ -n "${CLAUDE_ENV_FILE:-}" ]; then
    {
      echo "export ANDROID_HOME=\"${sdk_root}\""
      echo "export ANDROID_SDK_ROOT=\"${sdk_root}\""
      echo "export PATH=\"${sdk_root}/cmdline-tools/latest/bin:${sdk_root}/platform-tools:\${PATH}\""
    } >> "${CLAUDE_ENV_FILE}"
  fi

  ENSURE_ANDROID_SDK_ROOT="${sdk_root}"
  ENSURE_ANDROID_SDK_STATUS="installed"
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  ensure_android_sdk
  echo "Android SDK ready at ${ENSURE_ANDROID_SDK_ROOT:-unknown} (${ENSURE_ANDROID_SDK_STATUS:-unknown})."
fi
