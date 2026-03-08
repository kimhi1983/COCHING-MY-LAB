<template>
  <ModalFull ref="modal_full"
    :isActive="localData.isActive" 
    :classOption="localData.classOption"
    @onClickDim="onClickClose()"
    @close="onClickClose()">

    <!-- Header Content -->
    <template v-slot:header>
      <div class="title">제품</div>
     
    </template>

    <!-- Body Content -->
    <template v-slot:body>
      <div class="modal-pr-wrap">
      <div class="item">
        <!--pr-info-->
        <div class="pr-info">
          <!--img-->
          <div 
            v-if="localData.prodInfo.product && localData.prodInfo.product.image_url" 
            class="pr-img aspect-ratio-container">
            <img 
              :src="getProxyFile(localData.prodInfo.product.image_url)" 
              :alt="localData.prodInfo.product.name"
              :style="imageStyle"
              @load="onImageLoad"  
            />
          </div>
          <div v-else class="pr-img no-img"></div><!--이미지 없음 모든곳 공통-->

          <!--body-->
          <div class="pr-body">
            <div class="title">{{localData.prodInfo.product.name}}</div>
            <div v-if="hasBrandName()" class="co">
              <span class="search-text default-color addIcon"
                @click="goSearchKeywordV2(`${getBrandName()}`, {hintField : '2001', emo : true})"
              >{{getBrandName()}}</span>              
            </div>
            <div v-else class="co">1{{ getBrandName() || '-'}}</div>            
            <div class="price">{{localData.prodInfo.product.buy_info}}</div>
          </div>
        </div>
        <!--ewg table-->
        <div class="ewg-table">
          <div class="total-num">전성분<span>{{localData.prodInfo.ingredients.length}}</span></div>
          <table>
            <colgroup>
              <col width="80" />
            </colgroup>
            <thead>
              <tr>
                <th>EWG</th>
                <th>성분명</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(ingd, idx) of localData.prodInfo.ingredients" 
                :key="idx" class="link"
                @click="onClickIngdList(ingd)">
                <td>
                  <div class="ewg" :class="getEwgClass(getEwgInfo(ingd.ewg).max)">
                  {{getEwgInfo(ingd.ewg).scoreText}}</div>
                </td>
                <td>{{ingd.korean}}
                  <div v-if="ingd.english"
                    class="eng">{{ingd.english}}</div>
                </td>
              </tr>              
            </tbody>
          </table>
        </div>
      </div>
    </div>
    </template>

  </ModalFull>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { DEF_PRODINFO } from '@/constants/data';

import ModalFull from '@/components/modal/ModalFull.vue';

export default {
  name: 'coching-prodInfo-modal',
  mixins: [ernsUtils],
  components: {
    ModalFull,
  },
  props: {     
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
      localData: {
        isActive : false,
        classOption: "modal-md for-card-product",
        result : undefined,

        prodInfo : {
          ...DEF_PRODINFO
        },
      }, 
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
    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = true;
      ld.prodInfo = options.prodInfo;

      _vm.$refs['modal_full'].bodyScrollToTop(500);

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    getBrandName(){      
      return (this.localData.prodInfo.product.brand.full_name || this.localData.prodInfo.product.brand.name || '').trim();
    },

    hasBrandName(){
      return this.getBrandName() !== '';
    },

    //전성분 상세보기 클릭
    onClickIngdList(pickItem){
      const _vm = this;
      
      _vm.ermPushPage({
        name:'coching-ingredients', 
        query : {
          productId : _vm.localData.prodInfo.product.id,
          ingdId : pickItem.id
        }
      });
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

    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try{
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    async loadCodes(){
      const _vm = this;
     
    },

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = false;
      ld.result(false);
    },

    closeModal() {
      const _vm = this;
      _vm.$emit('close'); // 부모에게 close 이벤트 전달
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