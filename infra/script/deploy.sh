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
if [ "$BRANCH" = "develop" ]; then
  COMPOSE_FILES="-f docker-compose.yml -f docker-compose.dev.yml"
else
  COMPOSE_FILES="-f docker-compose.yml"
fi

if [ -n "$PROFILE" ]; then
  docker compose $COMPOSE_FILES --profile "$PROFILE" up -d --build
else
  docker compose $COMPOSE_FILES up -d --build
fi

echo "Deployment complete"
