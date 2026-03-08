<template>
 <div class="board-view">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="left">
          <div class="title">{{boardData.title}}</div>
          <div class="info">
            <div>
              {{boardData.cateNm}}
            </div>
            <div>
              <span>{{boardData.regName}}</span>
              <span>{{boardData.rgtDttm | fmtBoardDateDtail}}</span>
            </div>
          </div>
        </div>
      </div>
      <!--board-view-content-->
      <!--마지막 컨텐츠 라인이 없을 경우 border 추가-->
      <div class="board-view-content border">
        <!--text-box-->
        <div class="text-box">
          <p v-html="boardData.content"></p>
        </div>

        <BoardFile 
            v-show="fileData.filelist && fileData.filelist.length"
            pageType="read"
            :fileData="fileData"           
            @update:fileData="fileData = $event"></BoardFile>

        <!--answer-box 대기전이면 삭제-->
        <div class="answer-box"
          v-if="Object.keys(replyData).length > 0"
        >
          <!--header-->
          <div class="header">
            <div class="answer">A</div>
            <div class="title-wrap">
              <div class="title">{{replyData.title}}</div>
              <div class="date">{{replyData.rgtDttm | fmtBoardDateDtail}}</div>
            </div>
          </div>
          <!--body-->
          <div class="body">
            <p v-html="replyData.content"></p>
          </div>
        </div>
      </div>
      <!--board-view-bottom-->
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

      <AlertModal ref="alertModal"></AlertModal>
			<ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from './boardMixin';

import BoardFile from './BoardFile.vue';

import { DEF_FILE_INF, BOARD_MODE } from '@/constants/board';
import { getInqrBoard, getInqrBoardReply } from '@/api/coching/comm/board';

export default {
  name: 'coching-inqr-board-view',
  mixins: [ernsUtils, boardMixin],
  components: {
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
      replyData : {},
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
        const boardRes = await getInqrBoard(params);

        //데이터 바인딩
        _vm.boardData = {
          ...boardRes.resultData, 
          content: boardRes.resultData.content.replace(/\n/g, '<br>')
        };

        _vm.fileData.filelist = boardRes.resultData.filelist;

        
        //답변
        const replyRes = await getInqrBoardReply(params);
        
        if(replyRes.resultData) {
          _vm.replyData = {
            ...replyRes.resultData, 
            content: replyRes.resultData.content.replace(/\n/g, '<br>')
          };
        }

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
        locale : _vm.$i18n.locale,
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
</style>
