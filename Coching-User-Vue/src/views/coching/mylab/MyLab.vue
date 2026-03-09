<template>
  <section>
    <div class="container h-100">
      <div class="content">
        <div class="common-content">

          <div class="mylab-page-title">
            <span class="mylab-icon">🧪</span>
            MY LAB
            <span class="mylab-subtitle">AI 화장품 처방 생성</span>
          </div>

          <!-- ═══════════ 2-PANEL LAYOUT ═══════════ -->
          <div class="mylab-wrap">

            <!-- ══ LEFT PANEL ══ -->
            <div class="mylab-left">
              <div class="mylab-left-header">
                <span class="mylab-left-title">처방 목록</span>
                <button class="btn-new-lab" @click="onNewLab">+ 새 처방</button>
              </div>

              <div class="mylab-left-search">
                <div class="search-box">
                  <input
                    type="text"
                    v-model="sc.keyword"
                    placeholder="처방명 검색..."
                    @keyup.enter="loadList"
                    class="search-input"
                  />
                  <button class="search-btn" @click="loadList">검색</button>
                </div>
              </div>

              <div class="mylab-item-list" ref="labItemList">
                <div
                  v-for="item in labList"
                  :key="item.labMstSeq"
                  class="mylab-item"
                  :class="{ active: selectedSeq === item.labMstSeq }"
                  @click="selectLab(item)"
                >
                  <div class="lab-item-name">{{ item.title || '(이름 없음)' }}</div>
                  <div class="lab-item-meta">
                    <span class="lab-item-cate">{{ getCateLabel(item) }}</span>
                    <span class="lab-item-cnt">⚡ {{ item.aiLabResCnt || 0 }}</span>
                  </div>
                  <div class="lab-item-date">{{ item.chngDttm | eufDateFmt }}</div>
                </div>

                <div v-if="!isListLoading && labList.length === 0" class="lab-empty-list">
                  <div class="empty-icon">📋</div>
                  <p>처방 없음</p>
                </div>
                <div v-if="isListLoading" class="lab-loading">
                  <span class="loading-dot">●</span>
                  <span class="loading-dot">●</span>
                  <span class="loading-dot">●</span>
                </div>
              </div>
            </div>

            <!-- ══ RIGHT PANEL ══ -->
            <div class="mylab-right">

              <!-- Empty State -->
              <div v-if="!selectedSeq && !isNew" class="lab-empty-state">
                <div class="empty-state-icon">🧪</div>
                <h5>처방을 선택하거나 새로 만드세요</h5>
                <p>왼쪽 목록에서 처방을 선택하거나<br>새 처방을 만들어 AI 분석을 받아보세요</p>
                <button class="btn-primary-lg" @click="onNewLab">+ 새 처방 만들기</button>
              </div>

              <!-- 처방 폼 -->
              <div v-else class="lab-form-wrap">

                <!-- 상단 헤더 -->
                <div class="lab-form-header">
                  <div class="lab-title-wrap">
                    <input
                      type="text"
                      v-model="form.title"
                      placeholder="처방명을 입력하세요"
                      class="lab-title-input"
                      :readonly="!canEdit"
                    />
                  </div>
                  <div class="lab-form-actions">
                    <button v-if="canEdit" class="btn-outline" @click="onCancelEdit">취소</button>
                    <button v-if="canEdit" class="btn-success" :disabled="isSaving" @click="onSave">
                      {{ isSaving ? '저장 중...' : '저장' }}
                    </button>
                    <button v-if="!isNew && !canEdit" class="btn-outline-primary" @click="canEdit = true">✏ 수정</button>
                    <button v-if="!isNew" class="btn-outline-danger" @click="onDelete">🗑</button>
                  </div>
                </div>

                <!-- 카테고리 + 요구사항 -->
                <div class="lab-form-meta">
                  <div class="meta-row">
                    <div class="meta-col-left">
                      <select
                        v-model="form.prodCateGroup"
                        :disabled="!canEdit"
                        class="form-select mb-4"
                        @change="onChangeProdCateGroup"
                      >
                        <option value="">제품 대분류</option>
                        <option v-for="g in CODES.grpList" :key="g.etc3" :value="g.etc3">{{ g.name }}</option>
                      </select>
                      <select
                        v-model="form.prodCateCode"
                        :disabled="!canEdit"
                        class="form-select"
                      >
                        <option value="">제품 소분류</option>
                        <option
                          v-for="c in (CODES['CATE' + form.prodCateGroup] || [])"
                          :key="c.etc3"
                          :value="c.etc3"
                        >{{ c.name }}</option>
                      </select>
                    </div>
                    <div class="meta-col-right">
                      <textarea
                        v-model="form.reqContent"
                        rows="3"
                        :readonly="!canEdit"
                        placeholder="처방 요구사항 (예: 미백 효과가 있는 수분크림)"
                        class="form-textarea"
                      ></textarea>
                    </div>
                  </div>
                </div>

                <!-- 성분 + AI 패널 -->
                <div class="lab-form-body">

                  <!-- 전성분 입력 -->
                  <div class="ingredient-panel">
                    <div class="panel-head">
                      <span>전성분</span>
                      <span class="cnt-badge">{{ ingredientCount }}개</span>
                    </div>
                    <textarea
                      v-model="form.ingredientsRaw"
                      :readonly="!canEdit"
                      placeholder="성분을 쉼표(,) 또는 줄바꿈으로 구분하여 입력하세요.&#10;&#10;예)&#10;Water&#10;Niacinamide&#10;Glycerin&#10;Butylene Glycol"
                      class="ingredient-textarea"
                    ></textarea>
                  </div>

                  <!-- AI 처방 옵션 -->
                  <div class="ai-panel">
                    <div class="panel-head">
                      <span>AI 처방</span>
                      <span v-if="lastResult" class="badge-success-sm">결과 있음</span>
                    </div>

                    <div class="ai-opt-group">
                      <label class="opt-label">캐시</label>
                      <div class="opt-row">
                        <label class="radio-label">
                          <input type="radio" v-model="aiOpts.useCache" :value="true" /> 사용
                        </label>
                        <label class="radio-label">
                          <input type="radio" v-model="aiOpts.useCache" :value="false" /> 새로
                        </label>
                      </div>
                    </div>

                    <div class="ai-opt-group">
                      <label class="opt-label">Gemini 리서치</label>
                      <label class="toggle-switch">
                        <input type="checkbox" v-model="aiOpts.useGemini" />
                        <span class="toggle-slider"></span>
                        <span class="toggle-text">{{ aiOpts.useGemini ? '활성' : '비활성' }}</span>
                      </label>
                    </div>

                    <button
                      class="btn-ai-run"
                      :disabled="isRunning || !hasIngredients"
                      @click="onRunAi"
                    >
                      <span v-if="isRunning" class="running-icon">⚡</span>
                      <span v-else>⚡</span>
                      {{ isRunning ? '생성 중... (' + elapsedSec + 's)' : 'AI 처방 생성' }}
                    </button>

                    <button
                      v-if="isRunning"
                      class="btn-stop"
                      @click="onAbortAi"
                    >■ 중지</button>

                    <div v-if="lastMeta" class="ai-meta-box">
                      <div class="ai-meta-item">
                        <span>처리시간</span>
                        <strong>{{ lastMeta.processing_time }}s</strong>
                      </div>
                      <div class="ai-meta-item">
                        <span>모델</span>
                        <strong class="meta-model">{{ lastMeta.model }}</strong>
                      </div>
                      <div class="ai-meta-item">
                        <span>복잡도</span>
                        <span :class="'complexity-' + lastMeta.complexity">{{ lastMeta.complexity }}</span>
                      </div>
                      <div class="ai-meta-item">
                        <span>캐시</span>
                        <span :class="lastMeta.from_cache ? 'text-success' : 'text-muted'">
                          {{ lastMeta.from_cache ? '✓ 히트' : '미스' }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 결과 영역 -->
                <div v-if="streamText || lastResult" class="lab-result-wrap">
                  <div class="panel-head mb-8">
                    <span>AI 처방 결과</span>
                    <div class="result-actions">
                      <button v-if="lastResult" class="btn-sm-outline" @click="onCopyResult">📋 복사</button>
                    </div>
                  </div>

                  <div v-if="isRunning || (streamText && !lastResult)" class="stream-box" ref="streamBox">
                    <div v-html="streamHtml"></div>
                  </div>

                  <div v-if="lastResult && !isRunning" class="result-box">
                    <div
                      class="result-section"
                      v-for="(section, idx) in parsedResult"
                      :key="idx"
                    >
                      <h6 class="result-section-title">{{ section.title }}</h6>
                      <div class="result-section-body" v-html="section.html"></div>
                    </div>
                  </div>
                </div>

              </div>
            </div>
          </div>

        </div>
      </div>

      <AlertModal ref="alertModal"></AlertModal>

    </div>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { PROD_CATES } from '@/constants/testCode';
import {
  getLabMasterList,
  getLabMasterDetail,
  addLabMaster,
  setLabMaster,
  deleteLabMaster,
  getAiPrscStream,
} from '@/api/coching/mylab/mylab';
import _ from 'lodash';

export default {
  name: 'MyLab',
  mixins: [ernsUtils],

  data() {
    return {
      labList: [],
      isListLoading: false,
      sc: { keyword: '' },

      selectedSeq: null,
      isNew: false,
      canEdit: false,

      form: {
        title: '',
        prodCateGroup: '',
        prodCateCode: '',
        reqContent: '',
        ingredientsRaw: '',
      },
      formOriginal: null,

      CODES: { grpList: [], list: [] },
      isSaving: false,

      aiOpts: {
        useCache: true,
        useGemini: true,
      },
      isRunning: false,
      streamController: null,
      streamText: '',
      lastResult: null,
      lastMeta: null,
      elapsedSec: 0,
      elapsedTimer: null,
    };
  },

  computed: {
    ingredientCount() {
      if (!this.form.ingredientsRaw) return 0;
      return this.form.ingredientsRaw
        .split(/[,\n]/)
        .map(s => s.trim())
        .filter(Boolean).length;
    },
    hasIngredients() {
      return this.ingredientCount > 0;
    },
    streamHtml() {
      return this.streamText.replace(/\n/g, '<br>') + '<span class="blink-cursor">|</span>';
    },
    parsedResult() {
      if (!this.lastResult) return [];
      var text = typeof this.lastResult === 'string'
        ? this.lastResult
        : JSON.stringify(this.lastResult, null, 2);
      var sections = [];
      var lines = text.split('\n');
      var current = { title: '처방 결과', lines: [] };
      lines.forEach(function(line) {
        if (line.startsWith('## ')) {
          if (current.lines.length) sections.push(current);
          current = { title: line.replace('## ', ''), lines: [] };
        } else if (line.startsWith('# ')) {
          if (current.lines.length) sections.push(current);
          current = { title: line.replace('# ', ''), lines: [] };
        } else {
          current.lines.push(line);
        }
      });
      if (current.lines.length) sections.push(current);
      return sections.map(function(s) {
        return {
          title: s.title,
          html: s.lines.join('\n')
            .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
            .replace(/\n/g, '<br>'),
        };
      });
    },
  },

  filters: {
    eufDateFmt(val) {
      if (!val) return '';
      return String(val).slice(0, 10);
    },
  },

  mounted() {
    this.loadCodes();
    this.loadList();
  },

  beforeDestroy() {
    this.onAbortAi();
    clearInterval(this.elapsedTimer);
  },

  methods: {
    loadCodes() {
      var list = PROD_CATES.filter(function(cd) { return cd.name !== '전체' && cd.code !== 2; });
      list = _.sortBy(list, 'sortOrder');
      this.CODES.list = list;
      this.CODES.grpList = list.filter(function(cd) { return cd.etc1 == 1; });
      var self = this;
      this.CODES.grpList.forEach(function(g) {
        self.CODES['CATE' + g.etc3] = list.filter(function(cd) {
          return cd.etc3.indexOf(g.etc3) === 0 && cd.etc3 !== g.etc3;
        });
      });
    },

    getCateLabel(item) {
      var g = this.CODES.list.find(function(c) { return c.etc3 == item.prodCateGroup; });
      var d = this.CODES.list.find(function(c) { return c.etc3 == item.prodCateCode; });
      if (g && d) return g.name + ' > ' + d.name;
      if (g) return g.name;
      return '-';
    },

    onChangeProdCateGroup() {
      this.form.prodCateCode = '';
    },

    async loadList() {
      this.isListLoading = true;
      try {
        var res = await getLabMasterList({
          page: 1,
          rowSize: 100,
          titleL: this.sc.keyword || undefined,
        });
        this.labList = (res.resultData && res.resultData.list) || [];
      } catch (e) {
        this.alertError(e);
      } finally {
        this.isListLoading = false;
      }
    },

    async selectLab(item) {
      if (this.isRunning) return;
      this.isNew = false;
      this.canEdit = false;
      this.clearResult();
      this.selectedSeq = item.labMstSeq;
      try {
        this.loading(true);
        var res = await getLabMasterDetail({ labMstSeq: item.labMstSeq });
        var d = res.resultData;
        this.form = {
          title: d.title || '',
          prodCateGroup: d.prodCateGroup || '',
          prodCateCode: d.prodCateCode || '',
          reqContent: d.reqContent || '',
          ingredientsRaw: (d.ingredients || []).map(function(i) { return i.ingredientName; }).join('\n'),
        };
        this.formOriginal = Object.assign({}, this.form);
      } catch (e) {
        this.alertError(e);
      } finally {
        this.loading(false);
      }
    },

    onNewLab() {
      if (this.isRunning) return;
      this.isNew = true;
      this.canEdit = true;
      this.selectedSeq = null;
      this.clearResult();
      this.form = { title: '', prodCateGroup: '', prodCateCode: '', reqContent: '', ingredientsRaw: '' };
    },

    onCancelEdit() {
      if (this.isNew) {
        this.isNew = false;
        this.selectedSeq = null;
      } else {
        this.form = Object.assign({}, this.formOriginal);
        this.canEdit = false;
      }
    },

    async onSave() {
      if (!this.form.title.trim()) {
        this.alertError('처방명을 입력하세요.');
        return;
      }
      this.isSaving = true;
      try {
        var payload = {
          title: this.form.title,
          prodCateGroup: this.form.prodCateGroup,
          prodCateCode: this.form.prodCateCode,
          reqContent: this.form.reqContent,
          ingredients: this.form.ingredientsRaw
            .split(/[,\n]/).map(function(s) { return s.trim(); }).filter(Boolean)
            .map(function(name, idx) { return { ingredientName: name, sortOrder: idx + 1 }; }),
        };
        if (this.isNew) {
          await addLabMaster(payload);
          this.alertSuccess('처방이 등록되었습니다.');
        } else {
          await setLabMaster(Object.assign({}, payload, { labMstSeq: this.selectedSeq }));
          this.alertSuccess('처방이 수정되었습니다.');
        }
        this.isNew = false;
        this.canEdit = false;
        await this.loadList();
      } catch (e) {
        this.alertError(e);
      } finally {
        this.isSaving = false;
      }
    },

    async onDelete() {
      var ok = await this.$swal({
        title: '삭제',
        text: '처방을 삭제하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        customClass: {
          confirmButton: 'swal-btn-danger',
          cancelButton: 'swal-btn-cancel',
        },
        buttonsStyling: false,
      });
      if (!ok.value) return;
      this.loading(true);
      try {
        await deleteLabMaster({ labMstSeq: this.selectedSeq });
        this.alertSuccess('삭제되었습니다.');
        this.selectedSeq = null;
        this.isNew = false;
        this.clearResult();
        await this.loadList();
      } catch (e) {
        this.alertError(e);
      } finally {
        this.loading(false);
      }
    },

    onRunAi() {
      if (!this.hasIngredients) return;
      this.clearResult();
      this.isRunning = true;
      this.elapsedSec = 0;
      var self = this;
      this.elapsedTimer = setInterval(function() { self.elapsedSec++; }, 1000);

      var ingredientsList = this.form.ingredientsRaw
        .split(/[,\n]/).map(function(s) { return s.trim(); }).filter(Boolean).join(', ');

      var payload = {
        product_name: this.form.title || '화장품',
        ingredients_list: ingredientsList,
        product_type: this.getCateLabel({
          prodCateGroup: this.form.prodCateGroup,
          prodCateCode: this.form.prodCateCode,
        }),
        use_gemini: this.aiOpts.useGemini,
        use_cache: this.aiOpts.useCache,
      };

      this.streamController = getAiPrscStream(
        payload,
        function(chunk) {
          if (chunk.type === 'content') {
            self.streamText += chunk.text || '';
            self.$nextTick(function() {
              if (self.$refs.streamBox) {
                self.$refs.streamBox.scrollTop = self.$refs.streamBox.scrollHeight;
              }
            });
          } else if (chunk.type === 'result') {
            self.lastResult = chunk.result_data || chunk.data || self.streamText;
            self.lastMeta = {
              processing_time: chunk.processing_time,
              model: chunk.model,
              complexity: chunk.complexity,
              from_cache: chunk.from_cache,
            };
          }
        },
        function() { self.onAiDone(); },
        function(err) { self.alertError(err); self.onAiDone(); }
      );
    },

    onAiDone() {
      this.isRunning = false;
      clearInterval(this.elapsedTimer);
      if (!this.lastResult && this.streamText) {
        this.lastResult = this.streamText;
      }
    },

    onAbortAi() {
      if (this.streamController) {
        this.streamController.abort();
        this.streamController = null;
      }
      this.onAiDone();
    },

    clearResult() {
      this.streamText = '';
      this.lastResult = null;
      this.lastMeta = null;
      this.elapsedSec = 0;
      clearInterval(this.elapsedTimer);
    },

    onCopyResult() {
      var text = typeof this.lastResult === 'string'
        ? this.lastResult
        : JSON.stringify(this.lastResult, null, 2);
      navigator.clipboard.writeText(text).then(function() {
        alert('클립보드에 복사되었습니다.');
      });
    },
  },
};
</script>

