<template>
  <!--content-->
  <div class="content-inner" id="coching-board-view">
    <!--board-view-->
    <div class="board-view">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">
          {{boardData.title}}
          <!-- boardSeq:{{ boardSeq }} -->
        </div>
        <div class="info">
          <span>{{boardData.regName}}</span>
          <span>{{boardData.rgtDttm | fmtBoardDateDtail}}</span>
        </div>
      </div>
      <!--board-view-content-->
      <div class="board-view-content">
        <!--text-box-->
        <div class="text-box">
          <p v-html="boardData.content"></p>
        </div>

        <!--file-wrap-->
        <BoardFile 
          v-show="fileData.filelist && fileData.filelist.length"
          pageType="read"
          :fileData="fileData"           
          @update:fileData="fileData = $event"></BoardFile>

        <!--comment-wrap-->
        <BoardComment
          v-show="boardData.cmtUseYn == 'Y'"
          :boardSeq="parseInt(boardSeq || '0', 10)"
          :boardMstId="boardMstId"
          :boardWriterSeq="boardData.rgtr"
          :boardCmtCnt="boardData.cmtCnt"
          :boardTitle="boardData.title"></BoardComment>
      </div>

      <div class="board-view-bottom">
        <button 
          v-if="boardData.next" 
          type="button" 
          class="btn-left" 
          @click="goView(boardData.next)"></button>
        <button type="button" class="btn-list" @click="goList(1)">목록</button>
        <button 
          v-if="boardData.prev"
          type="button"
          class="btn-right" 
          @click="goView(boardData.prev)"></button>
      </div>
    </div>
  </div>  
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from './boardMixin';

import BoardComment from './BoardComment.vue';
import BoardFile from './BoardFile.vue';

import { DEF_FILE_INF, BOARD_MODE } from '@/constants/board';
import { getBoard } from '@/api/coching/comm/board';

export default {
  name: 'coching-board-view',
  mixins: [ernsUtils, boardMixin],
  components: {
    BoardComment,
    BoardFile,
  },
  computed: {    
  },
  watch: {
    boardSeq(newVal, oldVal) {
      const _vm = this;
      _vm.fetchData();
    },
  },
  props: {
    boardSeq: {
      type: [Number, String],
      required: true
    },
    boardMstId: {
      type: String,
      required: true
    }
  }, 
  data() {
    return {
      boardData : {},
      fileData : {...DEF_FILE_INF},
      cmtData : [],
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
        await _vm.loadDetail();

      } catch (err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    /* 게시글 상세 정보 가져오기 */
    async loadDetail() {
      const _vm = this;

      _vm.loading(true);
      try {
        const params = _vm.getBoardSearchParam();
        const boardRes = await getBoard(params);

        //데이터 바인딩
        _vm.boardData = {
          ...boardRes.resultData, 
          content: boardRes.resultData.content.replace(/\n/g, '<br>')
        };
        _vm.fileData.filelist = boardRes.resultData.filelist;

        //dataMap.backupList = JSON.parse(JSON.stringify(resData.list));
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    //검색조건
    getBoardSearchParam() {
      const _vm = this;

      const retParam = {
        boardSeq : _vm.boardSeq,
        boardMstId : _vm.boardMstId,
        delYn : 'N',
      };

      return retParam;
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
#coching-board-view {

}
</style>
