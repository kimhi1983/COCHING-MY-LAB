<template>
	<!-- <div>
		bannerMode:{{ bannerMode }}<br/>
		localData.bannerMode:{{ localData.bannerMode }}<br/>		
		bannerSeq:{{ bannerSeq }}<br/>		 -->
		<!-- <BannerView 
			v-if="localData.bannerMode == 'view'"
			:key="bannerSeq"
			id="coching-banner"
			v-bind="$props"
		></BannerView> -->
		<BannerWrite 
			v-if="
				bannerMstCd != 'BANNER_0002' &&
				(
					localData.bannerMode == 'write' ||
					localData.bannerMode == 'edit'
				)
			"
			id="coching-banner"
			v-bind="$props"
		></BannerWrite>
		<BannerAdWrite 
			v-else-if="
				bannerMstCd == 'BANNER_0002' &&
				(
				localData.bannerMode == 'write' ||
				localData.bannerMode == 'edit'
				)
			"
			id="coching-banner"
			v-bind="$props"
		></BannerAdWrite>
		<BannerAdList
			v-else-if="
				bannerMstCd == 'BANNER_0002' &&
				localData.bannerMode == 'list'
			"
			id="coching-banner"
			v-bind="$props"
		></BannerAdList>
		<BannerList
			v-else
			id="coching-banner"
			v-bind="$props"
		></BannerList>
	<!-- </div> -->
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import bannerMixin from './bannerMixin';
import { BANNER_MODE } from '@/constants/banner';

import BannerList from './BannerList.vue';
import BannerWrite from './BannerWrite.vue';
import BannerAdList from './BannerAdList.vue';
import BannerAdWrite from './BannerAdWrite.vue';

const DEF_BANNERMODE = BANNER_MODE.LIST;

export default {
	name: 'coching-banner-basic-main',
	mixins: [ernsUtils, bannerMixin],
	components: {
    BannerList,		
		BannerWrite,
		BannerAdList,
		BannerAdWrite,
  },
	computed: {    
  },
	watch: {
		'$route.params'(newVal, oldVal) {
			this.settingBanner();
		}
	},
	props: {
		bannerMode:{
			type: String,
			default: DEF_BANNERMODE
		},
    bannerMstCd:{
      type : String,
			require: true,
    },
		bannerSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
			localData : {
				bannerMode : DEF_BANNERMODE
			}
		}
	},
	async mounted() {
		const _vm = this;

		_vm.settingBanner();
    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		settingBanner(){
			const _vm = this;
			
			switch(_vm.bannerMode){
				case BANNER_MODE.EDIT:
				case BANNER_MODE.VIEW:
					const bannerSeq = parseInt("" +_vm.bannerSeq || "0", 10);
					if(bannerSeq <= 0){
						console.warn(`bannerSeq is not valid : ${_vm.bannerSeq}`);
						_vm.goList(1, true);
						return;
					}

					_vm.localData.bannerMode = _vm.bannerMode;
					break;

				case BANNER_MODE.WRITE:				
				case BANNER_MODE.LIST:
					_vm.localData.bannerMode = _vm.bannerMode;
					break;

				default:
					_vm.localData.bannerMode = BANNER_MODE.LIST;
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
#coching-banner {

}
</style>
