<template>
  <div class="layer">
    <div class="layer-content">
      <div class="modal-inner" :class="classOption != '' ? classOption : ''">
        <div class="modal-content">
          <!-- header -->
          <div class="modal-header">
            <slot name="header"></slot>
          </div>

          <!-- body -->
          <div class="modal-body">
            <slot name="body"></slot>
          </div>

          <!-- footer -->
          <div class="modal-footer">
            <slot name="footer"></slot>
          </div>
        </div>
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