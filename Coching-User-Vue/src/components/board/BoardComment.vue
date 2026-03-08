<template>
  <!--comment-wrap-->
  <div class="comment-wrap">
    <div class="total-wrap">
      <div class="total-num">댓글<span>{{boardCmtCnt}}</span></div>
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
                <div v-if="showCmtContent(item)" class="user">
                  <span>{{item.regName}}</span>
                </div>
                <div v-else class="user">
                  <span>익명</span> 
                </div>
                <div class="date">{{item.rgtDttm | fmtBoardDate}}</div>
                <div class="writer" v-if="showCmtContent(item) && (item.rgtr === boardWriterSeq)">작성자</div>
              </div>
              <div v-if="item.delYn == 'N' && showCmtContent(item)" class="more-container">
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
            </div>
            <div class="body">
              <div v-if="item.delYn === 'Y'">
                <span>[삭제된 댓글입니다.]</span> 
              </div>
              <div v-else-if="!showCmtContent(item)" class="comment-lock">
                <span>비밀 댓글입니다. 글쓴이와 댓글 작성자만 열람할 수 있습니다.</span>
              </div>
              <div v-else>
                <div :class="{ 'comment-lock': item.secretYn == 'Y' }">
                  <span>{{ item.content }}</span>
                </div>
                <div class="btn-comment-wrap">
                  <a href="javascript:;" class="btn-comment" @click="onClickReply($event)">답글달기</a>
                </div>
              </div>
            </div>
          </div>
          <!-- 수정 -->
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

          <!--reply-box-->
          <!-- 대댓글 등록 -->
          <div class="reply reply-box">
            <form>
              <div class="comment-box">
                <textarea 
                  v-model="item.newreply.content"
                  rows="5" placeholder="댓글을 입력해 주세요"
                  ></textarea>
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
                    <div v-if="showCmtContent(reply, item)" class="user">
                      <span>{{reply.regName}}</span>
                    </div>
                    <div v-else class="user">
                      <span>익명</span> 
                    </div>
                    <div class="date">{{reply.rgtDttm | fmtBoardDate}}</div>
                    <div class="writer" v-if="showCmtContent(reply, item) && (reply.rgtr === boardWriterSeq)">작성자</div>
                  </div>
                  <div v-if="reply.delYn == 'N' && showCmtContent(reply, item)" class="more-container">
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
                </div>
                <div class="body">
                  <div v-if="reply.delYn === 'Y'">
                    <span>[삭제된 댓글입니다.]</span> 
                  </div>
                  <div v-else-if="!showCmtContent(reply, item)" class="comment-lock">
                    <span>비밀 댓글입니다. 글쓴이와 댓글 작성자만 열람할 수 있습니다.</span>
                  </div>
                  <div v-else>
                    <div :class="{ 'comment-lock': reply.secretYn == 'Y' }">
                      <span>{{reply.content}}</span>
                    </div>
                  </div>
                </div>
              </div>
              <!-- 대댓글 수정 -->
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
            </div>
          </div>
        </div>  
      </div>
      <!--comment-editor-->
      <!-- 등록 -->
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
              <!--btn-disabled 추가시 비활성화-->
            </div>
          </div>
        </form>
      </div> 

      <!--오류-->
			<AlertModal ref="alertModal"></AlertModal>
			<ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { EventBus } from '@/components/eventBus'; 

import boardMixin from '@/components/board/boardMixin';
import { getBoardCmt, setBoardCmt, updateBoardCmt, deleteBoardCmt } from '@/api/coching/comm/board';

