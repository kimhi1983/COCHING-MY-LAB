<template>
  <!--comment-wrap-->
  <div class="comment-wrap">
    <div class="total-wrap">
      <!-- <div class="total-num">댓글<span>{{boardCmtCnt}}</span></div> -->
    </div>
    <!--comment-list-->
    <div class="comment-list">
      <!--item-->
      <div v-for="(item, idx) of cmtData" 
        :key="item.boardCmtSeq + '-' + item.isEditMode"
        class="item">
        <!--inner-->
        <div class="inner">
          <div v-if="!item.isEditMode">
            <div class="header">
              <div class="left">
                <div class="user">{{item.regName || ''}}({{item.regId}})</div>
                <div class="date">{{item.rgtDttm | fmtBoardDate}}</div>
              </div>
              <!--
              <div v-if="item.delYn == 'N'" class="more-container">
                <div class="ic-wrap more-wrap" @click="onClickCmtDrop($event)">
                  <div class="ic-lg ic-more" data-template="more"></div>
                </div>
                <div class="dropdown for-more">
                  <a v-if="item.isUpdate"
                    href="javascript:;" class="btn-edit" @click="onClickCmtEdit(item)">수정</a>
                  <a v-if="item.isUpdate" href="javascript:;" @click="onClickDelete(item)">삭제</a>
                  <a href="javascript:;" class="modal-open send-input-modal" @click="onClickSendMessage(item)">쪽지 보내기</a>
                </div>
              </div>
              -->
            </div>
            <div class="body">
              <div :class="{ 'comment-lock': item.secretYn == 'Y' }">
                <span v-if="item.delYn === 'Y'"><b>[삭제된 댓글입니다.]</b></span> 
                <span>{{ item.content }}</span>
              </div>
            </div>
          </div>
          <!-- 수정
          <div v-if="item.isEditMode">
            <div class="comment-edit-wrap">
              <div class="comment-box">
                <textarea v-model="item.content" rows="5" placeholder="댓글을 입력해 주세요"></textarea>
                <div class="bottom">
                  <div class="checkbox">
                    <input :id="'secret-' + item.boardCmtSeq" type="checkbox" 
                      v-model="item.secretYn"
                      :true-value="'Y'"
                      :false-value="'N'"
                    />
                    <label :for="'secret-' + item.boardCmtSeq" class="checkbox-label">비밀글</label>
                  </div>
                  <div class="right">
                    <button type="button" class="btn btn-sm btn-gray-outline btn-cancel" @click="onClickCmtCancel(item)">취소</button>
                    <button type="submit" class="btn btn-sm btn-primary" @click="onClickUpdate(item)">수정</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          -->

          <!--reply-box
          <div class="reply reply-box">
            <form>
              <div class="comment-box">
                <div class="bottom">
                  <div class="checkbox">
                    <input :id="'secret-reply-' + item.boardCmtSeq" type="checkbox" 
                      v-model="item.newreply.secretYn"
                      :true-value="'Y'"
                      :false-value="'N'"
                    />
                    <label :for="'secret-reply-' + item.boardCmtSeq" class="checkbox-label">비밀글</label>
                  </div>
                    <button type="button" class="btn btn-sm btn-primary" @click="onClickSave(item)">등록</button>
                </div>
              </div>
            </form>
          </div>
          -->
        </div>

        <!-- reply-list -->
        <div v-if="item.children.length" class="reply-list">
          <div v-for="(reply, idx) of item.children"
            :key="reply.boardCmtSeq + '-' + reply.isEditMode"
            class="item">
            <!--inner-->
            <div class="inner">
              <div v-if="!reply.isEditMode">
                <div class="header">
                  <div class="left">
                    <div class="user">{{reply.regName || ''}}({{reply.regId}})</div>
                    <div class="date">{{reply.rgtDttm | fmtBoardDate}}</div>
                  </div>
                  <!--
                  <div v-if="reply.delYn == 'N'" class="more-container">
                    <div class="ic-wrap more-wrap" @click="onClickCmtDrop($event)">
                      <div class="ic-lg ic-more" data-template="more"></div>
                    </div>
                    <div class="dropdown for-more">
                      <a v-if="reply.isUpdate"
                        href="javascript:;" class="btn-edit" @click="onClickCmtEdit(reply)">수정</a>
                      <a v-if="reply.isUpdate" href="javascript:;" @click="onClickDelete(reply)">삭제</a>
                      <a href="javascript:;" @click="onClickSendMessage(reply)">쪽지 보내기</a>
                    </div>
                  </div>
                  -->
                </div>
                <div class="body">
                  <div :class="{ 'comment-lock': reply.secretYn == 'Y' }">
                    <span v-if="reply.delYn === 'Y'"><b>[삭제된 댓글입니다.] </b></span> 
                    <span>{{ reply.content }}</span>
                  </div>
                </div>
              </div>
              <!-- 대댓글 수정
              <div v-if="reply.isEditMode">
                <div class="comment-edit-wrap">
                  <div class="comment-box">
                    <textarea v-model="reply.content" rows="5" placeholder="댓글을 입력해 주세요"></textarea>
                    <div class="bottom">
                      <div class="checkbox">
                        <input :id="'secret-reply-' + reply.boardCmtSeq" type="checkbox" 
                          v-model="reply.secretYn"
                          :true-value="'Y'"
                          :false-value="'N'"
                        />
                        <label :for="'secret-reply-' + reply.boardCmtSeq" class="checkbox-label">비밀글</label>
                      </div>
                      <div class="right">
                        <button type="button" class="btn btn-sm btn-gray-outline btn-cancel" @click="onClickCmtCancel(reply)">취소</button>
                        <button type="submit" class="btn btn-sm btn-primary" @click="onClickUpdate(reply)">수정</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
               -->
            </div>
          </div>
        </div>  
      </div>
      <!--comment-editor
      <div class="comment-editor">
        <form>
          <div class="comment-box">
            <textarea 
              v-model="newCmtData.content"
              rows="5" placeholder="댓글을 입력해 주세요"></textarea>
            <div class="bottom">
              <div class="checkbox">
                <input id="secret" type="checkbox"
                  v-model="newCmtData.secretYn"
                  :true-value="'Y'"
                  :false-value="'N'"
                />
                <label for="secret" class="checkbox-label">비밀글</label>
              </div>
              <button type="button" class="btn btn-sm btn-primary" @click="onClickSave(null)">등록</button>
            </div>
          </div>
        </form>
      </div> 
      -->

    </div>
  </div>
