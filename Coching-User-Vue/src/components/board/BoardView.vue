<template>
  <!--real-content-->
  <div class="real-content coching-board-view" id="coching-board-view">
    <div class="container-inner">
      <!--board-view-->
      <div class="board-view">
        <!--title-wrap-->
        <div class="title-wrap">
          <div class="left">
            <div class="title">{{boardData.title}}</div>
            <div class="info">
              <div>{{boardData.boardName}}</div>
              <div>
                <span>{{boardData.regName}}</span>
                <span>{{boardData.rgtDttm | fmtBoardDateDtail}}</span>
              </div>
            </div>
          </div>
          <div v-if=" (boardMstId == 'BD_RAW_001' || boardMstId == 'BD_RAW_002') && 
            (eumLoginUser && (boardData.rgtr == eumLoginUser.userSeq))" 
            class="right">
            <!--button-->
            <button 
              type="button" 
              class="btn btn-sm btn-primary modal-open-full request-data-modal" 
              @click="onClickEdit()">수정</button>
            <button 
              type="button" 
              class="btn btn-sm btn-gray-outline"
              @click="onClickDelete()">삭제</button>
          </div>
        </div>
        <!--board-view-content-->
        <div class="board-view-content">

          <!--file-wrap (공지사항 게시판일 때만 먼저 표시)-->
          <template v-if="isNoticeBoard">
            <div v-for="(file, fIdx) of fileData.filelist" :key="fIdx"
              class="data-content"
            >
              <ContinuousPdfPageImageViewer class="contents-pdf"
                v-if="isPdfViewContents(file.fileExt)"
                :fileId="file.fileId"
              />

              <div v-else class="contents-img contents-center">
                <img 
                :src="eumFileImagePath(file.fileId, '0', errorImg)"
                @error="onImageError"
                :alt="file.fileName"/>
              </div>
            </div>
          </template>

          <!--text-box-->
          <div
            class="text-box ql-editor"
            v-html="boardData.content"
          ></div>

          <!--file-wrap (공지사항 게시판이 아닐 때만 표시)-->
          <BoardFile 
            v-if="!isNoticeBoard"
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
          <!-- <button type="button" class="btn-list" @click="goEdit(boardSeq)">수정</button> -->
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

        <!-- boardMstId:{{boardMstId}} -->
      </div>

      <!--오류-->
			<AlertModal ref="alertModal"></AlertModal>
			<ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>    
  </div>  
</template>
<script>

import ernsUtils from '@/components/mixins/ernsUtils';
import boardMixin from './boardMixin';

import BoardComment from './BoardComment.vue';
import BoardFile from './BoardFile.vue';

import { DEF_FILE_INF, BOARD_MODE } from '@/constants/board';
import { getBoard, deleteBoard } from '@/api/coching/comm/board';

import ContinuousPdfPageImageViewer from '@/components/ContinuousPdfPageImageViewer.vue';
import ERROR_IMG from '@/assets/images/ic-img-lg.svg';

export default {
  name: 'coching-board-view',
  mixins: [ernsUtils, boardMixin],
  components: {
    BoardComment,
    BoardFile,
    ContinuousPdfPageImageViewer,
  },
  computed: {   
    errorImg(){
      return ERROR_IMG;
    },
    
    // 공지사항 게시판에서 파일을 내용보다 먼저 표시할지 여부
    isNoticeBoard() {
      return ['BD_WN_001', 'BD_WN_002', 'BD_NT_001', 'BD_NT_002'].includes(this.boardMstId);
    }
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
    isPdfViewContents(fileExt){
      const _vm = this;

      const upperfileExt = fileExt.toUpperCase();
      switch(upperfileExt){
        case "PDF":
        case "PPT":
        case "PPTX":
        case "DOC":
        case "DOCX":
          return true;
        default:
          return false;
      }      
    },

    onImageError(event) {
      const targetObj = event.target;
      targetObj.src = this.errorImg; // 대체 이미지로 설정
      targetObj.style.width = '100px'; // 원하는 너비 설정
      targetObj.style.height = '100px'; // 원하는 높이 설정
    },

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

    //수정 버튼
    onClickEdit() {
      const _vm = this;
      _vm.goEdit(_vm.boardSeq);
    },

    //삭제 버튼
    async onClickDelete() {
      const _vm = this;

      const ret = await _vm.$refs["confirmModal"].open({
        title: '게시글 삭제',
        content : '게시글을 삭제하시겠습니까?',
        okButtonText : '삭제',
        cancelButtonText : '취소'        
      });

      if(!ret){
        return;
      }

      _vm.loading(true);
      try {

       const params = {
          boardSeq : _vm.boardSeq,
          delYn : 'Y',
        }; 

        await deleteBoard(params);
        _vm.loading(false);

        await _vm.alertSuccess(`게시물이 삭제 되었습니다.`);   

        _vm.goList(1);
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

 }
}
</script>

<style lang="scss" scoped>
.coching-board-view {
  .contents-center {
    margin: 1rem 0;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .min-height-remove {
    min-height: 0 !important;
  }
}
</style>

<style lang="scss">
#coching-board.real-content.coching-board-view {
  .data-content {
    img {
      width: 100%;
    }
  }

  /* 게시글 내용에서 텍스트 스타일 태그들 원래대로 복원 */
  .ql-editor {
    strong {
      font-weight: bold !important;
    }
    
    em {
      font-style: italic !important;
    }
    
    u {
      text-decoration: underline !important;
    }
    
    s, strike {
      text-decoration: line-through !important;
    }
    
    cite {
      font-style: italic !important;
    }
  }
}
</style>
