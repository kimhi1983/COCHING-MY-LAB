<style lang="scss" scoped>
.comment-wrap .body {
  white-space: pre-wrap !important;
}
</style>
<template>
  <!--요청 자료 전달 내역-->
  <div class="order-wrap">
      <div class="total-wrap">
        <div class="total-num">요청 자료 전달 내역</div>
      </div>
      <!--list-->
      <div class="comment-wrap">
      <!--comment-list-->
      <div class="comment-list sample-list">
        <!--item-->
        <div
          v-for="(item, idx) of localData.list" class="item">
          <!--inner-->
          <div v-if="!item.refSeq || item.refSeq == item.rawReplySeq" class="inner">
            <div class="header">
              <div class="left">
                <div class="user">{{ getCmtName(item) }}</div>
                <div class="date">{{ item.rgtDttm | eFmtDateTime('.') }}</div>
              </div>
              <!--sample-type-->
              <div class="more-container sample-type">
                <div class="ic-wrap more-wrap" @click="onClickReplyDrop($event)">
                  <div class="ic-lg ic-more"></div>
                </div>
                <div class="dropdown for-more">
                  <!-- <a href="javascript:;" class="btn-edit">수정</a> -->
                  <a @click="onClickUpdateDelYn(item)" href="javascript:;" class="btn-delete">삭제</a>
                </div>
              </div>
            </div>
            <div v-if="item.contents" class="body">{{ item.contents }}</div>
            <!--sample-date-->
            <div v-if="(item.dispatchCode && item.dispatchCode != '004') || item.dispatchDate" 
              class="sample-date">샘플발송 예정일 : {{ getDispathDate(item) }}</div>
            <!--file-wrap-->
            <div v-if="item.filelist.length > 0" class="file-wrap">
              <div class="total-wrap">
                <div class="total-num">첨부파일<span>{{ item.filelist.length }}</span></div>
                <a @click="onClickAllFileDownload(item.filelist)" href="javascript:;" class="btn-save">모두저장</a>
              </div>
              <div class="file-list">
                <a @click="onClickFileDownload(file)" v-for="(file, idx) of item.filelist" href="javascript:;">
                  <div class="title-wrap">
                    <span class="title">{{ file.fileName }}</span>
                    <span class="date">{{ eufmtFileSize(file.fileSize) }}</span>
                  </div>
                  <span class="ic-md ic-download"></span>
                </a>
              </div>
            </div>
            <!-- <div class="btn-comment-wrap">
              <a href="javascript:;" class="btn-comment">답글달기</a>
            </div> -->

            <!-- 수정 -->
            <form v-if="item.isEditMode">
              <div class="comment-edit-wrap">
                <div class="comment-box">
                  <textarea rows="5" placeholder="댓글을 입력해 주세요"></textarea>
                  <div class="bottom">
                    <div class="checkbox">
                      <input id="secret" type="checkbox" />
                      <label for="secret" class="checkbox-label">비밀글</label>
                    </div>
                    <div class="right">
                      <button type="button" class="btn btn-sm btn-gray-outline btn-cancel">취소</button>
                      <button type="button" class="btn btn-sm btn-primary">수정</button>
                    </div>
                  </div>
                </div>
              </div>
            </form>

            <!--reply-box-->
            <div class="reply reply-box">
              <form class="sample-editor">
                <div class="comment-box">
                  <textarea rows="3" placeholder="댓글을 입력해 주세요"></textarea>
                  <!--upload-->
                  <div class="upload-wrap">
                    <div class="upload-area">
                      <input type="file" class="file-input" multiple />
                      <div class="file-list"></div>
                    </div>
                  </div>
                  <div class="bottom">
                    <div class="datepicker-wrap">
                      <span v-show="eumLoginUser.ptnSeq == requestData.managerPtnSeq" class="to">샘플발송 <span class="m-none">예정일</span></span> 
                      <input v-show="eumLoginUser.ptnSeq == requestData.managerPtnSeq" type="text" name="dateBox" class="datepicker" />
                    </div>
                    <button type="button" class="btn btn-sm btn-disabled">등록</button>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <!--reply-list-->
          <div v-if="item.refSeq && item.refSeq != item.rawReplySeq" class="reply-list">
            <div class="item">
              <!--inner-->
              <div class="inner">
                <div class="header">
                  <div class="left">
                    <div class="user">{{ getCmtName(item) }}</div>
                    <div class="date">{{ item.rgtDttm | eFmtDateTime('.') }}</div>
                  </div>
                  <!--sample-type-->
                  <div class="more-container sample-type">
                    <div class="ic-wrap more-wrap">
                      <div class="ic-lg ic-more"></div>
                    </div>
                    <div class="dropdown for-more">
                      <a href="javascript:;" class="btn-edit">수정</a>
                      <a @click="onClickUpdateDelYn(item)" href="javascript:;" class="btn-delete">삭제</a>
                    </div>
                  </div>
                </div>
                <div v-if="item.contents" class="body">{{ item.contents }}</div>
                <!--file-wrap-->
                <div v-if="item.filelist.length > 0" class="file-wrap">
                  <div class="total-wrap">
                    <div class="total-num">첨부파일<span>{{ item.filelist.length }}</span></div>
                    <a @click="onClickAllFileDownload(item.filelist)" href="javascript:;" class="btn-save">모두저장</a>
                  </div>
                  <div class="file-list">
                    <a @click="onClickFileDownload(file)" v-for="(file, idx) of item.filelist" href="javascript:;">
                      <div class="title-wrap">
                        <span class="title">{{ file.fileName }}</span>
                        <span class="date">{{ eufmtFileSize(file.fileSize) }}</span>
                      </div>
                      <span class="ic-md ic-download"></span>
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!--comment-editor-->
      <div class="comment-editor">
        <form class="sample-editor">
          <div class="comment-box">
            <textarea rows="3" placeholder="댓글을 입력해 주세요" v-model="localData.replyData.contents"></textarea>
            <!--upload-->
            <div @click="handleInputFile" 
                @dragover.prevent="handleDragOver"
                @dragenter.prevent="handleDragEnter"
                @dragleave="handleDragLeave"
                @drop.prevent="handleDrop" class="upload-wrap">
              <div class="upload-area">
                <input @change="handleFileChange" ref="fileInput" type="file" class="file-input" multiple />
                <div class="file-list">
                  <div v-for="(item, index) of localData.replyData.filelist" :key="index"
                    class="file-item">
                    <div class="file-wrap">
                      <div class="file-name">
                        <span>{{ item.fileName.split('.')[0] }}</span>
                        <span class="file-extension">{{ '.' + item.fileName.split('.').pop() }}</span>
                      </div>
                      <div class="file-size">({{ eufmtFileSize(item.fileSize) }})</div>
                    </div>
                    <button @click.stop="deleteFile(index)" type="button" class="delete-button"></button>
                  </div>
                </div>
                <p v-if="localData.replyData.filelist.length == 0" class="placeholder-text">파일을 마우스로 끌어 오거나 클릭하세요</p>
              </div>
            </div>
            <div class="bottom">
              <div class="datepicker-wrap">
                <span v-show="eumLoginUser.ptnSeq == requestData.managerPtnSeq" class="to">샘플발송 <span class="m-none">예정일</span></span> 
                <!-- <input type="text" name="dateBox" class="datepicker" /> -->
                  <SimpleDatePicker
                  v-show="eumLoginUser.ptnSeq == requestData.managerPtnSeq" 
                  v-model="localData.replyData.dispatchDate"										
                  ref="refDispatchDateStr"
                  :placeholder="$tt('test') || '발송예정일'"										
                  :options="localData.replyData.datePickerOption"
                  :isEmpty="true"
                  @change="onChangeDatePicker"
                  ></SimpleDatePicker>
              </div>
              <button @click="onSaveReply" type="button" class="btn btn-sm btn-primary">등록</button>
            </div>
          </div>
        </form>
        <!--오류-->
        <AlertModal ref="alertModal"></AlertModal>
        <ConfirmModal ref="confirmModal"></ConfirmModal>
      </div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import moment from 'moment';
