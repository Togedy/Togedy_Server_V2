#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

echo "Pull latest code from main"
git pull origin main

cd ../docker

echo "Stopping existing containers"
docker compose down

echo "Building & starting services"
docker compose up -d --build

echo "Deployment complete"