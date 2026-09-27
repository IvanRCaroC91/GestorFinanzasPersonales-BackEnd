#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CONFIG_DIR="$ROOT_DIR/config/config"

required_files=(
  "ms-api-gateway"
  "ms-auth-service"
  "ms-finance-service"
  "ms-service-registry"
)
profiles=("dev" "qa" "prod")

require_text() {
  local file="$1"
  local text="$2"

  if ! grep -Fq "$text" "$file"; then
    printf 'FALLO: %s no contiene "%s"\n' "$file" "$text" >&2
    exit 1
  fi
}

for profile in "${profiles[@]}"; do
  for service in "${required_files[@]}"; do
    file="$CONFIG_DIR/$service-$profile.yml"

    if [[ ! -f "$file" ]]; then
      printf 'FALLO: falta la configuración %s\n' "$file" >&2
      exit 1
    fi

    if grep -Ev '^[[:space:]]*#' "$file" | grep -Eq 'qa-(db-host|eureka)|prod-(db-host|eureka)|DB_USERNAME|DB_PASSWORD'; then
      printf 'FALLO: %s contiene referencias obsoletas o ficticias\n' "$file" >&2
      exit 1
    fi
  done

  require_text "$CONFIG_DIR/ms-api-gateway-$profile.yml" '${EUREKA_URL}'
  require_text "$CONFIG_DIR/ms-api-gateway-$profile.yml" '${JWT_SECRET}'

  for service in ms-auth-service ms-finance-service; do
    file="$CONFIG_DIR/$service-$profile.yml"
    require_text "$file" '${DATABASE_URL}'
    require_text "$file" '${DATABASE_USER}'
    require_text "$file" '${DATABASE_PASSWORD}'
    require_text "$file" '${EUREKA_URL}'
  done

  require_text "$CONFIG_DIR/ms-service-registry-$profile.yml" '${EUREKA_URL}'
done

if command -v docker >/dev/null 2>&1; then
  (
    cd "$ROOT_DIR"
    POSTGRES_DB=smoke_db \
    POSTGRES_USER=smoke_user \
    POSTGRES_PASSWORD=smoke_password \
    CONFIG_SERVER_USER=smoke_config \
    CONFIG_SERVER_PASSWORD=smoke_config_password \
    DATABASE_URL=jdbc:postgresql://postgres:5432/smoke_db \
    DATABASE_USER=smoke_user \
    DATABASE_PASSWORD=smoke_password \
    JWT_SECRET=smoke_jwt_secret_12345678901234567890 \
    docker compose -f docker/docker-compose.yml config --quiet
  )
fi

echo "Smoke tests de perfiles dev, qa, prod y docker completados correctamente."
