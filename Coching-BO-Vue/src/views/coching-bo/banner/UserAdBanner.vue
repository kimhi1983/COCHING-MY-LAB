<template>
  <Banner 
    v-bind="$props"
  ></Banner>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import Banner from '@/components/banner/Banner.vue';
import bannerMixin from '@/components/banner/bannerMixin';
import { BANNER_ID, BANNER_MODE } from '@/constants/banner';

export default {
  name: 'coching-bo-UserAdBanner',
  mixins: [ernsUtils, bannerMixin],
  components : {
    Banner
  },
  directives: {
  },
  props: {
    bannerMstCd: {
      type: String,
      require: true
    },
    bannerMode:{
			type: String,
			default: BANNER_MODE.LIST
		},
    bannerSeq:{
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
