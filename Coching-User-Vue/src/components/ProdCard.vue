<template>
  <div class="card-vs" @click="onClickCard()">
    <!--비교하기-->
    <div @click.stop="onClickCompare"
      class="vs"
      :class="{'active': compareProdList.findIndex(item => item.product.id === prodInfo.product.id) !== -1}"
    >비교하기</div>
    <div class="item">
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

      <!--body-->
      <div class="pr-body">
        <div class="title">{{prodInfo.product.name || '-'}}</div>
        <div class="info">
          <span v-for="(ingdElem, idx) of prodInfo.ingredients" :key="idx"
            v-html="displayIngdNamesHtml(ingdElem.korean, idx, prodInfo.ingredients)"/>          
          <span v-if="!(prodInfo.ingredients && prodInfo.ingredients.length > 0)">
            [No Data]
          </span>
        </div>
        <div class="co">{{ getBrandName() || '-'}}</div>
      </div>
    </div>
    <!-- prodInfo:{{ prodInfo }} -->
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  name: 'coching-prod-card',
  mixins: [ernsUtils],
  components: {},
  props: {
    prodInfo: {
      type: Object, // 전달받는 데이터 타입
      required: true // 필수 값
    }
  },
  computed: {
    compareProdList(){
      return this.$store.state.coching.compareProdList || [];
    },  
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
      isCompareAddedItem : false,
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
    getBrandName(){      
      return (this.prodInfo.product.brand.full_name || this.prodInfo.product.brand.name || '').trim();
    },

    hasBrandName(){
      return this.getBrandName() !== '';
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

    //상품카드 클릭이벤트
    onClickCard(){
      const _vm = this;
      _vm.$emit("onClickCard", _vm.prodInfo);
    },

    //비교하기 클릭 이벤트
    onClickCompare(){
      const _vm = this;
      _vm.$emit("onClickCompare", _vm.prodInfo);
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
</style>

<style lang="scss">

</style>