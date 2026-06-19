#!/usr/bin/env bash
# Post Pro Expense APK build status to Slack via incoming webhook.
set -euo pipefail

STATUS="${1:-}"
BRANCH="${2:-}"
COMMIT_SHA="${3:-}"
RUN_URL="${4:-}"
ARTIFACT_NAME="${5:-}"
APK_FILE="${6:-}"

if [[ -z "$STATUS" || -z "$BRANCH" || -z "$COMMIT_SHA" || -z "$RUN_URL" || -z "$ARTIFACT_NAME" ]]; then
  echo "Usage: $0 <success|failure> <branch> <commit_sha> <run_url> <artifact_name> [apk_file]" >&2
  exit 1
fi

if [[ -z "${SLACK_WEBHOOK_URL:-}" ]]; then
  echo "SLACK_WEBHOOK_URL not configured; skipping Slack notification"
  exit 0
fi

if ! command -v jq >/dev/null 2>&1; then
  echo "jq is required to build the Slack payload" >&2
  exit 1
fi

APP_NAME="${APP_NAME:-Pro Expense}"
REPO="${GITHUB_REPOSITORY:-arduia/ProExpense}"
SHORT_SHA="${COMMIT_SHA:0:7}"

if [[ "$STATUS" == "success" ]]; then
  EMOJI=":white_check_mark:"
  HEADLINE="${APP_NAME} APK build succeeded"
  DETAIL="Download the APK from the workflow artifacts."
else
  EMOJI=":x:"
  HEADLINE="${APP_NAME} APK build failed"
  DETAIL="Open the workflow run for logs."
fi

APK_DETAIL=""
if [[ -n "$APK_FILE" && -f "$APK_FILE" ]]; then
  APK_BYTES="$(wc -c <"$APK_FILE" | tr -d ' ')"
  APK_MB="$(awk "BEGIN { printf \"%.1f\", $APK_BYTES / 1048576 }")"
  APK_DETAIL=" APK size: ${APK_MB} MB."
fi

PAYLOAD="$(jq -n \
  --arg emoji "$EMOJI" \
  --arg headline "$HEADLINE" \
  --arg repo "$REPO" \
  --arg branch "$BRANCH" \
  --arg sha "$SHORT_SHA" \
  --arg artifact "$ARTIFACT_NAME" \
  --arg detail "${DETAIL}${APK_DETAIL}" \
  --arg run_url "$RUN_URL" \
  '{
    text: ($emoji + " " + $headline),
    blocks: [
      {
        type: "header",
        text: { type: "plain_text", text: $headline }
      },
      {
        type: "section",
        fields: [
          { type: "mrkdwn", text: ("*Repository:*\n" + $repo) },
          { type: "mrkdwn", text: ("*Branch:*\n`" + $branch + "`") },
          { type: "mrkdwn", text: ("*Commit:*\n`" + $sha + "`") },
          { type: "mrkdwn", text: ("*Artifact:*\n`" + $artifact + "`") }
        ]
      },
      {
        type: "section",
        text: {
          type: "mrkdwn",
          text: ($detail + "\n<" + $run_url + "|Open GitHub Actions run>")
        }
      }
    ]
  }')"

curl -fsS -X POST \
  -H 'Content-type: application/json' \
  --data "$PAYLOAD" \
  "$SLACK_WEBHOOK_URL"
