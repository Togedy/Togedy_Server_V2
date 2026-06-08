#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

BRANCH=${1:-develop}
PROFILE=${2:-}

echo "=== Deploy: branch=$BRANCH ==="
git pull origin "$BRANCH"

cd ../docker

# ──────────────────────────────────────────────
# Dev: simple restart (no blue-green)
# ──────────────────────────────────────────────
if [ "$BRANCH" = "develop" ]; then
  docker compose -f docker-compose.yml -f docker-compose.dev.yml down
  docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
  echo "=== Dev deployment complete ==="
  exit 0
fi

# ──────────────────────────────────────────────
# Prod: blue-green deploy
# ──────────────────────────────────────────────
COMPOSE_FILES="-f docker-compose.yml -f docker-compose.prod.yml"
STATE_FILE=".current_color"
UPSTREAM_CONF="/etc/nginx/togedy-upstream.conf"

# blue: host 8080, green: host 8081
blue_port=8080
green_port=8081

wait_healthy() {
  local name="$1"
  echo "Waiting for $name to be healthy..."
  for i in $(seq 1 24); do
    STATUS=$(docker inspect --format='{{.State.Health.Status}}' "$name" 2>/dev/null || echo "starting")
    [ "$STATUS" = "healthy" ] && return 0
    [ $i -eq 24 ] && { echo "ERROR: $name did not become healthy"; return 1; }
    sleep 5
  done
}

switch_nginx() {
  local port="$1"
  echo "server localhost:${port};" | sudo tee "$UPSTREAM_CONF" > /dev/null
  sudo nginx -s reload
  echo "Nginx upstream switched to localhost:${port}"
}

CURRENT=$(cat "$STATE_FILE" 2>/dev/null || echo "none")

# ── 최초 배포 ──
if [ "$CURRENT" = "none" ]; then
  echo "First deployment — starting blue (port ${blue_port})..."

  docker compose $COMPOSE_FILES up -d --build togedy-server-blue redis

  wait_healthy "togedy-server-blue"
  switch_nginx "$blue_port"

  [ -n "$PROFILE" ] && docker compose $COMPOSE_FILES --profile "$PROFILE" up -d prometheus grafana

  echo "blue" > "$STATE_FILE"
  echo "=== Prod deployment complete (active: blue) ==="
  exit 0
fi

# ── 롤링 배포 ──
if [ "$CURRENT" = "blue" ]; then
  NEXT="green"; NEXT_PORT=$green_port; CURRENT_PORT=$blue_port
else
  NEXT="blue";  NEXT_PORT=$blue_port;  CURRENT_PORT=$green_port
fi

echo "Rolling: $CURRENT (port ${CURRENT_PORT}) → $NEXT (port ${NEXT_PORT})"

docker compose $COMPOSE_FILES up -d --build togedy-server-$NEXT

if wait_healthy "togedy-server-$NEXT"; then
  switch_nginx "$NEXT_PORT"
  docker compose $COMPOSE_FILES stop togedy-server-$CURRENT
  echo "$NEXT" > "$STATE_FILE"
  echo "=== Prod deployment complete (active: $NEXT) ==="
else
  echo "Health check failed — rolling back, $CURRENT remains active"
  docker compose $COMPOSE_FILES stop togedy-server-$NEXT
  exit 1
fi
