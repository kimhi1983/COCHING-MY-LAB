#!/bin/bash
# COCHING 서버 의존성 설치 스크립트
# WSL 터미널에서 직접 실행: bash /mnt/e/COCHING/nginx-config/install-services.sh

set -e
echo "=== COCHING 서버 의존성 설치 ==="

# 1. Redis 설치
echo "[1/4] Redis 설치..."
sudo apt-get install -y redis-server
sudo sed -i 's/^supervised no/supervised systemd/' /etc/redis/redis.conf
sudo systemctl enable redis-server
sudo systemctl start redis-server
redis-cli ping && echo "Redis OK"

# 2. Nginx 설치
echo "[2/4] Nginx 설치..."
sudo apt-get install -y nginx
sudo systemctl enable nginx

# 3. Nginx 설정 복사
echo "[3/4] Nginx 설정 적용..."
sudo cp /mnt/e/COCHING/nginx-config/coching.conf /etc/nginx/sites-available/coching.conf
sudo ln -sf /etc/nginx/sites-available/coching.conf /etc/nginx/sites-enabled/coching.conf
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl start nginx
echo "Nginx OK"

# 4. Cloudflare Tunnel 설치
echo "[4/4] Cloudflare Tunnel 설치..."
curl -L --output cloudflared.deb https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb
sudo dpkg -i cloudflared.deb
rm cloudflared.deb
cloudflared --version && echo "cloudflared OK"

echo ""
echo "=== 설치 완료 ==="
echo ""
echo "다음 단계:"
echo "1. Redis 확인:    redis-cli ping"
echo "2. Nginx 확인:    sudo nginx -t && curl http://localhost"
echo "3. Cloudflare:    cloudflared tunnel login"
echo "4. 터널 생성:     cloudflared tunnel create coching"
echo "5. 설정 복사:     cp /mnt/e/COCHING/nginx-config/cloudflare-tunnel-config.yml ~/.cloudflared/config.yml"
echo "   (tunnel ID 교체 필요)"
echo "6. 터널 시작:     cloudflared tunnel run coching"
