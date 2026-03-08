<template>
  <Board 
    v-bind="$props"
  ></Board>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';

import Board from '@/components/board/notice/Board.vue';
import boardMixin from '@/components/board/boardMixin';
import { BOARD_ID, BOARD_MODE } from '@/constants/board';

export default {
  name: 'coching-BackOffice-Notice',
  mixins: [ernsUtils, boardMixin],
  components : {
    Board
  },
  directives: {
  },
  props: {
    boardMstId: {
      type: String,
      require: true
    },
    boardMode:{
			type: String,
			default: BOARD_MODE.LIST
		},
    boardSeq:{
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