import SimpleDatePicker from '@/components/SimpleDatePicker.vue';

import { getCodes } from '@/api/coching/comm/code';
import { getReply, updateReplyDelYn, setReply } from '@/api/coching/raw/request';

const DEF_REPLY_INFO ={
  rawReplySeq: null,
  rawRequestSeq: null,
  dispatchCode: '',
  dispatchDate: '',
  datePickerOption : {
    dateFormat: "yy-mm-dd",
    minDate : moment().add(0, 'days').toDate()
  },
  contents: '',
  filelist: [],
  delfilelist: [],
  rawName: '',
  ptnName: '',
  membSeq: null,
};

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
};


const getInitialReplyData = () => ({
  ...DEF_REPLY_INFO,
  filelist: [],
  delfilelist: [],
});

export default {
  name: 'ReplyComment',
  mixins: [ernsUtils],
  components: {
    SimpleDatePicker,
  },
  computed: {
  },
  watch: {
    '$route': 'fetchData' //라우트가 변경되면 메소드를 다시 호출됩니다.
    /*locale 변경감지*/
    ,'$i18n.locale' : function(){this.isLoadTerms=false;} //다시로드하도록 처리

    ,requestData: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.fetchData(); // rawRequestSeq가 변경되면 fetchData를 호출
      }
    }
  },
  props:{
    requestData: {
      type: Object,
      default: () => ({})
    },
  },
  async mounted(){
    const _vm = this;

    _vm.fetchData();    
    _vm.loadCodes();
  }, 
  data(){
    return {
      CODES:{
        CH011:[],
      },
      localData: {
        isDragging: false,
        list: [],
        replyData:{...DEF_REPLY_INFO},
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

        await _vm.loadReply();
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    async loadCodes(){
      const _vm = this;

      const codeRes011 = await getCodes({grpCode:"CH011", etc5:_vm.$i18n.locale});
      _vm.CODES.CH011 = [...codeRes011.resultData.list];
    },

    handleInputFile(){
      this.$refs.fileInput.click();
    },
    //File click
    handleFileChange(event) {
      this.handleFileUpload(event.target.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },
     //file drag
     handleDrop(event) {
      event.preventDefault();
      this.handleFileUpload(event.dataTransfer.files);
      event.target.value = ''; // 파일 입력 필드 초기화
    },
    handleDragOver(event) {
      this.isDragging = true;
      event.dataTransfer.dropEffect = 'copy'; // 드롭 효과를 'copy'로 설정
    },
    handleDragEnter(event) {
      this.isDragging = true;
    },
    handleDragLeave() {
      this.isDragging = false;
    },
    async handleFileUpload(files) {
      const _vm = this;

      if (files.length > 0) {
        Array.from(files).forEach(item => {
          let newFile = { ...DEF_FILE };
          newFile.file = item;
          newFile.fileName = item.name; 
          newFile.fileSize = item.size; 

          _vm.localData.replyData.filelist.push({ ...newFile });
        });
      }

    },
    async deleteFile(idx){
      const _vm = this;
      const file = _vm.localData.replyData.filelist[idx];

      if(file.fileId){
        if (!_vm.localData.replyData.delfilelist.includes(file.fileId)) {
          _vm.localData.replyData.delfilelist.push(file.fileId);
        }
      }
      _vm.localData.replyData.filelist.splice(idx, 1);
    },

    //달력 선택
		onChangeDatePicker(newVal, oldVal){
			const _vm = this;

			_vm.localData.replyData.dispatchDate = newVal;
		},

    async loadReply(){
      const _vm = this;
			_vm.loading(true);
      try {
          const res = await getReply({rawRequestSeq: _vm.requestData.rawRequestSeq });
          const resData = res.resultData;
          
          //데이터 바인딩
          _vm.localData.list = [...resData.list];
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    getDispathDate(item){
      const _vm = this;

      if(!item.dispatchCode && !item.dispatchDate) return '';

      if(item.dispatchCode == '004' || !item.dispatchCode){
        return _vm.eFmtDate(item.dispatchDate,'.');
      }else{
        const code = this.CODES.CH011.find(code => code.etc1 === item.dispatchCode);
        return code.codeName;
      }
    },

     async onSaveReply(){
      const _vm = this;

      if(_vm.localData.replyData.contents == ''
        && _vm.localData.replyData.filelist.length == 0
        && _vm.localData.replyData.dispatchDate == ''
      ){
        const ret = await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '요청자료 전달',
          titleHtml : _vm.$t('') || '내용을 입력해주세요.'
        });
        return;
      }
      _vm.loading(true);

      try{
        const param = _vm.setParamData();        

        let res = await setReply(param);
        const { resultCode, resultFailMessage, resultData } = res;
        _vm.localData.replyData = getInitialReplyData(); //초기화

        _vm.loading(false);

        const ret = await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '요청자료 전달',
          titleHtml : _vm.$t('') || '전달되었습니다.'
        });

        if(ret) _vm.loadReply();

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
      
    },

    setParamData(){
      const _vm = this;
    
      const dataMap = _vm.localData.replyData;
      const requestMap = _vm.requestData;
      const formData = new FormData();
      // 키-값 쌍을 FormData에 추가
      formData.append('rawReplySeq', requestMap.rawReplySeq || 0);
      formData.append('rawRequestSeq', requestMap.rawRequestSeq);
      formData.append('refSeq', requestMap.rawReplySeq || 0);
      if(dataMap.dispatchCode != ''){
        formData.append('dispatchCode', dataMap.dispatchCode);
      }
      if(dataMap.dispatchDate != ''){
        formData.append('dispatchDate', dataMap.dispatchDate);
      }
      formData.append('contents', dataMap.contents);
      formData.append('strDelFileIds', dataMap.delfilelist.join(","))
      formData.append('rawName', requestMap.rawName);
      formData.append('ptnName', requestMap.ptnName);
      formData.append('membSeq', _vm.eumLoginUser.userSeq);
      formData.append('managerSeq', requestMap.membSeq);

      // 파일 리스트 추가
      dataMap.filelist.forEach((fileItem, index) => {
        if (fileItem.file) {
            formData.append(`request_files_${index}`, fileItem.file, fileItem.fileName);
        }
      });

      return formData;
    },

    onClickReplyDrop(event) {
      const target = $(event.target).closest(".more-container").find(".for-more");
      const hasOn = target.hasClass("on");
      if(hasOn) {
        target.removeClass('on');
      } else {
        $(".more-container .for-more").removeClass("on"); //기존 항목 드롭다운 제거
        target.addClass('on');
      }
    },

    //요청 자료 전송 취소
    async onClickUpdateDelYn(item){
      const _vm = this;

      _vm.loading(true);
      try {
          const res = await updateReplyDelYn({rawReplySeq: item.rawReplySeq, delYn: 'Y' });
          
          _vm.loading(false);
          const ret = await _vm.$refs["alertModal"].open({
            title: _vm.$t('mypage.alert.modify001') || '자료 전송 취소',
            titleHtml : _vm.$tt('test001') || '전송취소되었습니다.'
          });

          _vm.loadReply();
          
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }

    },

    /* 첨부파일 다운로드 */
    // 저장
    onClickFileDownload(file) {
      const _vm = this;

      const downloadPath = _vm.eumFileDownPath(file.fileId);
      document.location.href = downloadPath;
    },

    // 모두저장
    onClickAllFileDownload(filelist) {
      // Promise를 사용하여 순차적으로 처리
      filelist.reduce((promise, file) => {
        return promise.then(() => this.downloadFile(file));
      }, Promise.resolve()); // 초기 Promise 설정
    },

    downloadFile(file) {
      return new Promise((resolve) => {
        const downloadPath = this.eumFileDownPath(file.fileId);

        const link = document.createElement('a');
        link.href = downloadPath;
        link.download = file.fileName || '';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        // 다운로드 실행 후 약간의 대기 시간 (필수는 아님)
        setTimeout(resolve, 100); // 브라우저의 다운로드 처리가 끝난 후 resolve 호출
      });
    },

    getCmtName(item) {
      const _vm = this;
      if(item.ptnSeq == _vm.requestData.managerPtnSeq){
        return item.membName + '(' + item.ptnName + ')';
      } else{
        return item.membName;
      }
    },
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
