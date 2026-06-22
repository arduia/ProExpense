#!/bin/bash
#
# Cursor `stop` hook: refresh the codebase reference snapshot when the agent finishes.
# Mirrors the Claude Stop hook; delegates to scripts/update-codebase-snapshot.sh.
#
set -euo pipefail

# Consume hook stdin (Cursor sends a JSON payload) so the pipe never blocks.
cat > /dev/null || true

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

"${PROJECT_ROOT}/scripts/update-codebase-snapshot.sh" || true

exit 0
