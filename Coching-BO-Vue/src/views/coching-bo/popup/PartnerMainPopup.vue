<template>
  <Popup 
    v-bind="$props"
  ></Popup>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import Popup from '@/components/popup/Popup.vue';
import popupMixin from '@/components/popup/popupMixin';
import { POPUP_MODE } from '@/constants/popup';

export default {
  name: 'coching-bo-UserMainPopup',
  mixins: [ernsUtils, popupMixin],
  components : {
    Popup
  },
  directives: {
  },
  props: {
    popupMstCd: {
      type: String,
      require: true
    },
    popupMode:{
			type: String,
			default: POPUP_MODE.LIST
		},
    popupSeq:{
      type : [String, Number],
			default : 0
    },
  },
  computed : {    
  },
  data(){
    return {
		}
  },
  async mounted(){
    const _vm = this;

    await _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
  },
  
  methods: {
    /* 데이터 가져오는 부분(사용하지 필요없어도 반드시 선언) */
		async fetchData() {      
			const _vm = this;

			_vm.loading(true);
			try {
				
			} catch (err) {
				_vm.defaultApiErrorHandler(err); //기본에러처리
			} finally {
				_vm.loading(false);
			}
		},
  }
}
</script>

<style lang="scss" scoped>  
</style>
