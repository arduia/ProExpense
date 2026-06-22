#!/bin/bash
#
# Regenerate the codebase reference snapshot at .cursor/context/project_codebase.md.
#
# PLACEHOLDER — wiring only. This is invoked automatically at the end of an agent
# turn by the Claude `Stop` hook (.claude/settings.json -> .claude/hooks/update-codebase.sh)
# and the Cursor `stop` hook (.cursor/hooks.json -> .cursor/hooks/update-codebase.sh).
#
# The actual snapshot-generation logic will be added later. Keep this fast and
# side-effect-free until then so it is safe to run on every turn.
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SNAPSHOT="${PROJECT_ROOT}/.cursor/context/project_codebase.md"

# TODO: regenerate "${SNAPSHOT}" from the live module/file structure
# (module map, key file index, current branch). Intentionally a no-op for now.
: "${SNAPSHOT}"

exit 0
