<template>
  <div v-if="visible" class="prsc-overlay" @click.self="close">
    <div class="prsc-modal">

      <!-- 헤더 -->
      <div class="prsc-header">
        <div>
          <div class="prsc-header-sub">COMPOUND-EXPANSION SKILL v1.0</div>
          <div class="prsc-header-title">새 처방 생성</div>
        </div>
        <button class="prsc-close" @click="close">✕</button>
      </div>

      <!-- 입력 폼 (생성 전) -->
      <div v-if="phase === 'input'" class="prsc-body">
        <div class="prsc-form">

          <div class="prsc-field">
            <label class="prsc-label">제품명</label>
            <input
              v-model="form.product_name"
              class="prsc-input"
              placeholder="예) 선스틱 SPF50+ PA++++"
            />
          </div>

          <div class="prsc-field">
            <label class="prsc-label">제형 타입</label>
            <select v-model="form.product_type" class="prsc-input">
              <option value="">선택 (선택사항)</option>
              <option value="O/W 크림">O/W 크림</option>
              <option value="W/O 크림">W/O 크림</option>
              <option value="W/Si 에멀전">W/Si 에멀전</option>
              <option value="유화스틱">유화스틱</option>
              <option value="O/W 폼">O/W 폼</option>
              <option value="수성 세럼">수성 세럼</option>
              <option value="토너">토너</option>
              <option value="앰플">앰플</option>
              <option value="샴푸">샴푸</option>
              <option value="선크림">선크림</option>
            </select>
          </div>

          <div class="prsc-field-row">
            <div class="prsc-field">
              <label class="prsc-label">목표 pH <span class="prsc-opt">(선택)</span></label>
              <input v-model="form.target_pH" class="prsc-input" placeholder="예) 5.5" />
            </div>
            <div class="prsc-field">
              <label class="prsc-label">목표 점도 <span class="prsc-opt">(선택)</span></label>
              <input v-model="form.target_viscosity" class="prsc-input" placeholder="예) 5000 cP" />
            </div>
          </div>

          <div class="prsc-field">
            <label class="prsc-label">
              투입 성분 목록
              <span class="prsc-opt">— 쉼표(,) 구분 · 복합성분 상품명 포함 가능</span>
            </label>
            <textarea
              v-model="form.ingredients_list"
              class="prsc-textarea"
              rows="6"
              placeholder="예) Cyclopentasiloxane, Bentone Gel MIO, Dimethicone 5cSt, Butylene Glycol, Titanium Dioxide, Niacinamide, Aqua"
            ></textarea>
            <div class="prsc-hint">
              복합성분(Bentone Gel MIO, Sepigel 305 등) 입력 시 자동 전성분 전개 적용
            </div>
          </div>

          <div class="prsc-options">
            <label class="prsc-check">
              <input type="checkbox" v-model="form.use_gemini" /> Gemini 리서치 사용 (규제·TDS 자동 조회)
            </label>
            <label class="prsc-check">
              <input type="checkbox" v-model="form.use_cache" /> 캐시 사용 (동일 처방 재사용)
            </label>
          </div>
        </div>

        <div class="prsc-footer">
          <button class="prsc-btn-ghost" @click="close">취소</button>
          <button class="prsc-btn-primary" @click="generate" :disabled="!canGenerate">
            ⚗ 처방 생성
          </button>
        </div>
      </div>

      <!-- 스트리밍 중 -->
      <div v-if="phase === 'streaming'" class="prsc-body">
        <div class="prsc-stream-header">
          <div class="prsc-spinner"></div>
          <div>
            <div class="prsc-stream-title">AI 처방 생성 중...</div>
            <div class="prsc-stream-sub">복합성분 전개 · 정수 연산 · 규제 검토</div>
          </div>
          <div class="prsc-elapsed">{{ elapsed }}초</div>
        </div>
        <div class="prsc-stream-box" ref="streamBox">
          <pre class="prsc-stream-text">{{ streamText }}</pre>
          <span class="prsc-cursor">▌</span>
        </div>
        <div class="prsc-footer">
          <button class="prsc-btn-ghost" @click="abort">중단</button>
        </div>
      </div>

      <!-- 결과 -->
      <div v-if="phase === 'done'" class="prsc-body">

        <!-- 탭 -->
        <div class="prsc-tabs">
          <button
            v-for="t in tabs" :key="t.id"
            class="prsc-tab"
            :class="{ active: activeTab === t.id }"
            @click="activeTab = t.id"
          >{{ t.label }}</button>
        </div>

        <!-- 전체 처방 텍스트 -->
        <div v-if="activeTab === 'full'" class="prsc-result-box">
          <pre class="prsc-result-text">{{ streamText }}</pre>
        </div>

        <!-- 처방 요약 파싱 -->
        <div v-if="activeTab === 'summary'" class="prsc-summary">
          <div class="prsc-summary-item">
            <span class="prsc-summary-key">제품명</span>
            <span class="prsc-summary-val">{{ form.product_name }}</span>
          </div>
          <div class="prsc-summary-item">
            <span class="prsc-summary-key">제형</span>
            <span class="prsc-summary-val">{{ form.product_type || '—' }}</span>
          </div>
          <div class="prsc-summary-item">
            <span class="prsc-summary-key">소요 시간</span>
            <span class="prsc-summary-val">{{ elapsed }}초</span>
          </div>
          <div class="prsc-summary-item">
            <span class="prsc-summary-key">투입 성분 수</span>
            <span class="prsc-summary-val">{{ ingredientCount }}개</span>
          </div>
        </div>

        <div class="prsc-footer">
          <button class="prsc-btn-ghost" @click="reset">다시 작성</button>
          <button class="prsc-btn-primary" @click="confirm">처방 목록에 추가</button>
        </div>
      </div>

      <!-- 오류 -->
      <div v-if="phase === 'error'" class="prsc-body">
        <div class="prsc-error">
          <div class="prsc-error-icon">⚠</div>
          <div class="prsc-error-msg">{{ errorMsg }}</div>
        </div>
        <div class="prsc-footer">
          <button class="prsc-btn-ghost" @click="reset">다시 시도</button>
          <button class="prsc-btn-ghost" @click="close">닫기</button>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { getAiPrscStream } from '@/api/coching/mylab/mylab';

