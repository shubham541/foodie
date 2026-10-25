#!/usr/bin/env bash
# One-time setup on a fresh Ubuntu VM: installs Docker and opens ports 80/443 in the OS firewall.
set -euo pipefail
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker "$USER"
# Oracle Ubuntu images ship restrictive iptables rules
sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 443 -j ACCEPT
sudo apt-get install -y iptables-persistent >/dev/null 2>&1 || true
sudo netfilter-persistent save || true
echo "Done. Log out and back in so the docker group applies, then follow docs/DEPLOY.md step 4."
