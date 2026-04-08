#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

echo "Pull latest code from develop"
git pull origin develop

cd ../docker

echo "Stopping existing containers"
docker compose down

echo "Building & starting services"
docker compose up -d --build

echo "Deployment complete"