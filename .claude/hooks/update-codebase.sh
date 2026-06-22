#!/bin/bash
#
# Claude `Stop` hook: refresh the codebase reference snapshot when the agent finishes
# a turn. Delegates to the shared generator at scripts/update-codebase-snapshot.sh.
#
set -euo pipefail

# Consume hook stdin (JSON payload) so the pipe never blocks.
cat > /dev/null || true

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

"${PROJECT_ROOT}/scripts/update-codebase-snapshot.sh" || true

exit 0
