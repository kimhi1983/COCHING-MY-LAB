<template>
  <div class="item"
    :class="{'empty-content ': !prodInfo}"
  >
    <div v-if="prodInfo" class="inner">
      <button @click="onClickRemoveProd"
        type="button" class="btn ic-md ic-close"></button>
      
      <!--img-->
      <div 
        v-if="prodInfo.product.image_url" class="pr-img aspect-ratio-container">
        <img 
          :src="getProxyFile(prodInfo.product.image_url)"
          :alt="prodInfo.product.name"
          :style="imageStyle"
          @load="onImageLoad"  
        />
      </div>
      <div v-else class="pr-img no-img"></div><!--이미지 없음 모든곳 공통-->

      <div class="pr-body">
        <div class="title">{{prodInfo.product.name || '-'}}</div>
        <div class="info">
          <span v-for="(ingdElem, idx) of prodInfo.ingredients" :key="idx"
            v-html="displayIngdNames(ingdElem, idx, prodInfo.ingredients)"/>          
          <span v-if="!(prodInfo.ingredients && prodInfo.ingredients.length > 0)">
            [No Data]
          </span>
        </div>
        <div class="co">{{prodInfo.product.brand.full_name || prodInfo.product.brand.name || '-'}}</div>
      </div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-prod-compare-card',
  mixins: [ernsUtils],
  components: {},
  props: {
    prodInfo: {
      type: Object, // 전달받는 데이터 타입
    }
  },
  computed: {    
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
  },
  data() {
    return {
      ld:{},
      imageStyle : {} // 동적으로 설정할 스타일
    }
  },
  async mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();    

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    displayIngdNames(ingdElem, index, arrays){
      const _vm = this;

      let retHtml = (ingdElem.korean || '').split(", ")[0];

      //TODO 
      //<span class="highlight">글리세린</span>
      if(index < (arrays.length-1)){
        retHtml = retHtml+",";
      }

      return retHtml || '-';
    },

    //이미지 로드
    onImageLoad(event) {
      const img = event.target;      
      const aspectRatio = img.naturalWidth / img.naturalHeight; // 가로세로 비율 계산
      const containerWidth = img.parentElement.clientWidth; // 컨테이너의 너비
      const containerHeight = img.parentElement.clientHeight; // 컨테이너의 높이

      if (aspectRatio > 1) {
        // 랜드스케이프
        this.imageStyle = {
          width: "100%",
          height: "auto",
        };
      } else {
        // 포트레이트
        this.imageStyle = {
          width: "auto",
          height: "100%",
        };
      }
    },

    //상품제거 클릭이벤트
    onClickRemoveProd(){
      const _vm = this;
      _vm.$emit("onClickRemoveProd", _vm.prodInfo);
    },

    //fetchData
    async fetchData(){
      const _vm = this;
    },    

    async loadCodes(){
      const _vm = this;
     
    },

    docReady(){
      const _vm = this;
    },
  }  
}
</script>

<style lang="scss" scoped>
.aspect-ratio-container {
  overflow: hidden;
  position: relative;

}

.aspect-ratio-container img {
  display: block;
  object-fit: cover; /* 비율 유지 및 초과 부분 잘림 */
}

.item.empty-content {
  min-height: inherit;
}
</style>

<style lang="scss">

</style>