# PostgreSQL 통합 설정 (2026-03-13)

## 변경 사항
- Windows PG 17을 단일 데이터베이스로 통합
- WSL2 PostgreSQL 비활성화 (`systemctl disable postgresql`)
- n8n PostgreSQL 크리덴셜을 Windows PG로 변경 (host=172.21.144.1)

## 네트워크 구성
- WSL2 모드: NAT + `localhostForwarding=true`
- `.wslconfig`: `[wsl2]\nlocalhostForwarding=true`
- n8n 웹 UI: http://localhost:5678 (localhostForwarding으로 Windows→WSL2 전달)

## Windows 방화벽
- PostgreSQL (TCP 5432): WSL2 대역 인바운드 허용
- n8n (TCP 5678): 인바운드 허용

## pg_hba.conf 추가 항목
```
host    coching_db      coching_user    172.16.0.0/12           md5
host    all             all             172.16.0.0/12           md5
```

## 데이터 이관 완료
- `coching` 스키마 (t_coos_prod 등)
- product_ingredients
- GEMINI_SAFETY, REG_MONITOR_US 테이블
- estimated_ph 컬럼: numeric → text 변환

## n8n 워크플로우 정리
- 6개 비활성 Track 1 워크플로우 삭제 (v2.3-A~F)
- 5개 활성 워크플로우만 유지

## 주의사항
- Windows 재부팅 시 WSL2 호스트 IP(172.21.x.x) 변경 가능
- 변경 시 n8n PG 크리덴셜 host 업데이트 필요
