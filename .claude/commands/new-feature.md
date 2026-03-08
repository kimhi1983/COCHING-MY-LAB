# 새 기능 개발 파이프라인

$ARGUMENTS 기능을 개발합니다.

## 실행 순서

1. **explorer** 에이전트로 관련 기존 코드 탐색 (백그라운드)
2. **pm** 에이전트로 영향도 분석 + 작업 분해
3. **architect** 에이전트로 설계 문서 생성
4. **ux-designer** 에이전트로 디자인 검토 (UI 포함 시)
5. 설계 리뷰 후 승인하면 병렬 실행:
   - **backend-dev**: API 구현 (worktree 격리)
   - **frontend-dev**: UI 구현 (worktree 격리)
6. Triple Quality Gate:
   - **qa-tester**: 코드 품질 + 테스트 (백그라운드)
   - **security-reviewer**: 보안 감사 (백그라운드)
   - **architect**: 설계 적합성 최종 검증
7. **docs-manager**로 문서 업데이트 (백그라운드)
