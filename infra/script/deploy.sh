#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

BRANCH=${1:-develop}
PROFILE=${2:-}

echo "Pull latest code from $BRANCH"
git pull origin "$BRANCH"

cd ../docker

echo "Stopping existing containers"
docker compose down

echo "Building & starting services"
if [ -n "$PROFILE" ]; then
  docker compose --profile "$PROFILE" up -d --build
else
  docker compose up -d --build
fi

echo "Deployment complete"
