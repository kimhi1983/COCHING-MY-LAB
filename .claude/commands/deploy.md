# 배포 체크리스트

$ARGUMENTS 환경에 배포합니다.

## 사전 체크 (자동)
1. **qa-tester**: 전체 빌드 테스트
2. **security-reviewer**: 최종 보안 점검
3. 미커밋 변경사항 확인

## 배포 (사용자 확인 후)
4. **devops-infra**: 배포 실행
5. 헬스체크 + 롤백 계획 확인

## 사후 (자동)
6. **docs-manager**: CHANGELOG 업데이트
