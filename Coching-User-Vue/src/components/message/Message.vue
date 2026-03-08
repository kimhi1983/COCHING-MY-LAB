<template>
	<!-- <div>
		messageMode:{{ messageMode }}<br/>
		localData.messageMode:{{ localData.messageMode }}<br/>		
		messageSeq:{{ messageSeq }}<br/>		 -->
		<MessageList
			id="coching-message"
			v-bind="$props"
		></MessageList>
	<!-- </div> -->
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import messageMixin from './messageMixin';
import { MESSAGE_MODE, MESSAGE_OWN_MODE } from '@/constants/message';

import MessageList from './MessageList.vue';

const DEF_MODE = MESSAGE_MODE.LIST;

export default {
	name: 'coching-message-main',
	mixins: [ernsUtils, messageMixin],
	components: {
    MessageList
  },
	computed: {    
  },
	watch: {
		'$route.params'(newVal, oldVal) {
			this.settingApp();
		}
	},
	props: {
    ownMode: {
      type: String,
      default: MESSAGE_OWN_MODE.RECIVED
    },
		messageMode:{
			type: String,
			default: DEF_MODE
		},
    messageSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
			localData : {
				messageMode : DEF_MODE
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
			
			switch(_vm.messageMode){				
				case MESSAGE_MODE.LIST:
					_vm.localData.messageMode = _vm.messageMode;
					break;

				default:
					_vm.localData.messageMode = MESSAGE_MODE.LIST;
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
#coching-message {

}
</style>
