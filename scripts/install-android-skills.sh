#!/usr/bin/env bash
# Install official Android agent skills from https://github.com/android/skills
# into .agents/skills/ for Cursor (Agent Skills open standard).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ANDROID_BIN="${ANDROID_BIN:-android}"

if ! command -v "${ANDROID_BIN}" >/dev/null 2>&1; then
  echo "Android CLI not found. Installing..."
  curl -fsSL https://dl.google.com/android/cli/latest/linux_x86_64/install.sh | bash
  ANDROID_BIN="${HOME}/.local/bin/android"
  export PATH="${HOME}/.local/bin:${PATH}"
fi

echo "Installing Android skills for Cursor into ${ROOT}/.agents/skills ..."
"${ANDROID_BIN}" skills add --all --agent=cursor --project="${ROOT}"
echo "Done. Skills available at ${ROOT}/.agents/skills/"
