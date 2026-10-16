#!/usr/bin/env bash
set -euo pipefail
DIR=/opt/backups/stok
mkdir -p "$DIR"
FILE="$DIR/stok_$(date +%F_%H%M).sql.gz"
source /opt/stok-takip-be/.env
docker exec stok-db pg_dump -U "$DB_USERNAME" stok_takip | gzip > "$FILE"
find "$DIR" -name 'stok_*.sql.gz' -mtime +14 -delete