<style lang="scss" scoped>
/* ── Page Title ── */
.mylab-page-title {
  font-size: 20px;
  font-weight: 700;
  color: #222;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;

  .mylab-icon { font-size: 22px; }
  .mylab-subtitle {
    font-size: 13px;
    font-weight: 400;
    color: #888;
    margin-left: 4px;
  }
}

/* ── 2-Panel Wrap ── */
.mylab-wrap {
  display: flex;
  height: calc(100vh - 200px);
  min-height: 600px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

/* ── LEFT PANEL ── */
.mylab-left {
  width: 270px;
  min-width: 270px;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.mylab-left-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 14px 10px;
  border-bottom: 1px solid #ebebeb;
  background: #fff;
}

.mylab-left-title {
  font-size: 14px;
  font-weight: 700;
  color: #333;
}

.btn-new-lab {
  padding: 5px 10px;
  font-size: 12px;
  font-weight: 600;
  background: #2c5ff6;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.15s;
  &:hover { background: #1a4de6; }
}

.mylab-left-search {
  padding: 8px 10px;
  border-bottom: 1px solid #f0f0f0;
}

.search-box {
  display: flex;
  gap: 4px;
}

.search-input {
  flex: 1;
  padding: 5px 8px;
  font-size: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  outline: none;
  &:focus { border-color: #2c5ff6; }
}

.search-btn {
  padding: 5px 10px;
  font-size: 12px;
  background: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  &:hover { background: #e4e4e4; }
}

.mylab-item-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

.mylab-item {
  padding: 10px 14px;
  cursor: pointer;
  border-bottom: 1px solid #f2f2f2;
  transition: background 0.12s;

  &:hover { background: #eef2ff; }
  &.active {
    background: #e8edff;
    border-left: 3px solid #2c5ff6;
    padding-left: 11px;
  }

  .lab-item-name {
    font-size: 13px;
    font-weight: 600;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .lab-item-meta {
    display: flex;
    justify-content: space-between;
    margin-top: 3px;

    .lab-item-cate {
      font-size: 11px;
      color: #888;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 160px;
    }

    .lab-item-cnt {
      font-size: 11px;
      color: #2c5ff6;
    }
  }

  .lab-item-date {
    font-size: 10px;
    color: #bbb;
    margin-top: 2px;
  }
}

.lab-empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 10px;
  color: #ccc;
  font-size: 12px;
  text-align: center;
  .empty-icon { font-size: 32px; margin-bottom: 8px; }
  p { margin: 0; }
}

.lab-loading {
  display: flex;
  justify-content: center;
  gap: 4px;
  padding: 20px;

  .loading-dot {
    color: #aaa;
    font-size: 8px;
    animation: pulse 1.2s infinite;
    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

/* ── RIGHT PANEL ── */
.mylab-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.lab-empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #aaa;
  text-align: center;
  padding: 40px;

  .empty-state-icon { font-size: 52px; margin-bottom: 4px; }
  h5 { color: #555; font-size: 18px; margin: 0; }
  p { font-size: 13px; color: #999; line-height: 1.6; margin: 0; }
}

.btn-primary-lg {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  background: #2c5ff6;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 8px;
  &:hover { background: #1a4de6; }
}

.lab-form-wrap {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

/* 헤더 */
.lab-form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
  gap: 12px;
}

.lab-title-wrap { flex: 1; }

.lab-title-input {
  width: 100%;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-bottom: 2px solid #e0e0e0;
  padding: 3px 0;
  outline: none;
  background: transparent;
  &:focus { border-bottom-color: #2c5ff6; }
  &[readonly] { cursor: default; }
}

.lab-form-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.btn-outline {
  padding: 5px 12px;
  font-size: 12px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  &:hover { background: #f5f5f5; }
}

.btn-success {
  padding: 5px 14px;
  font-size: 12px;
  font-weight: 600;
  background: #28a745;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  &:hover { background: #218838; }
  &:disabled { opacity: 0.65; cursor: not-allowed; }
}

.btn-outline-primary {
  padding: 5px 12px;
  font-size: 12px;
  background: #fff;
  color: #2c5ff6;
  border: 1px solid #2c5ff6;
  border-radius: 5px;
  cursor: pointer;
  &:hover { background: #eef2ff; }
}

.btn-outline-danger {
  padding: 5px 10px;
  font-size: 12px;
  background: #fff;
  color: #dc3545;
  border: 1px solid #dc3545;
  border-radius: 5px;
  cursor: pointer;
  &:hover { background: #fff5f5; }
}

/* 메타 */
.lab-form-meta {
  padding: 10px 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.meta-row {
  display: flex;
  gap: 12px;
}

.meta-col-left {
  width: 180px;
  flex-shrink: 0;
}

.meta-col-right { flex: 1; }

.form-select {
  width: 100%;
  padding: 5px 8px;
  font-size: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  outline: none;
  background: #fff;
  &:focus { border-color: #2c5ff6; }
  &:disabled { background: #f8f8f8; color: #aaa; }
}

.mb-4 { margin-bottom: 4px; }

.form-textarea {
  width: 100%;
  padding: 6px 8px;
  font-size: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  outline: none;
  resize: vertical;
  font-family: inherit;
  &:focus { border-color: #2c5ff6; }
  &[readonly] { background: #f8f8f8; cursor: default; }
}

/* 바디 */
.lab-form-body {
  display: flex;
  flex: 1;
  overflow: hidden;
  min-height: 0;
}

.ingredient-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px 16px;
  border-right: 1px solid #f0f0f0;
  overflow: hidden;
  min-width: 0;
}

.ingredient-textarea {
  flex: 1;
  resize: none;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 8px;
  outline: none;
  line-height: 1.6;
  &:focus { border-color: #2c5ff6; }
  &[readonly] { background: #f8f8f8; cursor: default; }
}

.ai-panel {
  width: 220px;
  min-width: 220px;
  padding: 10px 14px;
  overflow-y: auto;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  font-weight: 700;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cnt-badge {
  font-size: 11px;
  color: #2c5ff6;
  font-weight: 600;
}

.badge-success-sm {
  font-size: 10px;
  padding: 2px 6px;
  background: #d4edda;
  color: #155724;
  border-radius: 10px;
}

/* AI 옵션 */
.ai-opt-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.opt-label {
  font-size: 11px;
  font-weight: 600;
  color: #666;
}

.opt-row {
  display: flex;
  gap: 12px;
}

.radio-label {
  font-size: 12px;
  color: #444;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  input { cursor: pointer; }
}

/* Toggle */
.toggle-switch {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;

  input { display: none; }

  .toggle-slider {
    width: 34px;
    height: 18px;
    background: #ddd;
    border-radius: 9px;
    position: relative;
    transition: background 0.2s;

    &::after {
      content: '';
      position: absolute;
      width: 14px;
      height: 14px;
      background: #fff;
      border-radius: 50%;
      top: 2px;
      left: 2px;
      transition: left 0.2s;
    }
  }

  input:checked + .toggle-slider {
    background: #2c5ff6;
    &::after { left: 18px; }
  }

  .toggle-text { font-size: 12px; color: #444; }
}

/* AI 실행 버튼 */
.btn-ai-run {
  width: 100%;
  padding: 10px;
  font-size: 13px;
  font-weight: 700;
  background: linear-gradient(135deg, #2c5ff6, #7367f0);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: opacity 0.15s;
  &:hover:not(:disabled) { opacity: 0.9; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.btn-stop {
  width: 100%;
  padding: 6px;
  font-size: 12px;
  background: #fff;
  color: #dc3545;
  border: 1px solid #dc3545;
  border-radius: 6px;
  cursor: pointer;
  &:hover { background: #fff5f5; }
}

/* AI 메타 */
.ai-meta-box {
  background: #f0f0f0;
  border-radius: 6px;
  padding: 8px 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ai-meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: #555;

  strong { font-size: 11px; color: #333; }
  .meta-model { font-size: 10px; word-break: break-all; }
}

.complexity-simple { color: #28a745; font-weight: 600; font-size: 11px; }
.complexity-normal  { color: #fd7e14; font-weight: 600; font-size: 11px; }
.complexity-complex { color: #dc3545; font-weight: 600; font-size: 11px; }
.text-success { color: #28a745; font-size: 11px; }
.text-muted   { color: #999; font-size: 11px; }

/* 결과 영역 */
.lab-result-wrap {
  flex-shrink: 0;
  max-height: 40%;
  overflow-y: auto;
  padding: 10px 16px;
  border-top: 1px solid #e8e8e8;
  background: #fafafa;
}

.mb-8 { margin-bottom: 8px; }

.result-actions { display: flex; gap: 6px; }

.btn-sm-outline {
  padding: 3px 10px;
  font-size: 11px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  &:hover { background: #f0f0f0; }
}

.stream-box {
  background: #1a1a2e;
  color: #00e676;
  border-radius: 6px;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 280px;
  overflow-y: auto;
  white-space: pre-wrap;
}

.result-box {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 12px;
  max-height: 320px;
  overflow-y: auto;
  font-size: 13px;
}

.result-section {
  margin-bottom: 14px;

  .result-section-title {
    font-size: 13px;
    font-weight: 700;
    color: #2c5ff6;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 4px;
    margin-bottom: 6px;
  }

  .result-section-body {
    font-size: 12px;
    color: #444;
    line-height: 1.7;
  }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0; }
}

:deep(.blink-cursor) {
  animation: blink 0.8s infinite;
  color: #00e676;
}
</style>