export default {
  name: 'PrescriptionModal',

  data() {
    return {
      visible: false,
      phase: 'input',   // input | streaming | done | error
      activeTab: 'full',
      tabs: [
        { id: 'full',    label: '전체 처방서' },
        { id: 'summary', label: '요약' },
      ],
      form: {
        product_name: '',
        product_type: '',
        ingredients_list: '',
        target_pH: '',
        target_viscosity: '',
        use_gemini: true,
        use_cache: true,
      },
      streamText: '',
      errorMsg: '',
      elapsed: 0,
      _timer: null,
      _controller: null,
    };
  },

  computed: {
    canGenerate() {
      return this.form.product_name.trim() && this.form.ingredients_list.trim();
    },
    ingredientCount() {
      return this.form.ingredients_list.split(',').filter(s => s.trim()).length;
    },
  },

  methods: {
    open() {
      this.visible = true;
      this.reset();
    },

    close() {
      this.abort();
      this.visible = false;
    },

    reset() {
      this.phase = 'input';
      this.streamText = '';
      this.errorMsg = '';
      this.elapsed = 0;
      this.activeTab = 'full';
      clearInterval(this._timer);
    },

    abort() {
      if (this._controller) {
        this._controller.abort();
        this._controller = null;
      }
      clearInterval(this._timer);
    },

    generate() {
      if (!this.canGenerate) return;

      this.phase = 'streaming';
      this.streamText = '';
      this.elapsed = 0;

      // 경과 타이머
      this._timer = setInterval(() => { this.elapsed++; }, 1000);

      // 파라미터 구성
      const params = {
        product_name:     this.form.product_name.trim(),
        ingredients_list: this.form.ingredients_list.trim(),
        use_gemini:       this.form.use_gemini,
        use_cache:        this.form.use_cache,
      };
      if (this.form.product_type)     params.product_type     = this.form.product_type;
      if (this.form.target_pH)        params.target_pH        = this.form.target_pH;
      if (this.form.target_viscosity) params.target_viscosity = this.form.target_viscosity;

      const _vm = this;

      this._controller = getAiPrscStream(
        params,
        // onChunk
        function(data) {
          if (data && data.text) {
            _vm.streamText += data.text;
            _vm.$nextTick(() => {
              const box = _vm.$refs.streamBox;
              if (box) box.scrollTop = box.scrollHeight;
            });
          }
        },
        // onDone
        function() {
          clearInterval(_vm._timer);
          _vm.phase = 'done';
        },
        // onError
        function(err) {
          clearInterval(_vm._timer);
          _vm.phase = 'error';
          _vm.errorMsg = err ? err.message : 'AI 서버 연결 오류. localhost:8420 상태를 확인하세요.';
        }
      );
    },

    confirm() {
      this.$emit('onPrescriptionDone', {
        id:         'F-' + String(Math.floor(Math.random() * 900) + 100),
        name:       this.form.product_name,
        ver:        'v1.0',
        type:       this.form.product_type || '—',
        ph:         this.form.target_pH    || '—',
        visc:       '—',
        status:     '처방설계',
        progress:   10,
        pColor:     '#7c5cbf',
        fullText:   this.streamText,
      });
      this.close();
    },
  },
};
</script>

