#!/usr/bin/env bash
# Daily MySQL backup; keeps 14 days. Install: crontab -e  ->  0 3 * * * /home/ubuntu/HouseOfShukita/deploy/backup.sh
set -euo pipefail
cd "$(dirname "$0")/.."
set -a; . ./.env; set +a
mkdir -p backups
docker compose exec -T db mysqldump -uroot -p"$FOODIE_DB_PASSWORD" --single-transaction shukita_foodie | gzip > "backups/shukita-$(date +%F).sql.gz"
find backups -name 'shukita-*.sql.gz' -mtime +14 -delete
