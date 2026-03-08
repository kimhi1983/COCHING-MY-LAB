<template>
	<!-- <div>
		recentInfoMode:{{ recentInfoMode }}<br/>
		localData.recentInfoMode:{{ localData.recentInfoMode }}<br/>		
		messageSeq:{{ messageSeq }}<br/>		 -->
		<RecentInfoList
			id="coching-recentInfo"
			v-bind="$props"
		></RecentInfoList>
	<!-- </div> -->
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import recentInfoMixin from './recentInfoMixin';
import { RECENT_INFO_MODE, REF_CODES } from '@/constants/recentInfo';

import RecentInfoList from './RecentInfoList.vue';

const DEF_MODE = RECENT_INFO_MODE.LIST;

export default {
	name: 'coching-recentInfo-main',
	mixins: [ernsUtils, recentInfoMixin],
	components: {
    RecentInfoList
  },
	computed: {    
  },
	watch: {
		'$route.params'(newVal, oldVal) {
			this.settingApp();
		}
	},
	props: {
    refCode: {
      type: String,
      default: REF_CODES.RAW
    },
		recentInfoMode:{
			type: String,
			default: DEF_MODE
		},    
  },
	data() {
		return {
			localData : {
				recentInfoMode : DEF_MODE
			}
		}
	},
	async mounted() {
		const _vm = this;

		_vm.settingApp();
    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		settingApp(){
			const _vm = this;
			
			switch(_vm.recentInfoMode){					
				case RECENT_INFO_MODE.LIST:
					_vm.localData.recentInfoMode = _vm.recentInfoMode;
					break;

				default:
					_vm.localData.recentInfoMode = RECENT_INFO_MODE.LIST;
					break;
			}
		},

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

		docReady() {
			const _vm = this;
		},
	}
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
#coching-recentInfo {

}
</style>