<style lang="scss" scoped>
/* ═══════════════════════════════════════
   PrescriptionModal — Pretendard + WL tokens
═══════════════════════════════════════ */

/* ── 오버레이 ── */
.prsc-overlay {
  position: fixed;
  inset: 0;
  background: rgba(26, 24, 20, 0.52);
  backdrop-filter: blur(2px);
  z-index: 9000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.prsc-modal {
  background: var(--wl-surface);
  border-radius: 14px;
  width: 700px;
  max-width: 96vw;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 64px rgba(0,0,0,0.16), 0 4px 16px rgba(0,0,0,0.08);
  overflow: hidden;
  font-family: var(--wl-font);
  -webkit-font-smoothing: antialiased;
}

/* ── 헤더 ── */
.prsc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 26px 18px;
  border-bottom: 1px solid var(--wl-border);
  background: var(--wl-surface);

  .prsc-header-sub {
    font-size: 10px;
    color: var(--wl-accent);
    letter-spacing: 2px;
    font-family: var(--wl-mono);
    margin-bottom: 5px;
    font-weight: 500;
  }

  .prsc-header-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--wl-text);
    letter-spacing: -0.3px;
  }
}

.prsc-close {
  background: none;
  border: 1px solid transparent;
  font-size: 15px;
  color: var(--wl-text-dim);
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 6px;
  line-height: 1;
  transition: all 0.15s;

  &:hover {
    background: var(--wl-bg);
    border-color: var(--wl-border);
    color: var(--wl-text);
  }
}

/* ── 바디 ── */
.prsc-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

/* ── 폼 ── */
.prsc-form {
  padding: 22px 26px;
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.prsc-field {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.prsc-field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.prsc-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--wl-text);
  letter-spacing: 0.1px;
}

.prsc-opt {
  font-weight: 400;
  color: var(--wl-text-dim);
  font-size: 11px;
}

.prsc-input,
.prsc-textarea {
  border: 1.5px solid var(--wl-border);
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 13px;
  color: var(--wl-text);
  font-family: var(--wl-font);
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
  background: var(--wl-bg);
  resize: none;

  &:focus {
    border-color: var(--wl-accent);
    background: var(--wl-surface);
    box-shadow: 0 0 0 3px rgba(184, 147, 90, 0.13);
  }
  &::placeholder { color: var(--wl-text-dim); }
}

.prsc-hint {
  font-size: 11px;
  color: var(--wl-text-dim);
  margin-top: 2px;
  line-height: 1.5;
}

/* ── 체크박스 (세로 배치) ── */
.prsc-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 14px 16px;
  background: var(--wl-bg);
  border-radius: 8px;
  border: 1px solid var(--wl-border);
}

.prsc-check {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--wl-text-sub);
  cursor: pointer;
  line-height: 1.4;

  input {
    accent-color: var(--wl-accent);
    width: 15px;
    height: 15px;
    flex-shrink: 0;
  }
}

/* ── 스트리밍 ── */
.prsc-stream-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 26px;
  border-bottom: 1px solid var(--wl-border);
  background: var(--wl-bg);

  .prsc-stream-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--wl-text);
  }

  .prsc-stream-sub {
    font-size: 11px;
    color: var(--wl-text-dim);
    margin-top: 2px;
    font-family: var(--wl-mono);
  }
}

