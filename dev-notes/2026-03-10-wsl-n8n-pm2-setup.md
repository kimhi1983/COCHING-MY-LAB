# WSL Ubuntu-22.04 환경 정비 및 n8n + PM2 설정

**날짜:** 2026-03-10
**환경:** WSL Ubuntu-22.04 / Windows 11

---

## 1. ~/.bashrc 119번째 줄 수정

### 문제
119번째 줄에 Windows 경로가 Linux PATH로 잘못 등록되어 syntax error 발생:
```bash
export PATH="C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot\/bin:..."
```

### 조치
```bash
cp ~/.bashrc ~/.bashrc.backup  # 백업
sed -i '119d' ~/.bashrc        # 해당 줄 삭제
bash -n ~/.bashrc              # syntax 검증 → OK
```

---

## 2. Node.js 20 LTS 설치

### 잔존 프로세스 정리
이전 세션에서 남은 apt-get 프로세스들이 apt lock 점유:
- PID 2243, 2273: `apt-get install redis-server`
- PID 2445: `apt-get install nginx`
- PID 13368: NodeSource setup (lock 대기 중)

```bash
# root로 kill
wsl -d Ubuntu-22.04 -u root -e bash -c "kill -9 2243 2273 2445 13368"

# apt lock 정리
wsl -d Ubuntu-22.04 -u root -e bash -c \
  "rm -f /var/lib/dpkg/lock-frontend /var/lib/dpkg/lock \
   /var/lib/apt/lists/lock /var/cache/apt/archives/lock \
   && dpkg --configure -a"
```

### NodeSource 20.x 설치
```bash
# sudo 없이 root로 직접 실행 (tty 문제 우회)
wsl -d Ubuntu-22.04 -u root -e bash -c \
  "curl -fsSL https://deb.nodesource.com/setup_20.x | bash -"

wsl -d Ubuntu-22.04 -u root -e bash -c "apt install nodejs -y"
```

### 결과
| 항목 | 버전 |
|------|------|
| Node.js | v20.20.1 |
| npm | 10.8.2 |

---

## 3. n8n 재설치

```bash
wsl -d Ubuntu-22.04 -u root -e bash -c "npm install -g n8n"
```

- **n8n 버전:** v2.8.4
- 접속: http://localhost:5678

---

## 4. PM2 설치 및 n8n 백그라운드 실행

### PM2 설치
```bash
wsl -d Ubuntu-22.04 -u root -e bash -c "npm install -g pm2"
# PM2 버전: v6.0.14
```

### n8n PM2 등록
```bash
# kpros 유저로 실행
pm2 delete n8n 2>/dev/null
pm2 start n8n --name n8n -- start
pm2 save
```

### PM2 systemd startup 등록
```bash
# startup 명령 확인
pm2 startup
# → sudo env PATH=... /usr/lib/node_modules/pm2/bin/pm2 startup systemd -u kpros --hp /home/kpros

# Windows PATH 괄호 오류 우회: 깨끗한 PATH로 root 실행
wsl -d Ubuntu-22.04 -u root -e bash -c \
  "export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
   && /usr/lib/node_modules/pm2/bin/pm2 startup systemd -u kpros --hp /home/kpros"
```

- **systemd 서비스:** `pm2-kpros.service` 등록 완료

---

## 5. 최종 상태 확인

```
node -v    → v20.20.1
npm -v     → 10.8.2
n8n --version → 2.8.4
pm2 --version → 6.0.14

PM2 프로세스:
 id=0  coching-ai  online  (PID 2401, 108MB)
 id=1  n8n         online  (PID 15509, 245MB)

curl http://localhost:5678 → HTTP 200 ✓
```

---

## 핵심 주의사항 (WSL)

| 문제 | 해결 |
|------|------|
| sudo tty 없음 | `wsl -u root -e bash -c` 사용 |
| Windows PATH 괄호 `(x86)` | root 실행 시 PATH를 Linux 기본값으로 재설정 |
| apt lock 충돌 | lock 파일 삭제 + `dpkg --configure -a` |
| 잔존 프로세스 | `ps x`로 확인 후 `kill -9` |
