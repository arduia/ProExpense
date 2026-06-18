#!/usr/bin/env bash
# Pro Expense — install Android SDK + warm Gradle dependencies for build/test.
# Safe to run repeatedly (idempotent). Used by CI and cloud-agent environments.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

COMPILE_SDK="${COMPILE_SDK:-36}"
BUILD_TOOLS="${BUILD_TOOLS:-36.0.0}"
SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$REPO_ROOT/android-sdk}}"
CMDLINE_TOOLS_ZIP="${CMDLINE_TOOLS_ZIP:-commandlinetools-linux-11076708_latest.zip}"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/${CMDLINE_TOOLS_ZIP}"

log() { printf '==> %s\n' "$*"; }

ensure_java_17() {
  if command -v java >/dev/null 2>&1; then
  local version
  version="$(java -version 2>&1 | head -n1 | sed -E 's/.*"([0-9]+).*/\1/')"
  if [[ "$version" -ge 17 ]]; then
    log "Java $version detected"
    return
  fi
  fi

  if command -v apt-get >/dev/null 2>&1; then
    log "Installing OpenJDK 17"
    export DEBIAN_FRONTEND=noninteractive
    sudo apt-get update -qq
    sudo apt-get install -y -qq openjdk-17-jdk
  else
    log "Java 17+ required. Install Temurin/OpenJDK 17 and re-run."
    exit 1
  fi
}

install_cmdline_tools() {
  mkdir -p "$SDK_ROOT/cmdline-tools"
  if [[ -x "$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]]; then
    log "Android cmdline-tools already installed at $SDK_ROOT"
    return
  fi

  log "Downloading Android command-line tools"
  local tmp
  tmp="$(mktemp -d)"
  curl -fsSL "$CMDLINE_TOOLS_URL" -o "$tmp/cmdline-tools.zip"
  unzip -q "$tmp/cmdline-tools.zip" -d "$tmp"
  rm -rf "$SDK_ROOT/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$SDK_ROOT/cmdline-tools/latest"
  rm -rf "$tmp"
}

accept_sdk_licenses() {
  export ANDROID_SDK_ROOT="$SDK_ROOT"
  export ANDROID_HOME="$SDK_ROOT"
  mkdir -p "$HOME/.android"
  touch "$HOME/.android/repositories.cfg"
  log "Accepting Android SDK licenses"
  set +o pipefail
  yes | sdkmanager --sdk_root="$SDK_ROOT" --licenses >/dev/null 2>&1 || true
  set -o pipefail
}

install_sdk_packages() {
  export ANDROID_SDK_ROOT="$SDK_ROOT"
  export ANDROID_HOME="$SDK_ROOT"
  export PATH="$SDK_ROOT/cmdline-tools/latest/bin:$SDK_ROOT/platform-tools:$PATH"

  accept_sdk_licenses
  log "Installing SDK packages (platform ${COMPILE_SDK}, build-tools ${BUILD_TOOLS})"
  sdkmanager --sdk_root="$SDK_ROOT" \
    "platform-tools" \
    "platforms;android-${COMPILE_SDK}" \
    "build-tools;${BUILD_TOOLS}"
}

write_local_properties() {
  log "Writing local.properties (sdk.dir=$SDK_ROOT)"
  printf 'sdk.dir=%s\n' "$SDK_ROOT" > "$REPO_ROOT/local.properties"
}

warm_gradle_dependencies() {
  chmod +x "$REPO_ROOT/gradlew"
  log "Downloading Gradle wrapper and resolving project dependencies"
  "$REPO_ROOT/gradlew" --no-daemon --stacktrace \
    buildEnvironment \
    :app:dependencies \
    :shared:dependencies
}

main() {
  ensure_java_17
  install_cmdline_tools
  install_sdk_packages
  write_local_properties
  warm_gradle_dependencies
  log "Android toolchain ready. Run ./gradlew verifyAll to build and test."
}

main "$@"