.prsc-elapsed {
  margin-left: auto;
  font-family: var(--wl-mono);
  font-size: 20px;
  font-weight: 700;
  color: var(--wl-accent);
  min-width: 52px;
  text-align: right;
}

.prsc-spinner {
  width: 22px;
  height: 22px;
  border: 2.5px solid var(--wl-border);
  border-top-color: var(--wl-accent);
  border-radius: 50%;
  animation: spin 0.75s linear infinite;
  flex-shrink: 0;
}

@keyframes spin { to { transform: rotate(360deg); } }

.prsc-stream-box {
  flex: 1;
  overflow-y: auto;
  padding: 18px 26px;
  background: var(--wl-bg);
  min-height: 280px;
  max-height: 360px;
}

.prsc-stream-text {
  font-family: var(--wl-mono);
  font-size: 12px;
  color: var(--wl-text);
  line-height: 1.75;
  white-space: pre-wrap;
  margin: 0;
}

.prsc-cursor {
  display: inline-block;
  color: var(--wl-accent);
  animation: blink 1s step-end infinite;
}

@keyframes blink { 50% { opacity: 0; } }

/* ── 결과 탭 ── */
.prsc-tabs {
  display: flex;
  border-bottom: 1px solid var(--wl-border);
  padding: 0 26px;
  background: var(--wl-bg);
}

.prsc-tab {
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  padding: 13px 18px 11px;
  font-size: 13px;
  color: var(--wl-text-dim);
  cursor: pointer;
  font-family: var(--wl-font);
  font-weight: 500;
  transition: all 0.15s;

  &.active {
    color: var(--wl-accent);
    border-bottom-color: var(--wl-accent);
    font-weight: 600;
  }

  &:hover:not(.active) { color: var(--wl-text-sub); }
}

.prsc-result-box {
  flex: 1;
  overflow-y: auto;
  padding: 18px 26px;
  max-height: 360px;
}

.prsc-result-text {
  font-family: var(--wl-mono);
  font-size: 12px;
  color: var(--wl-text);
  line-height: 1.75;
  white-space: pre-wrap;
  margin: 0;
}

/* ── 요약 ── */
.prsc-summary {
  padding: 22px 26px;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.prsc-summary-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 13px 0;
  border-bottom: 1px solid var(--wl-border);

  &:last-child { border-bottom: none; }
}

.prsc-summary-key {
  font-size: 11px;
  color: var(--wl-text-dim);
  font-family: var(--wl-mono);
  letter-spacing: 0.8px;
  width: 110px;
  flex-shrink: 0;
}

.prsc-summary-val {
  font-size: 14px;
  color: var(--wl-text);
  font-weight: 500;
}

/* ── 오류 ── */
.prsc-error {
  padding: 40px 26px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;

  .prsc-error-icon {
    font-size: 32px;
    color: var(--wl-red);
  }

  .prsc-error-msg {
    font-size: 13px;
    color: var(--wl-text-sub);
    text-align: center;
    line-height: 1.65;
    max-width: 380px;
  }
}

/* ── 푸터 ── */
.prsc-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px 26px;
  border-top: 1px solid var(--wl-border);
  background: var(--wl-surface);
}

.prsc-btn-primary {
  background: var(--wl-accent);
  color: #fff;
  border: none;
  border-radius: 7px;
  padding: 10px 22px;
  font-size: 13px;
  font-weight: 600;
  font-family: var(--wl-font);
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(184,147,90,0.25);
  transition: background 0.15s, box-shadow 0.15s;

  &:disabled {
    opacity: 0.38;
    cursor: not-allowed;
    box-shadow: none;
  }
  &:not(:disabled):hover {
    background: #a67d47;
    box-shadow: 0 4px 16px rgba(184,147,90,0.38);
  }
}

.prsc-btn-ghost {
  background: none;
  border: 1.5px solid var(--wl-border-mid);
  border-radius: 7px;
  padding: 10px 18px;
  font-size: 13px;
  font-weight: 500;
  color: var(--wl-text-sub);
  cursor: pointer;
  font-family: var(--wl-font);
  transition: all 0.15s;

  &:hover {
    border-color: var(--wl-accent);
    color: var(--wl-accent);
    background: var(--wl-accent-lt);
  }
}
</style>
