<template>

  <!--for-vs-->
  <!--아이템 한개도 없을 경우, for-vs에서 active 삭제-->
  <div class="for-vs"
    :class="{ 'active': compareProdList.length > 0, 'acd' : !localData.bodyShow }"
  >
    <div class="container">
      <div class="container-inner">
        <!--header-->
        <div class="header">
          <div class="title">제품 비교하기 {{compareProdList.length}}/2</div>
          <div class="right">
            <div class="inner">
              <button 
                v-show="compareProdList.length > 0"
                @click="onClickReset"
                type="button" class="deselect">초기화</button>
              <button @click="onClickDoCompare"
                :disabled="compareProdList.length != 2"
                type="button" 
                class="btn btn-sm modal-open-full vs-detail-modal"
                :class="{'btn-disabled':compareProdList.length != 2 , 'btn-primary' : compareProdList.length == 2}"
              >결과보기</button>
            </div>
            <button @click="localData.bodyShow = !localData.bodyShow;" 
              type="button" class="ic-lg ic-arrow-down"></button>
          </div>
        </div>
        <!--body-->
        <!-- compareProdList:{{ compareProdList }} -->
        <transition
          @enter="onEnter"
          @after-enter="onAfterEnter"
          @leave="onLeave"
          @after-leave="onAfterLeave">
          <div class="body" v-show="localData.bodyShow">
            <div class="inner">
              
              <ProductCompareCard
                :prodInfo="compareProdList[0]"
                @onClickRemoveProd="onClickRemoveProd"
              />

              <ProductCompareCard
                :prodInfo="compareProdList[1]"
                @onClickRemoveProd="onClickRemoveProd"
              />            
            </div>
            <div class="vs-m-btn">
              <button @click="onClickDoCompare"
                :disabled="compareProdList.length != 2"
                type="button" 
                class="btn btn-sm w-100 modal-open-full vs-detail-modal"
                :class="{'btn-disabled':compareProdList.length != 2 , 'btn-primary' : compareProdList.length == 2}"
              >결과보기</button>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>

</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import ProductCompareCard from './ProductCompareCard.vue';

export default {
  name: 'coching-product-compare-bottom-modal',
  mixins: [ernsUtils],
  components: {
    ProductCompareCard,
  },
  props: {    
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
    compareProdList(newValue, oldValue) {
      //console.log("compareProdList changed:", { oldValue, newValue });
      
      //비교하기 목록이 1->2 로 변화시 
      if(oldValue.length == 1 && newValue.length == 2){        
        this.localData.bodyShow = true;
      }
    },
  },
  data() {
    return {
      localData: {
        isActive : true,
        classOption: "",
        result : undefined,

        bodyShow : false,

        prodItems:[]
      },      
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

    //비교하기 추가/제거
    setProdInfo(prodInfo){
      const _vm = this;

      const removeRet = _vm.removeProdInfo(prodInfo);
      if(removeRet){return;}

      _vm.addProdInfo(prodInfo);
    },

    //비교하기 추가
    addProdInfo(prodInfo){
      const _vm = this;

      //console.debug(prodInfo);
      if(!prodInfo){
        return false;
      }

      if(_vm.compareProdList.length >= 2){
        //TODO 2개만
        return false;
      }

      const list = [..._vm.compareProdList, prodInfo];
      _vm.$store.dispatch('coching/setCompareProdList', list);
      return true;
    },

    //비교하게 제거
    removeProdInfo(prodInfo){
      const _vm = this;

      // console.debug(prodInfo);
      // console.debug(_vm.compareProdList);

      const index = _vm.compareProdList.findIndex(item => item.product.id === prodInfo.product.id);

      if (index !== -1) {
        _vm.compareProdList.splice(index, 1);
        return true;
      }
      
      _vm.$store.dispatch('coching/setCompareProdList', _vm.compareProdList);
      return false;
    },    

    //초기화
    onClickReset(){
      const _vm = this;
      _vm.$store.dispatch('coching/setCompareProdList', []);
    },

    //비교하기
    onClickDoCompare(){
      const _vm = this;

      _vm.$emit('onClickDoCompare'); // 부모에게 close 이벤트 전달       
    },

    //상품제거 클릭이벤트
    onClickRemoveProd(prodInfo){
      const _vm = this;

      _vm.removeProdInfo(prodInfo);
    },

    closeModal() {
      const _vm = this;
      _vm.$emit('close'); // 부모에게 close 이벤트 전달
    },

    docReady(){
      const _vm = this;   

    },

    onEnter(el) {
      // 초기 max-height 설정
      el.style.maxHeight = '0';
      el.style.transition = 'max-height 0.5s ease';
      el.style.overflow = 'hidden';

      // 최대 높이 계산 후 적용
      const scrollHeight = el.scrollHeight + 'px';
      setTimeout(() => {
        el.style.maxHeight = scrollHeight;
      }, 10); // 트랜지션 시작을 위해 약간의 지연 추가
    },
    onAfterEnter(el) {
      // 애니메이션이 끝난 후 max-height 제한 제거
      el.style.maxHeight = '';
    },
    onLeave(el) {
      // 현재 높이를 계산하여 max-height로 설정
      el.style.maxHeight = el.scrollHeight + 'px';
      el.style.transition = 'max-height 0.5s ease';
      el.style.overflow = 'hidden';

      // 0으로 줄이기
      setTimeout(() => {
        el.style.maxHeight = '0';
      }, 10); // 트랜지션 시작을 위해 약간의 지연 추가
    },
    onAfterLeave(el) {
      // 애니메이션이 끝난 후 DOM에서 완전히 제거
      el.style.maxHeight = '';
    },
  }  
}
</script>

<style lang="scss" scoped>
.body .inner{
  padding-top: 1rem;
  padding-bottom: 1rem;
}

</style>

<style lang="scss">

</style>