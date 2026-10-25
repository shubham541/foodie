# Deploying House of Shukita (free, always on)

Target: an **Oracle Cloud Always Free** Arm VM running Docker Compose: Caddy (automatic HTTPS) → Spring Boot app → MySQL. Nothing depends on your laptop; the whole system lives on the VM and updates with `git pull`.

> Free-tier terms change. Check [Oracle's Always Free page](https://www.oracle.com/cloud/free/) before you start. A payment card is needed to verify the account, and you should set a budget alert (step 1).

## 1. Create the VM
1. Sign up at oracle.com/cloud/free, pick the **India (Mumbai or Hyderabad)** home region (it cannot be changed later) and add a budget alert of ₹1 under *Billing → Budgets*.
2. *Compute → Instances → Create*: image **Ubuntu 22.04 or 24.04 (aarch64)**, shape **VM.Standard.A1.Flex** with 2 OCPU / 8–12 GB RAM, download the SSH key, assign a public IPv4 address. (If A1 capacity is unavailable, retry later or in another availability domain. The 1 GB AMD micro shape is too small.)
3. *Networking → your VCN → Security List*: add **ingress rules** for TCP **80** and **443** from `0.0.0.0/0`.
4. **Keep the account active:** Oracle can reclaim idle Always Free instances. Upgrading the account to *Pay As You Go* (you are still charged nothing while inside the free limits) removes that risk.

## 2. Get a free HTTPS address
Pick one:
- **nip.io (zero setup):** use `<server-ip>.nip.io`, e.g. `129.1.2.3.nip.io`. Works immediately.
- **DuckDNS:** create `shukita.duckdns.org` at duckdns.org and point it to the server IP.
- **Your own domain:** add an `A` record to the server IP.

## 3. Prepare the server
```bash
ssh -i <key> ubuntu@<server-ip>
git clone https://github.com/shubham541/HouseOfShukita.git && cd HouseOfShukita
bash deploy/setup-vm.sh          # installs Docker, opens ports 80/443; then log out and back in
```

## 4. Configure and start
```bash
cd HouseOfShukita
cp .env.example .env && nano .env
```
Set `DOMAIN` (step 2), `FOODIE_DB_PASSWORD`, `FOODIE_RESTAURANT_PASSWORD` (your owner login) and `FOODIE_JWT_SECRET` (`openssl rand -base64 48`). Then:
```bash
docker compose up -d --build     # first build takes a few minutes
docker compose logs -f app       # wait for "Started FoodieApplication"
```
Open `https://<DOMAIN>`; Caddy fetches the certificate by itself. Log in as the owner at `/login` with `FOODIE_RESTAURANT_EMAIL` / `FOODIE_RESTAURANT_PASSWORD`. The menu is seeded on first start.

In production the app refuses to start if the database URL points at localhost, if the database or owner password is a default, or if the JWT secret is a development value, so a mistake shows up immediately in `docker compose logs app`. The live database is a fresh, empty MySQL volume on the VM, seeded with the menu on first start; nothing is read from your laptop.

## 5. Day-to-day
| Task | Command |
|------|---------|
| Deploy new code | `bash deploy/update.sh` |
| Logs | `docker compose logs -f app` |
| Restart | `docker compose restart app` |
| Daily backup (keeps 14 days) | `crontab -e` → `0 3 * * * /home/ubuntu/HouseOfShukita/deploy/backup.sh` |
| Restore a backup | `gunzip -c backups/shukita-YYYY-MM-DD.sql.gz \| docker compose exec -T db mysql -uroot -p"$FOODIE_DB_PASSWORD" shukita_foodie` |
| Copy backups off the VM | `scp -i <key> ubuntu@<ip>:HouseOfShukita/backups/*.gz .` (do this regularly: a VM failure would otherwise lose them) |

## 6. Moving your existing local data (optional)
```bash
mysqldump -uroot -p shukita_foodie > local.sql
scp -i <key> local.sql ubuntu@<ip>:~
# on the server, after the first start:
docker compose exec -T db mysql -uroot -p"$FOODIE_DB_PASSWORD" shukita_foodie < ~/local.sql
docker compose restart app
```
This also copies your local test orders, so only do it if you want them.

## Notes
- Only ports 80/443 are public. MySQL and the app are reachable only inside the Docker network.
- Passwords and JWT secret live in `.env` on the server only (git-ignored).
- Continuous integration (`.github/workflows/ci.yml`) runs the tests and builds the Docker image on every push, but does not deploy; deploying is the one-line `deploy/update.sh`.
- Fallback if Oracle signup or capacity does not work out: the same `docker-compose.yml` runs on any VPS, and Render's free tier works for the app but sleeps after 15 minutes idle.
