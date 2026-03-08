<template>
  <!--section-->
  <section id="coching-board-faq">
		<div class="container h-100">
			<div class="content">
				<!--common-content-->
				<div class="common-content">

					<BoardMenuBar :boardMode="boardMode" />

					<Board 
						v-bind="$props"
						:boardMstId="boardMstId"
					></Board>
				</div>
			</div>
		</div>
	</section>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import BoardMenuBar from '@/views/coching/board/BoardMenuBar.vue';
import Advertise from '@/components/Advertise.vue';

import Board from '@/components/board/Board.vue';
import boardMixin from '@/components/board/boardMixin';
import { BOARD_ID, BOARD_MODE } from '@/constants/board';


export default {
	name: 'coching-board-faq',
	mixins: [ernsUtils, boardMixin],
	components: {
    BoardMenuBar,
    Advertise,
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
			boardMstId : BOARD_ID.FAQ_KO,
			hasBanner: false, // 광고 배너 유무
		}
	},
	async mounted() {
		const _vm = this;

    _vm.docReady();
    _vm.fetchData();
	},
	beforeDestroy() {
		const _vm = this;
	},
	methods: {
		/* 데이터 가져오는 부분(사용하지 필요없어도 반드시 선언) */
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
#coching-board-faq {

}
</style>
