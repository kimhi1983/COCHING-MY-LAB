<template>
  <div class="my-lab-wrap">

    <!-- ═══════════════════ LEFT PANEL ═══════════════════ -->
    <div class="my-lab-left">
      <div class="lab-left-header">
        <span class="lab-title">
          <feather-icon icon="FlaskIcon" size="16" class="mr-50" />
          MY LAB
        </span>
        <b-button size="sm" variant="primary" @click="onNewLab">
          <feather-icon icon="PlusIcon" size="12" class="mr-25" />새 처방
        </b-button>
      </div>

      <div class="lab-left-search">
        <b-input-group size="sm">
          <b-form-input
            v-model="sc.keyword"
            placeholder="처방명 검색..."
            @keyup.enter="loadList"
          />
          <b-input-group-append>
            <b-button variant="outline-secondary" @click="loadList">
              <feather-icon icon="SearchIcon" size="12" />
            </b-button>
          </b-input-group-append>
        </b-input-group>
      </div>

      <div class="lab-item-list" ref="labItemList">
        <div
          v-for="item in labList"
          :key="item.labMstSeq"
          class="lab-item"
          :class="{ active: selectedSeq === item.labMstSeq }"
          @click="selectLab(item)"
        >
          <div class="lab-item-name">{{ item.title || '(이름 없음)' }}</div>
          <div class="lab-item-meta">
            <span class="lab-item-cate">{{ getCateLabel(item) }}</span>
            <span class="lab-item-cnt">
              <feather-icon icon="ZapIcon" size="11" />
              {{ item.aiLabResCnt || 0 }}
            </span>
          </div>
          <div class="lab-item-date">{{ item.chngDttm | eufDateFmt }}</div>
        </div>

        <div v-if="!isListLoading && labList.length === 0" class="lab-empty-list">
          <feather-icon icon="InboxIcon" size="28" class="mb-1" />
          <p>처방 없음</p>
        </div>
        <div v-if="isListLoading" class="lab-empty-list">
          <b-spinner small />
        </div>
      </div>
    </div>

    <!-- ═══════════════════ RIGHT PANEL ═══════════════════ -->
    <div class="my-lab-right">

      <!-- 선택 전 Empty State -->
      <div v-if="!selectedSeq && !isNew" class="lab-empty-state">
        <feather-icon icon="FlaskIcon" size="48" class="mb-2 text-primary" />
        <h5>처방을 선택하거나 새로 만드세요</h5>
        <b-button variant="primary" @click="onNewLab">
          <feather-icon icon="PlusIcon" size="14" class="mr-50" />새 처방 만들기
        </b-button>
      </div>

      <!-- 처방 폼 -->
      <div v-else class="lab-form-wrap">

        <!-- 상단 헤더 -->
        <div class="lab-form-header">
          <div class="lab-form-title">
            <b-form-input
              v-model="form.title"
              placeholder="처방명을 입력하세요"
              class="lab-title-input"
              :readonly="!canEdit"
            />
          </div>
          <div class="lab-form-actions">
            <b-button
              v-if="canEdit"
              size="sm"
              variant="outline-secondary"
              class="mr-1"
              @click="onCancelEdit"
            >취소</b-button>
            <b-button
              v-if="canEdit"
              size="sm"
              variant="success"
              class="mr-1"
              :disabled="isSaving"
              @click="onSave"
            >
              <b-spinner v-if="isSaving" small class="mr-50" />
              저장
            </b-button>
            <b-button
              v-if="!isNew && !canEdit"
              size="sm"
              variant="outline-primary"
              class="mr-1"
              @click="canEdit = true"
            >
              <feather-icon icon="EditIcon" size="13" class="mr-25" />수정
            </b-button>
            <b-button
              v-if="!isNew"
              size="sm"
              variant="outline-danger"
              @click="onDelete"
            >
              <feather-icon icon="TrashIcon" size="13" />
            </b-button>
          </div>
        </div>

        <!-- 카테고리 + 요구사항 -->
        <div class="lab-form-meta">
          <b-row no-gutters>
            <b-col md="4" class="pr-1">
              <v-select
                v-model="form.prodCateGroup"
                :options="CODES.grpList"
                label="name"
                :reduce="o => o.etc3"
                :disabled="!canEdit"
                placeholder="제품 대분류"
                class="select-size-sm mb-50"
                @input="onChangeProdCateGroup"
              />
              <v-select
                v-model="form.prodCateCode"
                :options="CODES[`CATE${form.prodCateGroup}`] || []"
                label="name"
                :reduce="o => o.etc3"
                :disabled="!canEdit"
                placeholder="제품 소분류"
                class="select-size-sm"
              />
            </b-col>
            <b-col md="8" class="pl-1">
              <b-form-textarea
                v-model="form.reqContent"
                rows="2"
                :readonly="!canEdit"
                placeholder="처방 요구사항 (예: 미백 효과가 있는 수분크림)"
                class="text-sm"
              />
            </b-col>
          </b-row>
        </div>

        <!-- 성분 + AI 패널 (2-col) -->
        <div class="lab-form-body">

          <!-- 전성분 입력 -->
          <div class="ingredient-panel">
            <div class="panel-head">
              <span>전성분</span>
              <span class="text-muted text-sm">{{ ingredientCount }}개</span>
            </div>
            <b-form-textarea
              v-model="form.ingredientsRaw"
              :rows="12"
              :readonly="!canEdit"
              placeholder="성분을 쉼표(,) 또는 줄바꿈으로 구분하여 입력하세요."
              class="ingredient-textarea"
            />
          </div>

          <!-- AI 처방 옵션 + 실행 -->
          <div class="ai-panel">
            <div class="panel-head">
              <span>AI 처방</span>
              <b-badge v-if="lastResult" variant="light-success">결과 있음</b-badge>
            </div>

            <b-form-group label="캐시" class="mb-75">
              <div class="d-flex">
                <b-form-radio v-model="aiOpts.useCache" :value="true" class="mr-1">
                  <span class="text-sm">사용</span>
                </b-form-radio>
                <b-form-radio v-model="aiOpts.useCache" :value="false">
                  <span class="text-sm">새로</span>
                </b-form-radio>
              </div>
            </b-form-group>

            <b-form-group label="Gemini 리서치" class="mb-75">
              <b-form-checkbox v-model="aiOpts.useGemini" switch>
                <span class="text-sm">{{ aiOpts.useGemini ? '활성' : '비활성' }}</span>
              </b-form-checkbox>
            </b-form-group>

            <b-form-group label="스트리밍" class="mb-75">
              <b-form-checkbox v-model="aiOpts.streaming" switch>
                <span class="text-sm">{{ aiOpts.streaming ? '실시간' : '완료 후' }}</span>
              </b-form-checkbox>
            </b-form-group>

            <b-button
              block
              variant="gradient-primary"
              :disabled="isRunning || !hasIngredients"
              @click="onRunAi"
              class="mt-1"
            >
              <b-spinner v-if="isRunning" small class="mr-50" />
              <feather-icon v-else icon="ZapIcon" size="14" class="mr-50" />
              {{ isRunning ? `생성 중... (${elapsedSec}s)` : 'AI 처방 생성' }}
            </b-button>

            <b-button
              v-if="isRunning"
              block
              variant="outline-danger"
              size="sm"
              class="mt-50"
              @click="onAbortAi"
            >중지</b-button>

            <div v-if="lastMeta" class="ai-meta-box mt-1">
              <div class="ai-meta-item">
                <span>처리시간</span><strong>{{ lastMeta.processing_time }}s</strong>
              </div>
              <div class="ai-meta-item">
                <span>모델</span><strong>{{ lastMeta.model }}</strong>
              </div>
              <div class="ai-meta-item">
                <span>복잡도</span>
                <b-badge :variant="complexityVariant(lastMeta.complexity)" class="text-xs">
                  {{ lastMeta.complexity }}
                </b-badge>
              </div>
              <div class="ai-meta-item">
                <span>캐시</span>
                <feather-icon
                  :icon="lastMeta.from_cache ? 'CheckIcon' : 'XIcon'"
                  size="13"
                  :class="lastMeta.from_cache ? 'text-success' : 'text-muted'"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 결과 영역 -->
        <div v-if="streamText || lastResult" class="lab-result-wrap">
          <div class="panel-head mb-75">
            <span>AI 처방 결과</span>
            <div class="d-flex align-items-center">
              <b-button
                v-if="lastResult"
                size="sm"
                variant="outline-secondary"
                class="mr-50"
                @click="onCopyResult"
              >
                <feather-icon icon="CopyIcon" size="12" class="mr-25" />복사
              </b-button>
              <b-button
                v-if="lastResult"
                size="sm"
                variant="outline-primary"
                @click="onSaveResult"
              >
                <feather-icon icon="SaveIcon" size="12" class="mr-25" />저장
              </b-button>
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
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import vSelect from 'vue-select';
import { PROD_CATES } from '@/constants/testCode';
import {
  getLabMasterList,
  getLabMasterDetail,
  addLabMaster,
  setLabMaster,
  deleteLabMaster,
  getAiPrscStream,
} from '@/api/coching-bo/rnd/rnd';
import _ from 'lodash';

