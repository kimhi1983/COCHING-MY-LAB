<template>
	<!-- <div>
		boardMode:{{ boardMode }}<br/>
		localData.boardMode:{{ localData.boardMode }}<br/>		
		boardSeq:{{ boardSeq }}<br/>		 -->
		<BoardView 
			v-if="localData.boardMode == 'view'"
			:key="boardSeq"
			id="coching-board"
			v-bind="$props"
		></BoardView>
		<BoardWrite 
			v-else-if="
				localData.boardMode == 'write' ||
				localData.boardMode == 'edit'
			"
			id="coching-board"
			v-bind="$props"
		></BoardWrite>
		<BoardList v-else
			id="coching-board"
			v-bind="$props"
		></BoardList>
	<!-- </div> -->
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from './boardMixin';
import { BOARD_MODE } from '@/constants/board';

import BoardList from './BoardList.vue';
import BoardView from './BoardView.vue';
import BoardWrite from './BoardWrite.vue';

const DEF_BOARDMODE = BOARD_MODE.LIST;

export default {
	name: 'coching-board-main',
	mixins: [ernsUtils, boardMixin],
	components: {
    BoardList,
		BoardView,
		BoardWrite,
  },
	computed: {    
  },
	watch: {
		'$route.params'(newVal, oldVal) {
			this.settingBoard();
		}
	},
	props: {
		boardMode:{
			type: String,
			default: DEF_BOARDMODE
		},
    boardMstId:{
      type : String
    },
		boardSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
			localData : {
				boardMode : DEF_BOARDMODE
			}
		}
	},
	async mounted() {
		const _vm = this;

		_vm.settingBoard();
    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		settingBoard(){
			const _vm = this;
			
			switch(_vm.boardMode){
				case BOARD_MODE.EDIT:
				case BOARD_MODE.VIEW:
					const boardSeq = parseInt("" +_vm.boardSeq || "0", 10);
					if(boardSeq <= 0){
						console.warn(`boardSeq is not valid : ${_vm.boardSeq}`);
						_vm.goList(1, true);
						return;
					}

					_vm.localData.boardMode = _vm.boardMode;
					break;

				case BOARD_MODE.WRITE:				
				case BOARD_MODE.LIST:
					_vm.localData.boardMode = _vm.boardMode;
					break;

				default:
					_vm.localData.boardMode = BOARD_MODE.LIST;
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
#coching-board {

}
</style>
