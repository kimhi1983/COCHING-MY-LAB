<template>
  <div class="es_highlight_array_text_cell_renderer">
    <span 
      v-for="(item, index) in processedArray" 
      :key="index"
      :class="['array-item', { 'clickable': hasOnClick }]"
      @click="handleItemClick(item, index, $event)"
    >
      <span v-html="item"/>
      <span v-if="index < processedArray.length - 1" class="separator">, </span>
    </span>
    <!-- {{ highlightPrefix }} -->
  </div>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";

export default {
  mixins: [ernsUtils],
  props: {
    
  },
  beforeMount() {
    const _vm = this;

    _vm.field = _vm.params.colDef.field;
    _vm.value = _vm.params.value;
    _vm.processedArray = Array.isArray(_vm.value) ? _vm.value : [_vm.value];
    _vm.highlightPrefix = _vm.params.highlightPrefix || _vm.field;
    _vm.setHightlightText();    
  },
  data() {
    return {
      field : "",
      value : "",
      processedArray : [],
      highlightPrefix : "",
    };
  },
  computed: {
    hasOnClick() {
      return this.params && typeof this.params.onClick === 'function';
    }
  },    
  methods: {
    setHightlightText(){
      const _vm = this;
      const preFix = _vm.highlightPrefix;

      //console.debug(_vm.params);
      
      // 배열이 아닌 경우 기본 처리
      if (!Array.isArray(_vm.value)) {
        _vm.processedArray = [_vm.eumConvertStringArrayToString(_vm.value)];
        return;
      }

      // 1단계: 먼저 각 배열 요소에 하이라이트 처리 적용
      let processedArray = _vm.value;
      
      if(preFix){
        const highlightData = _vm.params.data["_highlight"];
        if(highlightData){
          const highlightTexts = _vm.getHighlightText(highlightData, preFix);
          if(highlightTexts){
            // 하이라이트 처리된 배열 생성
            processedArray = _vm.replaceHighlightTextArray(_vm.value, highlightTexts);
          }
        }
      }

      // 2단계: 처리된 배열을 데이터에 저장 (join하지 않음)
      _vm.processedArray = processedArray;
    },

    getHighlightText(highlightData, preFix){
      const _vm = this;
      if(!highlightData){
        return null;
      }

      // preFix가 배열인지 확인
      const prefixes = Array.isArray(preFix) ? preFix : [preFix];
      let allHighlightTexts = [];

      // 각 prefix에 대해 하이라이트 데이터 검색
      for (const prefix of prefixes) {
        // 키워드 우선 검색
        const keywords = highlightData[`${prefix}.static`] || highlightData[`${prefix}.keyword`];
        if(keywords){
          const keywordArray = Array.isArray(keywords) ? keywords : [keywords];
          allHighlightTexts = allHighlightTexts.concat(keywordArray);
          continue; // 키워드를 찾았으면 다음 prefix로
        }

        // 일반 하이라이트 검색
        const highlightTexts = highlightData[prefix];
        if(highlightTexts){
          const highlightArray = Array.isArray(highlightTexts) ? highlightTexts : [highlightTexts];
          allHighlightTexts = allHighlightTexts.concat(highlightArray);
        }
      }

      return allHighlightTexts.length > 0 ? allHighlightTexts : null;
    },

    replaceHighlightTextArray(values, highlightTexts){
      const _vm = this;
      const hts = highlightTexts instanceof Array ? highlightTexts : [highlightTexts];
      const checkArray = hts.map(ht=>{        
        const text = ht.replace(/<[^>]*>/g, '');
        //console.debug(text);
        return {
          htmlText : ht,
          text : text
        }
      });

      const ret = values.map(v=>{
        //console.debug(v);
        const findText = checkArray.find(cv=>cv.text.toLowerCase() == v.toLowerCase());
        return findText ? findText.htmlText : v;
      });      

      return ret;
    },

    handleItemClick(item, index, event) {
      const _vm = this;
      
      // onClick 핸들러가 없으면 클릭 처리하지 않음
      if (!_vm.hasOnClick) {
        return;
      }
      
      // HTML 태그를 제거하여 실제 텍스트 값 추출
      const cleanItem = item.replace(/<[^>]*>/g, '');
      
      // 부모에서 전달된 onClick 핸들러 실행
      _vm.params.onClick({
        event: event,
        data: _vm.params.data,
        value: cleanItem,  // 클릭된 개별 아이템 값
        originalValue: _vm.value,  // 전체 배열 값
        index: index,  // 클릭된 아이템의 인덱스
        field: _vm.field,
        params: _vm.params,
        isArrayItem: true  // 배열 아이템임을 표시
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.es_highlight_array_text_cell_renderer{
  .array-item {
    display: inline-block;
    border-radius: 4px;
    padding: 2px 4px;
    margin: 1px;
    transition: background-color 0.2s ease;
    
    &.clickable {
      cursor: pointer;
      
      &:hover {
        background-color: rgba(0, 123, 255, 0.1);
      }
      
      &:active {
        background-color: rgba(0, 123, 255, 0.2);
      }
    }
  }
  
  .separator {
    cursor: default;
    padding: 0;
    margin: 0;
    
    &:hover {
      background-color: transparent !important;
    }
  }
}
</style> 