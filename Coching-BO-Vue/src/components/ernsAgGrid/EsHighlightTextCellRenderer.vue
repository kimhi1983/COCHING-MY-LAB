<template>
  <div class="es_highlight_text_cell_renderer" 
       :class="{ 'clickable': hasOnClick }"
       @click="handleClick">
    <span v-html="displayText"/>
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
    _vm.displayText = _vm.value;
    _vm.highlightPrefix = _vm.params?.highlightPrefix || _vm.field;
    _vm.setHightlightText();    
  },
  data() {
    return {
      field : "",
      value : "",
      displayText : "",
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
      if(!preFix){
        return;
      }

      const highlightData = _vm.params.data["_highlight"];
      if(!highlightData){
        return;
      }

      const highlightTexts = _vm.getHighlightText(highlightData, preFix);
      if(!highlightTexts){
        return;
      }

      const rht = _vm.replaceHighlightText(_vm.value, highlightTexts);
      _vm.displayText = _vm.eumConvertStringArrayToString(rht);
    },

    getHighlightText(highlightData, preFix){
      const _vm = this;
      if(!highlightData){
        return null;
      }

      // preFix가 배열인지 확인
      const prefixes = Array.isArray(preFix) ? preFix : [preFix];
      let allHighlightTexts = [];

      //console.debug(prefixes);

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

    replaceHighlightText(values, highlightTexts){
      const _vm = this;
      const tv = values instanceof Array ? values : [values];
      const hts = highlightTexts instanceof Array ? highlightTexts : [highlightTexts];
      const checkArray = hts.map(ht=>{        
        const text = ht.replace(/<[^>]*>/g, '');
        //console.debug(text);
        return {
          htmlText : ht,
          text : text
        }
      });

      const ret = tv.map(v=>{
        //console.debug(v);
        const findText = checkArray.find(cv=>cv.text.toLowerCase() == v.toLowerCase());
        if(findText){
          return findText.htmlText;
        }

        const findText2 = checkArray.find(cv=>v.toLowerCase().indexOf(cv.text.toLowerCase()) >= 0);
        return findText2 ? v.replace(findText2.text, findText2.htmlText) : v;
      });      

      return ret;
    },

    handleClick(event) {
      const _vm = this;
      
      // onClick 핸들러가 없으면 클릭 처리하지 않음
      if (!_vm.hasOnClick) {
        return;
      }
      
      // 부모에서 전달된 onClick 핸들러 실행
      _vm.params.onClick({
        event: event,
        data: _vm.params.data,
        value: _vm.value,
        field: _vm.field,
        params: _vm.params
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.es_highlight_text_cell_renderer{
  &.clickable {
    cursor: pointer;
    
    &:hover {
      background-color: rgba(0, 0, 0, 0.05);
    }
  }
}

</style>
