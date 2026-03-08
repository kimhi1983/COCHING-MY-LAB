<template>
  <div class="ai-prsc-card">
    <!-- 카드 헤더: 날짜와 처방 번호 -->
    <div class="ai-prsc-card-header">
      {{ prscNumber }} {{ (aiPrsc.rgtDttm || aiPrsc.createDate) | eFmtDateTime('.') }} 
    </div>
    
    <!-- 카드 본문 -->
    <div class="ai-prsc-card-body">
      <!-- 카테고리 정보 -->
      <div class="ai-prsc-info-item">
        <span class="ai-prsc-icon">▶</span>
        <span>{{ localData.aiLabRes.request_data.formulation}}</span>
      </div>

      <div class="ai-prsc-info-item">
        <span class="ai-prsc-icon">▶</span>
        <span>{{ localData.aiLabRes.request_data.direction}}</span>
      </div>

      <!-- 성분 개수 -->
      <div class="ai-prsc-info-item">
        <span class="ai-prsc-icon">▶</span>
        <span>
          {{ getIngredientText(localData.aiLabRes.request_data) }}
        </span>
      </div>
      
      <!-- 요청 내용 (긴 경우 말줄임) -->
      <div class="ai-prsc-info-item">
        <span class="ai-prsc-icon">▶</span>
        <span class="ai-prsc-text-ellipsis">{{ localData.aiLabRes.resultData.overview }}</span>
      </div>     
      
    </div>

    <!-- aiLabRes:{{ localData.aiLabRes }} -->
    
    <!-- 카드 푸터 링크 -->
    <div class="ai-prsc-card-footer">
      <a href="#" class="ai-prsc-link-footer" @click.prevent="onClickViewDetail">자세히보기</a>
    </div>
  </div>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";

const DEF_AI_LAB_RES = {
  request_id : '',
  resultCode : '0000',  
  total_ingredients : 0,
  resultData : {
    overview : '',
  },
  request_data : {
    direction : '',
    formulation : '', 
    ingredients: [],   
  }
};

export default {
  name: 'AiPrscCard',
  mixins: [ernsUtils],
  props: {
    aiPrsc: {
      type: Object,
      required: true
    },
    prscNumber: {
      type: String,
      required: true
    }
  },
  watch: {
    'aiPrsc.aiLabResJson': 'loadAiLabRes',
  },
  data() {
    return {
      localData: {
        aiLabRes : {...DEF_AI_LAB_RES},
      }
    }
  },
  mounted() {
    const _vm = this;
    _vm.loadAiLabRes(_vm.aiPrsc.aiLabRes || JSON.stringify(DEF_AI_LAB_RES));
  },
  methods: {
    // 성분 텍스트 생성 (최대 5개까지 성분명 표시 후 "등 n개" 형식)
    getIngredientText(aiPrsc) {
      if (aiPrsc.ingredients && aiPrsc.ingredients.length > 0) {
        const count = aiPrsc.ingredients.length;
        const maxDisplay = 5;
        const displayCount = Math.min(count, maxDisplay);
        
        // 표시할 성분명 추출
        const ingredientNames = aiPrsc.ingredients
          .slice(0, displayCount)
          .map(ing => ing.repName || ing.rep_name || ing.name || '')
          .filter(name => name); // 빈 문자열 제거
        
        if (ingredientNames.length === 0) {
          return `${count}개`;
        }
        
        const namesText = ingredientNames.join(', ');
        
        // 항상 "등 n개" 형식으로 표시
        return `${namesText} 등 ${count}개`;
      }
      return '';
    },

    // 자세히보기 클릭
    onClickViewDetail() {
      this.$emit('view-detail', this.aiPrsc);
    },

    async loadAiLabRes(aiLabResJson) {
      const _vm = this;

      _vm.localData.aiLabRes = JSON.parse(aiLabResJson);
    }
  }
};
</script>

<style lang="scss" scoped>
.ai-prsc-card {
  flex: 0 0 calc(33.333% - 11px); // 3개씩 표시, gap 고려
  min-width: 280px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s ease;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  @media (max-width: 992px) {
    flex: 0 0 calc(50% - 8px); // 태블릿: 2개씩
  }

  @media (max-width: 576px) {
    flex: 0 0 100%; // 모바일: 1개씩
  }
}

.ai-prsc-card-header {
  font-weight: 600;
  font-size: 0.95rem;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e9ecef;
}

.ai-prsc-card-body {
  flex: 1;
  margin-bottom: 12px;
}

.ai-prsc-info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #555;
  line-height: 1.5;

  &:last-child {
    margin-bottom: 0;
  }
}

.ai-prsc-icon {
  color: #000;
  margin-right: 6px;
  flex-shrink: 0;
  font-size: 0.85rem;
}

.ai-prsc-text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; // 최대 2줄
  line-clamp: 2; // 표준 속성
  -webkit-box-orient: vertical;
  word-break: break-word;
}

.ai-prsc-card-actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-top: 12px;
  border-top: 1px solid #e9ecef;
}

.ai-prsc-link {
  color: #007bff;
  text-decoration: none;
  font-size: 0.85rem;
  cursor: pointer;
  transition: color 0.2s ease;

  &:hover {
    color: #0056b3;
    text-decoration: underline;
  }
}

.ai-prsc-card-footer {
  text-align: center;
  padding-top: 8px;
  border-top: 1px solid #e9ecef;
}

.ai-prsc-link-footer {
  color: #007bff;
  text-decoration: none;
  font-size: 0.85rem;
  cursor: pointer;
  transition: color 0.2s ease;

  &:hover {
    color: #0056b3;
    text-decoration: underline;
  }
}
</style>

