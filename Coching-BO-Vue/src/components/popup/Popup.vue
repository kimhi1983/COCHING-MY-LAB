<template>
	<!-- <div>
		popupMode:{{ popupMode }}<br/>
		localData.popupMode:{{ localData.popupMode }}<br/>		
		popupSeq:{{ popupSeq }}<br/>		 -->
		<!-- <PopupView 
			v-if="localData.popupMode == 'view'"
			:key="popupSeq"
			id="coching-popup"
			v-bind="$props"
		></PopupView> -->
		<PopupWrite 
			v-if="
				popupMstCd != 'Popup_0002' &&
				(
					localData.popupMode == 'write' ||
					localData.popupMode == 'edit'
				)
			"
			id="coching-popup"
			v-bind="$props"
		></PopupWrite>		
		<PopupList
			v-else
			id="coching-popup"
			v-bind="$props"
		></PopupList>
	<!-- </div> -->
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import popupMixin from './popupMixin';
import { POPUP_MODE } from '@/constants/popup';

import PopupList from './PopupList.vue';
import PopupWrite from './PopupWrite.vue';

const DEF_POPUPMODE = POPUP_MODE.LIST;

export default {
	name: 'coching-popup-basic-main',
	mixins: [ernsUtils, popupMixin],
	components: {
    PopupList,		
		PopupWrite,
  },
	computed: {    
  },
	watch: {
		'$route.params'(newVal, oldVal) {
			this.settingPopup();
		}
	},
	props: {
		popupMode:{
			type: String,
			default: DEF_POPUPMODE
		},
    popupMstCd:{
      type : String,
			require: true,
    },
		popupSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
			localData : {
				popupMode : DEF_POPUPMODE
			}
		}
	},
	async mounted() {
		const _vm = this;

		_vm.settingPopup();
    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		settingPopup(){
			const _vm = this;
			
			switch(_vm.popupMode){
				case POPUP_MODE.EDIT:
				case POPUP_MODE.VIEW:
					const popupSeq = parseInt("" +_vm.popupSeq || "0", 10);
					if(popupSeq <= 0){
						console.warn(`popupSeq is not valid : ${_vm.popupSeq}`);
						_vm.goList(1, true);
						return;
					}

					_vm.localData.popupMode = _vm.popupMode;
					break;

				case POPUP_MODE.WRITE:				
				case POPUP_MODE.LIST:
					_vm.localData.popupMode = _vm.popupMode;
					break;

				default:
					_vm.localData.popupMode = POPUP_MODE.LIST;
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
#coching-popup {

}
</style>
