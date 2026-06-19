#!/usr/bin/env bash
# Post Pro Expense APK build status to Slack via incoming webhook.
set -euo pipefail

STATUS="${1:-}"
BRANCH="${2:-}"
COMMIT_SHA="${3:-}"
COMMIT_MESSAGE="${4:-}"
RUN_URL="${5:-}"
ARTIFACT_NAME="${6:-}"
APK_FILE="${7:-}"
APK_DOWNLOAD_URL="${8:-}"
ARTIFACT_URL="${9:-}"

if [[ -z "$STATUS" || -z "$BRANCH" || -z "$COMMIT_SHA" || -z "$RUN_URL" || -z "$ARTIFACT_NAME" ]]; then
  echo "Usage: $0 <success|failure> <branch> <commit_sha> <commit_message> <run_url> <artifact_name> [apk_file] [apk_download_url] [artifact_url]" >&2
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
  if [[ -n "$APK_DOWNLOAD_URL" ]]; then
    DETAIL="Install the APK using the direct download link below."
  elif [[ -n "$ARTIFACT_URL" ]]; then
    DETAIL="Download the APK from the GitHub artifact page (GitHub login required)."
  else
    DETAIL="Download the APK from the workflow run."
  fi
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

LINK_LINES=""
if [[ -n "$APK_DOWNLOAD_URL" ]]; then
  LINK_LINES+="<${APK_DOWNLOAD_URL}|Download APK (direct link)>"
fi
if [[ -n "$ARTIFACT_URL" ]]; then
  if [[ -n "$LINK_LINES" ]]; then
    LINK_LINES+="\n"
  fi
  LINK_LINES+="<${ARTIFACT_URL}|GitHub artifact page>"
fi
if [[ -n "$RUN_URL" ]]; then
  if [[ -n "$LINK_LINES" ]]; then
    LINK_LINES+="\n"
  fi
  LINK_LINES+="<${RUN_URL}|View workflow run>"
fi

COMMIT_MESSAGE_BLOCK="$(jq -n \
  --arg message "$COMMIT_MESSAGE" \
  'if ($message | length) > 0 then
    {
      type: "section",
      text: {
        type: "mrkdwn",
        text: ("*Commit message:*\n>" + $message)
      }
    }
  else
    empty
  end')"

PAYLOAD="$(jq -n \
  --arg emoji "$EMOJI" \
  --arg headline "$HEADLINE" \
  --arg repo "$REPO" \
  --arg branch "$BRANCH" \
  --arg sha "$SHORT_SHA" \
  --arg artifact "$ARTIFACT_NAME" \
  --arg detail "${DETAIL}${APK_DETAIL}" \
  --arg links "$LINK_LINES" \
  --argjson commit_message_block "${COMMIT_MESSAGE_BLOCK:-null}" \
  '{
    text: ($emoji + " " + $headline),
    blocks: (
      [
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
        }
      ]
      + (if $commit_message_block == null then [] else [$commit_message_block] end)
      + [
        {
          type: "section",
          text: {
            type: "mrkdwn",
            text: ($detail + "\n" + $links)
          }
        }
      ]
    )
  }')"

curl -fsS -X POST \
  -H 'Content-type: application/json' \
  --data "$PAYLOAD" \
  "$SLACK_WEBHOOK_URL"