export default {
  name: 'MyLab',
  mixins: [ernsUtils],
  components: { vSelect },

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
        streaming: true,
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
      const text = typeof this.lastResult === 'string'
        ? this.lastResult
        : JSON.stringify(this.lastResult, null, 2);
      const sections = [];
      const lines = text.split('\n');
      let current = { title: '처방 결과', lines: [] };
      lines.forEach(line => {
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
      return sections.map(s => ({
        title: s.title,
        html: s.lines.join('\n')
          .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
          .replace(/\n/g, '<br>'),
      }));
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
      let list = PROD_CATES.filter(cd => cd.name !== '전체' && cd.code !== 2);
      list = _.sortBy(list, 'sortOrder');
      this.CODES.list = list;
      this.CODES.grpList = list.filter(cd => cd.etc1 == 1);
      this.CODES.grpList.forEach(g => {
        this.CODES[`CATE${g.etc3}`] = list.filter(
          cd => cd.etc3.indexOf(g.etc3) === 0 && cd.etc3 !== g.etc3
        );
      });
    },

    getCateLabel(item) {
      const g = this.CODES.list.find(c => c.etc3 == item.prodCateGroup);
      const d = this.CODES.list.find(c => c.etc3 == item.prodCateCode);
      if (g && d) return `${g.name} > ${d.name}`;
      if (g) return g.name;
      return '-';
    },

    onChangeProdCateGroup() {
      this.form.prodCateCode = '';
    },

    async loadList() {
      this.isListLoading = true;
      try {
        const res = await getLabMasterList({
          page: 1,
          rowSize: 100,
          titleL: this.sc.keyword || undefined,
        });
        this.labList = res.resultData?.list || [];
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
        const res = await getLabMasterDetail({ labMstSeq: item.labMstSeq });
        const d = res.resultData;
        this.form = {
          title: d.title || '',
          prodCateGroup: d.prodCateGroup || '',
          prodCateCode: d.prodCateCode || '',
          reqContent: d.reqContent || '',
          ingredientsRaw: (d.ingredients || [])
            .map(i => i.ingredientName).join('\n'),
        };
        this.formOriginal = { ...this.form };
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
      this.form = {
        title: '',
        prodCateGroup: '',
        prodCateCode: '',
        reqContent: '',
        ingredientsRaw: '',
      };
    },

    onCancelEdit() {
      if (this.isNew) {
        this.isNew = false;
        this.selectedSeq = null;
      } else {
        this.form = { ...this.formOriginal };
        this.canEdit = false;
      }
    },

    async onSave() {
      if (!this.form.title.trim()) {
        this.$bvToast.toast('처방명을 입력하세요.', {
          variant: 'warning', title: '알림', toaster: 'b-toaster-top-center',
        });
        return;
      }
      this.isSaving = true;
      try {
        const payload = {
          title: this.form.title,
          prodCateGroup: this.form.prodCateGroup,
          prodCateCode: this.form.prodCateCode,
          reqContent: this.form.reqContent,
          ingredients: this.form.ingredientsRaw
            .split(/[,\n]/).map(s => s.trim()).filter(Boolean)
            .map((name, idx) => ({ ingredientName: name, sortOrder: idx + 1 })),
        };
        if (this.isNew) {
          await addLabMaster(payload);
          this.alertSuccess('처방이 등록되었습니다.');
        } else {
          await setLabMaster({ ...payload, labMstSeq: this.selectedSeq });
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
      const ok = await this.$swal({
        title: '삭제',
        text: '처방을 삭제하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '삭제',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });
      if (!ok.value) return;
      this.loading(true);
      try {
        await deleteLabMaster({ labMstSeq: this.selectedSeq });
        this.alertSuccess('삭제되었습니다.');
        this.selectedSeq = null;
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
      this.elapsedTimer = setInterval(() => { this.elapsedSec++; }, 1000);

      const ingredientsList = this.form.ingredientsRaw
        .split(/[,\n]/).map(s => s.trim()).filter(Boolean).join(', ');

      const payload = {
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
        (chunk) => {
          if (chunk.type === 'content') {
            this.streamText += chunk.text || '';
            this.$nextTick(() => {
              if (this.$refs.streamBox) {
                this.$refs.streamBox.scrollTop = this.$refs.streamBox.scrollHeight;
              }
            });
          } else if (chunk.type === 'result') {
            this.lastResult = chunk.result_data || chunk.data || this.streamText;
            this.lastMeta = {
              processing_time: chunk.processing_time,
              model: chunk.model,
              complexity: chunk.complexity,
              from_cache: chunk.from_cache,
            };
          }
        },
        () => { this.onAiDone(); },
        (err) => { this.alertError(err); this.onAiDone(); }
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

    complexityVariant(c) {
      return { simple: 'success', normal: 'warning', complex: 'danger' }[c] || 'secondary';
    },

    onCopyResult() {
      const text = typeof this.lastResult === 'string'
        ? this.lastResult
        : JSON.stringify(this.lastResult, null, 2);
      navigator.clipboard.writeText(text).then(() => {
        this.$bvToast.toast('클립보드에 복사되었습니다.', {
          variant: 'success', title: '복사', toaster: 'b-toaster-top-center',
        });
      });
    },

    onSaveResult() {
      this.$bvToast.toast('결과 저장 기능은 백엔드 연동 후 활성화됩니다.', {
        variant: 'info', title: '안내', toaster: 'b-toaster-top-center',
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.my-lab-wrap {
  display: flex;
  height: calc(100vh - 130px);
  min-height: 600px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

/* ── LEFT ────────────────── */
.my-lab-left {
  width: 280px;
  min-width: 280px;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.lab-left-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px 8px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;

  .lab-title {
    font-weight: 700;
    font-size: 14px;
    color: #3d3d3d;
    display: flex;
    align-items: center;
  }
}

.lab-left-search {
  padding: 8px 10px;
  border-bottom: 1px solid #f0f0f0;
}

.lab-item-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;
}

.lab-item {
  padding: 9px 14px;
  cursor: pointer;
  border-bottom: 1px solid #f2f2f2;
  transition: background 0.12s;

  &:hover { background: #eef2ff; }
  &.active { background: #e8edff; border-left: 3px solid #7367f0; padding-left: 11px; }

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
    margin-top: 2px;

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
      color: #7367f0;
      display: flex;
      align-items: center;
      gap: 2px;
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
}

/* ── RIGHT ───────────────── */
.my-lab-right {
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

  h5 { color: #888; }
}

.lab-form-wrap {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.lab-form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;

  .lab-title-input {
    font-size: 16px;
    font-weight: 600;
    border: none;
    border-bottom: 2px solid #e0e0e0;
    border-radius: 0;
    padding: 2px 0;
    box-shadow: none !important;
    width: 380px;
    max-width: 100%;

    &:focus { border-bottom-color: #7367f0; }
    &[readonly] { background: transparent; }
  }

  .lab-form-actions { display: flex; align-items: center; flex-shrink: 0; }
}

.lab-form-meta {
  padding: 8px 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

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

  .ingredient-textarea {
    flex: 1;
    resize: none;
    font-family: 'Courier New', monospace;
    font-size: 12px;
    height: 100% !important;
    max-height: none !important;
  }
}

.ai-panel {
  width: 230px;
  min-width: 230px;
  padding: 10px 14px;
  overflow-y: auto;
  background: #fafafa;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  font-weight: 700;
  color: #666;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.ai-meta-box {
  background: #f0f0f0;
  border-radius: 6px;
  padding: 8px 10px;
}

.ai-meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  padding: 2px 0;
  color: #555;

  strong { font-size: 11px; color: #333; }
}

.lab-result-wrap {
  flex-shrink: 0;
  max-height: 38%;
  overflow-y: auto;
  padding: 10px 16px;
  border-top: 1px solid #e8e8e8;
  background: #fafafa;
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
  margin-bottom: 12px;

  .result-section-title {
    font-size: 13px;
    font-weight: 700;
    color: #7367f0;
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

.text-sm  { font-size: 12px; }
.text-xs  { font-size: 11px; }
.cursor-pointer { cursor: pointer; }
.mb-75 { margin-bottom: 0.75rem; }
</style>
