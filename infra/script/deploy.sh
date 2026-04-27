#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

BRANCH=${1:-develop}

echo "Pull latest code from $BRANCH"
git pull origin "$BRANCH"

cd ../docker

echo "Stopping existing containers"
docker compose down

echo "Building & starting services"
docker compose up -d --build

echo "Deployment complete"
