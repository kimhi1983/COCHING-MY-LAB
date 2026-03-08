<template>
  <div class="modal-full modal-md active" ref="request_reply_modal" style="display: none;">
      <div class="inner">
        <!--header-->
        <div class="modal-header">
          <!--left-->
          <div class="title">요청자료 전달</div>
          <!--button-->
          <div class="full-close ic-wrap">
            <div @click="onClickClose" class="ic-lg ic-close"></div>
          </div>
        </div>

        <!--body-->
        <div class="modal-body scroller">
          <!--input-->
          <validation-observer class="request-form" ref="requestReplyForm" #default="{ invalid }">
            <div class="inner">
              <div class="label-set">
                <label>첨부파일</label>
                <!--upload-->
                <div @click="handleInputFile" 
                    @dragover.prevent="handleDragOver"
                    @dragenter.prevent="handleDragEnter"
                    @dragleave="handleDragLeave"
                    @drop.prevent="handleDrop" class="upload-wrap">
                  <div class="upload-area" id="uploadArea">
                    <input @change="handleFileChange" type="file" ref="fileInput" id="fileInput" multiple style="display: none" />
                    <div id="fileList">
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
                      <p v-show="localData.replyData.filelist.length === 0">{{ $t('') || '파일을 마우스로 끌어 오거나 클릭하세요' }}</p>
                    </div>
                  </div>
                </div>
              </div>
              <div class="label-set">
                <label>샘플 발송 예정일</label>
                <validation-provider tag="div" #default="{ errors }" rules="required" class="radio-wrap">
                  <div v-for="(item, idx) in CODES.CH011" :key="idx">
                    <div v-if="item.etc1 != '004'">
                      <div class="radiobox">
                        <input v-model="localData.replyData.dispatchCode" 
                          @change="onChangeDispatch"
                          :id="`radio-${item.etc1}`" :value="item.etc1" type="radio" name="request-date" />
                        <label :for="`radio-${item.etc1}`" class="radiobox-label">{{ item.codeName }}</label>
                      </div>
                    </div>
                    <div v-else class="flex">
                      <div class="radiobox">
                        <input v-model="localData.replyData.dispatchCode" 
                          @change="onChangeDispatch"
                          :id="`radio-${item.etc1}`" :value="item.etc1" type="radio" name="request-date" />
                        <label :for="`radio-${item.etc1}`" class="radiobox-label">{{ item.codeName }}</label>
                      </div>
                      <SimpleDatePicker
                        v-model="localData.replyData.dispatchDate"										
                        ref="refDispatchDateStr"
                        :placeholder="$tt('test') || '발송예정일'"										
                        :options="localData.replyData.datePickerOption"
                        :disabled="!localData.isDatePicker"
                        @change="onChangeDatePicker"
                      ></SimpleDatePicker>
                      <div class="datepicker-wrap">
                        <span class="to">까지</span>
                      </div>
                    </div>
                  </div>
                </validation-provider>
              </div>
              <div class="label-set">
                <label>메세지 작성</label>
                <div class="textarea-wrap">
                  <textarea placeholder="내용을 입력해 주세요" v-model="localData.replyData.contents"></textarea>
                </div>
              </div>
            </div>
            <!--button-->
            <div class="btn-area">
              <button @click="onClickClose" 
                type="button" class="btn btn-lg btn-gray bottom-modal-full-close">취소</button>
              <button @click="onSubmit" type="button" :disabled="invalid" class="btn btn-lg btn-primary">전송하기</button>
            </div>
          </validation-observer>
        </div>
        <AlertModal ref="alertModal"></AlertModal>
      </div>
    </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import moment from 'moment';

import { getCodes } from '@/api/coching/comm/code';
import { required } from '@validations';
import { setReply } from '@/api/coching/raw/request';

import SimpleDatePicker from '../SimpleDatePicker.vue';

const DEF_FILE ={
  fileId: null,
  fileName: '',
  file: null,
};

