<template>
  <div ref="fullModal" class="modal-full" 
    :class="[classOption != '' ? classOption : '', { 'active': isActive }]"
    @click="onClickModal">
    <div class="inner" ref="modalContent">
      <!-- header -->
      <div class="modal-header">
        <slot name="header"></slot>
        <div class="full-close ic-wrap" @click="closeModal()">
          <div class="ic-lg ic-close"></div>
        </div>
      </div>

      <!-- body -->
      <div class="modal-body scroller" ref="modal_body">
        <slot name="body"></slot>
      </div>

      <!-- footer -->
      <div class="modal-footer">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';


export default {
    name: 'coching-modal-full',
    mixins: [ernsUtils],
    components: {
      
    },
    props: {
     isActive: {
        type: Boolean,
        default: false
     },
     classOption: {
        type: String,
        default: ''
     }
    },
    computed: {
        
    },
    watch: {
      '$i18n.locale' : async function(){
        const _vm = this;
        await _vm.loadCodes();
        _vm.$nextTick(() => _vm.$refs.cntrcDetailForm.validate());
      },
    },
    data() {
        return {
          localInvalidState: false, //양식폼의 validate 상태
          status: '',
          CODES: {
           
          },
          localData: {
            
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

    //모달창 클릭영역 감시
    onClickModal(event){
      const _vm = this;
      //console.debug(event);

      // 클릭된 대상이 .modal-content 내부가 아니면 닫기
      if(this.$refs.modalContent){
        if (!this.$refs.modalContent.contains(event.target)) {
          //console.debug("click outside");
          _vm.$emit('onClickDim');        
        }else{
          //console.debug("click inside");
        }
      }
    },

    closeModal(){
      const _vm = this;
      _vm.$emit('close');
    },

    bodyScrollToTop(animationTime){
      const _vm = this;

      const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);
      if(!isMobile){
        const bodyRef = _vm.$refs["modal_body"];
        //console.log(bodyRef);
        //console.log($(bodyRef).getNiceScroll(0));  
        
        $(bodyRef).getNiceScroll(0).doScrollTop(0, animationTime);  
      }
    },

    docReady(){
      const _vm = this;   

    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>