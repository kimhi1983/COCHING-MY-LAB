<template>
  <a href="javascript:;" class="item">
    <div class="title-wrap">
      <div class="title"
        @click.stop="onClickTitle"
        v-html="getTitleHtml()"
      ></div><!-- <span class="highlight">글리</span>세린 -->        
      <div class="info"
        @click.stop="onClickInfo"
        v-html="getInfoHtml()"
      ></div>
      <!-- <span class="highlight">(주)케이프로스</span> -->        
      <!-- <span>삼정트레이딩</span> -->
      <!-- 파트너 -->
      <!-- 공급사-->
    </div>
    <!--dataType-->
    <div class="badge pink">브랜드</div>
  </a>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'Coching-Search-Suggestion-HwBrand',
  mixins: [ernsUtils],
  components: {    
	},
  computed: {    
  },
  props:{
    keyword : {
      type : String,
      default : '',
    },
    suggestion : {
      type : Object,
      default: () => ({}),
      require : true
    },
  },
  watch:{
  },
  data(){
    return {

    }
  },
  methods: {
    onClickTitle(){
      const _vm = this;
      _vm.$emit("onClickTitle", _vm.suggestion);
    },

    onClickInfo(){
      const _vm = this;
      _vm.$emit("onClickInfo", _vm.suggestion);
    },

    getTitleHtml(){
      const _vm = this;

      const searchQuery = (_vm.keyword || "").trim();
      const text = (_vm.suggestion.data2 || "").trim();
      if (text.toLowerCase().includes(searchQuery) && searchQuery !== "") {
        const highlightedText = text.replace(new RegExp(`(${searchQuery})`, "gi"), '<span class="highlight">$1</span>');
        return highlightedText;
      }

      return text;
    },

    getInfoHtml(){
      const _vm = this;

      const data5Html = _vm.highlightSubText(_vm.suggestion.data5, _vm.keyword);
      const data4Html = _vm.highlightSubText(_vm.suggestion.data4, _vm.keyword);

      let retHtml = "";
      if(data5Html){
        retHtml += `<span>${data5Html}</span>`;
      }
      if(data4Html){
        retHtml += ` <span>${data4Html}</span>`;
      }

      return retHtml;      
    },

    highlightSubText(pOrgText, pSearchQuery){
      const searchQuery = (pSearchQuery || "").trim();
      const text = (pOrgText || "").trim();

      if(text == ""){
        return "";
      }

      if (text.toLowerCase().includes(searchQuery) && searchQuery !== "") {
        const highlightedText = text.replace(new RegExp(`(${searchQuery})`, "gi"), '<em class="highlight">$1</em>');
        return highlightedText;
      }

      return pOrgText
    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
