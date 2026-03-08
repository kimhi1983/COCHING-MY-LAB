<template>
	<!--section-->
	<section id="coching-faq-board">
		<div class="container">
      <div class="content-wrap">
        <!--title-->
        <div class="h1">자주 묻는 질문</div>
        
        <Board 
					v-bind="$props"
					:boardMstId="boardMstId"
				></Board>
      </div>
    </div>
	</section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import Board from '@/components/board/Board.vue';
import boardMixin from '@/components/board/boardMixin';
import { BOARD_ID, BOARD_MODE } from '@/constants/board';

export default {
	name: 'coching-faq-board',
	mixins: [ernsUtils, boardMixin],
	components: {	
    Board
	},
	computed: {		
	},
	watch: {
	},
	props: {
		boardMode:{
			type: String,
			default: BOARD_MODE.LIST
		},
		boardSeq:{
      type : [String, Number],
			default : 0
    },
  },
	data() {
		return {
			boardMstId : BOARD_ID.FAQ_KO
		}
	},
	async mounted() {
		const _vm = this;

		_vm.fetchData();		
		_vm.docReady();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		//데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
		async fetchData() {
      const _vm = this;

			_vm.loading(true);
			try {
				_vm.boardMstId = _vm.getBoardId_faq();
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
#coching-faq-board {

}
</style>