export default {
  name: 'BoardComment',
  mixins: [ernsUtils, boardMixin],
  computed: {
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.
    /*locale 변경감지*/
    ,'$i18n.locale' : function(){this.isLoadTerms=false;} //다시로드하도록 처리
  },
  props:{
    boardSeq: {
      type: Number,
      default: 0
    },
    boardMstId: {
      type: String,
      default: ''
    },
    boardWriterSeq: {
      type: Number,
      default: 0
    },
    boardCmtCnt: {
      type: Number,
      default: 0
    },
    boardTitle: {
      type: String,
      default: ''
    },
  },
  async mounted(){
    const _vm = this;

    _vm.fetchData();    
  }, 
  data(){
    return {
      boardData : {
        boardSeq : 0,
        boardMstId : '',
        cmtCnt : 0,
        writerSeq : 0
      },
      cmtData : [],

      newCmtData : {
        content: '',
        secretYn: 'N'
      }, //신규 댓글 관리
    }
  },
  methods : {

    async fetchData(){
      const _vm = this;

      _vm.loading(true);

      try{

        await _vm.loadCmtList();
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    async loadCmtList(){
      const _vm = this;

      _vm.loading(true);
      try {

          const cmtParams = _vm.getCmtSearchParam();
          const cmtRes = await getBoardCmt(cmtParams);

          var loginSeq =  _vm.eumLoginUser ? _vm.eumLoginUser.userSeq : ''; //로그인유저 Seq
          const updatedCmtData = cmtRes.resultData.list.map(item => ({
            ...item,
            isUpdate: loginSeq == item.rgtr, //수정가능 여부 (로그인유저==댓글작성자)
            isEditMode: false, //수정 버튼용
            newreply: {content: '', secretYn: 'N'}, //대댓글용
          }));

          _vm.cmtData = _vm.buildTree(updatedCmtData);
          
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },

    getCmtSearchParam() {
      const _vm = this;

      const retParam = {
        boardMstId : _vm.boardMstId,
        boardSeq : _vm.boardSeq,
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

    //등록
    async onClickSave(item) {

      const _vm = this;

      if(!_vm.eumLoginUser) { //로그인 정보가 없을 때 return
        await _vm.onClickConfirmLogin(); //로그인 확인
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

          //답글달기창 닫기          
          $(".comment-list .reply-box").removeClass("active"); 

          //newCmtData 초기화
          if(newCmt.replyYn == 'N') {
            _vm.newCmtData = {
              content: '',
              secretYn: 'N'
            }
          }

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
        await _vm.onClickConfirmLogin(); //로그인 확인
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

    /* 대댓글 버튼 동작
      1. 답글달기
      2. ... 드롭다운
      3. ... 쪽지 보내기
    */
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

    //3. 쪽지 보내기
    async onClickSendMessage(item){
      const _vm = this;

      if(!_vm.eumLoginUser) { //로그인 정보가 없을 때 return
        await _vm.onClickConfirmLogin();
        return;
      };

      EventBus.$emit('open-message-modal', {
        membSeq: item.rgtr,
        membName: item.writer,
      });
    
    },

    //4. 삭제
    async onClickDelete(item) {
      const _vm = this;

      const ret = await _vm.$refs["confirmModal"].open({
        title: '댓글 삭제',
        content : '댓글을 삭제하시겠습니까?',
        okButtonText : '삭제',
        cancelButtonText : '취소'        
      });

      if(!ret){
        return;
      }

      _vm.loading(true);
      try {

       const params = {
          boardCmtSeq : item.boardCmtSeq,
          delYn : 'Y',
        }; 

        await deleteBoardCmt(params);
        _vm.loading(false);

        await _vm.alertSuccess(`댓글이 삭제 되었습니다.`); 

        _vm.loadCmtList(); 
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
      
    },

    /*  댓글 내용 조회 가능 여부
      1. 비밀글이 아닐 때
      2. 비밀글이지만, 비밀글 조회 가능할 때
        1) 로그인 유저 == 글작성자
        2) 로그인 유저 == 댓글작성자
        3) 대댓글일때: 로그인 유저 == 상위댓글작성자
    */
    showCmtContent(cmt, upperCmt) {
      const _vm = this;
      var loginSeq =  _vm.eumLoginUser ? _vm.eumLoginUser.userSeq : ''; //로그인유저 Seq

      return (
        cmt.secretYn === 'N' ||
        (cmt.secretYn === 'Y' &&
          (loginSeq == cmt.rgtr || 
          loginSeq == _vm.boardWriterSeq || 
          (upperCmt && loginSeq == upperCmt.rgtr))
        )
      );
    },

  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