</template>


<script>
import ernsUtils from "@/components/mixins/ernsUtils";
//import { EventBus } from '@/components/eventBus';

import boardMixin from "@/components/board/boardMixin";
import { getBoardCmt } from "@/api/coching-bo/comm/board";

export default {
  name: "BoardComment",
  mixins: [ernsUtils, boardMixin],
  computed: {},
  watch: {
    $route: "fetchData", //라우트가 변경되면 메소드를 다시 호출됩니다.
    /*locale 변경감지*/
    "$i18n.locale": function () {
      this.isLoadTerms = false;
    }, //다시로드하도록 처리
  },
  props: {
    boardSeq: {
      type: Number,
      default: 0,
    },
  },
  async mounted() {
    const _vm = this;

    _vm.fetchData();
  },
  data() {
    return {
      boardData: {
        boardSeq: 0,
        boardMstId: "",
      },
	  cmtData : [],
    };
  },
  methods: {
    async fetchData() {
      const _vm = this;

      _vm.loading(true);

      try {
        await _vm.loadCmtList();
      } catch (err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    async loadCmtList() {
      const _vm = this;

      _vm.loading(true);
      try {
        const cmtParams = _vm.getCmtSearchParam();
        const cmtRes = await getBoardCmt(cmtParams);

        var loginSeq = _vm.eumLoginUser ? _vm.eumLoginUser.userSeq : ""; //로그인유저 Seq
        const updatedCmtData = cmtRes.resultData.list.map((item) => ({
          ...item,
          /*
            isUpdate: loginSeq == item.rgtr, //수정가능 여부 (로그인유저==댓글작성자)
            isSecretView: loginSeq == item.rgtr || loginSeq == _vm.boardWriterSeq, //비밀글조회 여부 (로그인유저==댓글작성자 OR 글작성자)
            isEditMode: false, //수정 버튼용
            newreply: {content: '', secretYn: 'N'}, //답글용
			    */
        }));

        _vm.cmtData = _vm.buildTree(updatedCmtData);
      } catch (err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    getCmtSearchParam() {
      const _vm = this;

      const retParam = {
        //boardMstId : _vm.boardMstId,
        boardSeq: _vm.boardSeq,
        //delYn : 'N',
      };
      return retParam;
    },

    //댓글 데이터 트리구조로 만들기
    buildTree(data) {
      const map = {};
      const tree = [];

      //데이터를 ID 기반으로 맵핑
      data.forEach((item) => {
        map[item.boardCmtSeq] = { ...item, children: [] };
      });

      //계층 구조 생성
      data.forEach((item) => {
        if (item.refId === item.boardCmtSeq) {
          //최상위 댓글은 트리의 루트에 추가
          tree.push(map[item.boardCmtSeq]);
        } else {
          //대댓글은 부모의 children에 추가
          if (map[item.refId]) {
            map[item.refId].children.push(map[item.boardCmtSeq]);
          }
        }
      });

      return tree;
    },

    /*
    //등록
    async onClickSave(item) {

      const _vm = this;

      if(!_vm.eumLoginUser) { //로그인 정보가 없을 때 return
        _vm.alertError('로그인 해주세요');
        return;
      };

      var newCmt = {
        boardSeq: _vm.boardSeq,
        content: item ? item.newreply.content: _vm.newCmtData.content,
        secretYn: item ? item.newreply.secretYn : _vm.newCmtData.secretYn,
        refId: item ? item.boardCmtSeq : null,
        delYn: 'N',
        adminDelCd: 'N',
        replyYn: item ? 'Y' : 'N', //대댓글 여부
        boardWriterSeq: _vm.boardWriterSeq, //(알림용)글작성자 seq
        cmtWriterSeq: item ? item.rgtr : 0, //(대댓글 알림용)댓글작성자 seq
        boardTitle: _vm.boardTitle, //(알림용)글제목
      };

      _vm.loading(true);
      try {
          const writeRes = await setBoardCmt(newCmt);

          _vm.loading(false);
          await _vm.alertSuccess(`댓글을 등록 했습니다.`);

          const data = writeRes.resultData;
          console.log(data);

          await _vm.loadCmtList();

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },

    //수정
    async onClickUpdate(item) {
      const _vm = this;

      if(!_vm.eumLoginUser) { //로그인 정보가 없을 때 return
        _vm.alertError('로그인 해주세요');
        return;
      };

      var newCmt = {
        boardCmtSeq: item.boardCmtSeq,
        content: item.content,
        secretYn: item.secretYn,
      };

      _vm.loading(true);
      try {
          const editRes = await updateBoardCmt(newCmt);

          _vm.loading(false);
          await _vm.alertSuccess(`댓글을 수정 했습니다.`);

          const data = editRes.resultData;
          console.log(data);

          await _vm.loadCmtList();

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },
	*/

    /* 대댓글 버튼 동작
      1. 답글달기
      2. ... 드롭다운
      3. ... 쪽지보내기
    //1. 답글달기
    onClickReply(event) {
      $(event.target).closest(".inner").find(".reply-box").toggleClass("active");
    },

    //$(this).closest(".more-container").find(".for-more").removeClass("on");

    //2. ... 드롭다운
    onClickCmtDrop(event) {
      const target = $(event.target).closest(".more-container").find(".for-more");
      const hasOn = target.hasClass("on");
      if(hasOn) {
        target.removeClass('on');
      } else {
        $(".more-container .for-more").removeClass("on"); //기존 항목 드롭다운 제거
        target.addClass('on');
      }
    },

    //2-1. 드롭다운 수정
    onClickCmtEdit(item) {
      item.isEditMode = true;
    },
    //2-2. 드롭다운 수정 취소
    onClickCmtCancel(item) {
      item.isEditMode = false;
    },

    //2-2. 드롭다운 수정 저장

    //쪽지보내기
    onClickSendMessage(item){
      const _vm = this;

      if(!_vm.eumLoginUser) { //로그인 정보가 없을 때 return
        _vm.alertError('로그인 해주세요');
        return;
      };

      EventBus.$emit('open-message-modal', {
        membSeq: item.rgtr,
        membName: item.writer,
      });
    
    },
	*/
  },
};
</script>

<style lang="scss" scoped>
/*
.comment-list .no-hover {
	background-color: transparent !important; 
}

.comment-list .hide {
	display: none !important;
}
*/

.comment-wrap {
  /*comment-wrap*/
  .total-wrap {
    padding-bottom: 0;
  }

  /*comment-list*/
  .comment-list > .item {
    padding: 2rem 0;
    border-bottom: 1px solid #eeeeee;
  }

  .comment-list > .item .header {
    
    position: relative;
    padding-bottom: 0.75rem;
  }
  .comment-list > .item .header .left {
    
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.5rem;
    width: calc(100% - 2.5rem);
  }
  .comment-list > .item .header .user {
    font-size: 0.875rem;
    font-weight: 700;
    word-break: break-all;
  }
  .comment-list > .item .header .date {
    font-size: 0.875rem;
    color: #999999;
  }
  .comment-list > .item .header .writer {
    font-size: 0.75rem;
    font-weight: 700;
    color: var(--color--primary);
    padding: 0.125rem 0.375rem;
    border-radius: 0.1875rem;
    background-color: rgba(255, 56, 92, 0.08);
  }
  /* .comment-list .more-wrap {
    position: absolute;
    right: 0;
    top: -0.75rem;
  } */

  /*more-container*/
  .more-container {
    position: absolute;
    right: 0;
    top: -0.75rem;
  }
  /*for-more*/
  .more-container .for-more {
    inset: 0px 0px auto auto;
    //transform: translate(-50px, -39px);
  }

  .more-container .for-more a {
    text-align: center;
  }
  .comment-list > .item .body {
    font-size: 0.9375rem;
    font-weight: normal;
    line-height: 1.6;
  }
  @media (max-width: 767px) {
    .comment-list > .item {
      padding: 1rem 0;
    }

    .comment-list > .item .body {
      font-size: 0.875rem;
    }
  }

  /*btn-comment-wrap*/
  .btn-comment-wrap {
    padding-top: 0.875rem;
  }
  .btn-comment {
    color: #999999;
    font-size: 0.875rem;
    font-weight: 500;
    padding: 0.25rem 0;
  }

  /*comment-editor*/
  .comment-editor {
    padding-top: 2rem;
  }
  .comment-box {
    border: 1px solid #dddddd;
    border-radius: 0.25rem;
    overflow: hidden;
  }
  .comment-box .bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.625rem 1rem;
    border-top: 1px solid #dddddd;
    background-color: #ffffff;
  }
  .comment-box .bottom .right {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  .comment-box .bottom .right .btn-sm {
    height: 2.25rem;
  }
  .comment-box textarea {
    border: 0;
  }
  @media (max-width: 767px) {
    .comment-editor {
      padding-top: 1rem;
    }

    .comment-box .bottom {
      padding: 0.5rem 1rem;
    }
    .comment-box .bottom .right {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
    .comment-box textarea {
      height: 7.5rem;
    }
  }
  /*reply*/
  .reply {
    padding: 1.5rem;
    background-color: #f9f9f9;
    margin-top: 1rem;
  }
  .reply-box {
    display: none;
  }
  .reply-box.active {
    display: block;
  }
  @media (max-width: 767px) {
    .reply {
      padding: 1rem;
    }
  }

  /*comment-edit-wrap*/
  .comment-edit-wrap {
    background-color: #f9f9f9;
    padding: 1.5rem;
  }
  @media (max-width: 767px) {
    .comment-edit-wrap {
      padding: 1rem;
    }
  }

  /*reply-list*/
  .reply-list {
    margin-top: 1rem;
    background-color: #f9f9f9;
    padding: 0 1.5rem;
  }
  .reply-list > .item {
    padding: 1.5rem 0;
  }
  .reply-list > .item:nth-child(n + 2) {
    border-top: 1px solid #eeeeee;
  }
  .reply-list .comment-edit-wrap {
    background-color: transparent;
    padding: 0;
  }
  @media (max-width: 767px) {
    .reply-list {
      margin-top: 1rem;
      background-color: #f9f9f9;
      padding: 0 1rem;
    }
    .reply-list > .item {
      padding: 1rem 0;
    }
  }

  /*comment-lock*/
  .comment-lock {
    color: #666666;
    display: flex;
    gap: 0.5rem;
    align-items: center;
  }
  .comment-lock span {
    //width: calc(100% - 1.5rem);
  }
  .comment-lock:before {
    content: "";
    width: 1rem;
    height: 1rem;
    background-image: url("~@/assets/images/coching/ic-lock-gray-sm.svg");
    display: inline-block;
  }
}

</style>

<style lang="scss">
</style>