const DEF_REPLY_INFO ={
  rawReplySeq: null,
  rawRequestSeq: null,
  dispatchCode: '001',
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

export default {
    name: 'coching-modal-full',
    mixins: [ernsUtils],
    components: {
      SimpleDatePicker,
          },
    props: {
    },
    computed: {
        
    },
    watch: {
      '$i18n.locale' : async function(){
        const _vm = this;
        await _vm.loadCodes();
        _vm.$nextTick(() => _vm.$refs.cntrcDetailForm.validate());
      },
      "localData.isShow" : "onChangeShow"
    },
    data() {
        return {
          CODES:{
            CH011:[],
          },
          localData : {
            isShow : false,
            result : undefined,
            isDatePicker: false,

            isRequestValid: false,
            replyData:{...DEF_REPLY_INFO}
          },
          isPartner: false,
        }
    },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    _vm.docReady();
    _vm.fetchData();    

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    async open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = true;

      ld.replyData = {...DEF_REPLY_INFO};
      ld.replyData.filelist = [];
      ld.replyData.delfilelist = [];
      ld.replyData.rawRequestSeq = options.rawRequestSeq;
      ld.replyData.rawName = options.rawName;
      ld.replyData.ptnName = options.ptnName;
      ld.replyData.membSeq = options.membSeq;

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },
   //데이터 가져오는 부분(사용하지 필요없어도 반드시 선언)
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

    async loadCodes(){
      const _vm = this;
     
      const codeRes = await getCodes({grpCode:"CH011", etc5:_vm.$i18n.locale});
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH011 = [...resultData.list];
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

    //발송 예정일 변경
    onChangeDispatch(){
      const _vm = this;

      if(_vm.localData.replyData.dispatchCode === '004'){
        _vm.localData.isDatePicker = true;
      }else{
        _vm.localData.isDatePicker = false;
      }

      const initStrDate = moment().format('YYYY-MM-DD');
      _vm.localData.replyData.dispatchDate = initStrDate;
    },

    //달력 선택
		onChangeDatePicker(newVal, oldVal){
			const _vm = this;

			//console.log(`newVal:${newVal}, oldVal:${oldVal}`);
			_vm.localData.replyData.dispatchDate = newVal;
		},

    async onSubmit(){
      const _vm = this;

      const isValid = await _vm.$refs.requestReplyForm.validate();
      if(!isValid){
        return;
      }
      _vm.loading(true);

      try{
        const param = _vm.setParamData();        

        let res = await setReply(param);
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);

        const ret = await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '요청자료 전달',
          titleHtml : _vm.$t('') || '전달되었습니다.'
        });

        if(ret){
          const ld = _vm.localData;
          ld.isShow = false;
          ld.result(false);
          _vm.$emit("load");
        }

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
      
    },

    setParamData(){
      const _vm = this;
    
      const dataMap = _vm.localData.replyData;
      const formData = new FormData();
      // 키-값 쌍을 FormData에 추가
      formData.append('rawReplySeq', dataMap.rawReplySeq || 0);
      formData.append('rawRequestSeq', dataMap.rawRequestSeq);
      formData.append('dispatchCode', dataMap.dispatchCode);
      if(dataMap.dispatchCode === "004"){
        formData.append('dispatchDate', dataMap.dispatchDate);
      }
      formData.append('contents', dataMap.contents);
      formData.append('strDelFileIds', dataMap.delfilelist.join(","))
      formData.append('rawName', dataMap.rawName);
      formData.append('ptnName', dataMap.ptnName);
      formData.append('membSeq', dataMap.membSeq);

      // 파일 리스트 추가
      dataMap.filelist.forEach((fileItem, index) => {
        if (fileItem.file) {
            formData.append(`request_files_${index}`, fileItem.file, fileItem.fileName);
        }
      });

      return formData;
    },

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isShow = false;
      ld.result(false);
    },

    onChangeShow(){
      const _vm = this;

      const newVal =_vm.localData.isShow;
      console.log(newVal);

      if(newVal == true){
        $(_vm.$refs["request_reply_modal"]).fadeIn(300);
        return;
      }

      $(_vm.$refs["request_reply_modal"]).fadeOut(300);
      $("body").css("overflow", "visible");
    },

    docReady(){
      const _vm = this;   

    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>