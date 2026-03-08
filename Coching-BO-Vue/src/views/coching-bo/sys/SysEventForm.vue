<template>
  <b-card no-body>
    <validation-observer ref="simpleRules">
      <b-card-group>
        <b-card  class="col-md-5 mt-1">
          <b-row>
            <b-col cols="12">
              <b-form-group
                label="이벤트 이미지 등록"
                label-for="pt-event-image"
              >
                <Vue2Dropzone ref="dzVue"
                  id="dropzone"
                  :options="dropzoneOptions"
                  :include-styling="true"
                  @vdropzone-removed-file="dzOnClickRemoveFile"
                  @vdropzone-file-added="eumDzHandleFileAdded($event)"
                  ></Vue2Dropzone>
                </b-form-group>
            </b-col>
          </b-row>
        </b-card>
        <b-card  class="col-md-7  mt-1">
          <b-row>
            <b-col cols="12">
              <b-form-group
                label="이벤트명"
                label-for="sys-event-title"
              >
                <validation-provider
                  #default="{ errors }"
                  name="'이벤트명'"
                  rules="required"
                >
                  <b-form-input
                    id="sys-event-title"
                    placeholder="이벤트명을 입력하세요."
                    v-model="detail.title"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>
            <b-col cols="12">
              <b-form-group
                label="기간 설정"
                label-for="sys-event-exposure-date">
                <b-input-group id="sys-event-exposure-date" >
                  <b-form-datepicker
                    v-model.trim="detail.fromDate"
                    placeholder="YYYY-MM-DD" locale="ko"
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                    today-button label-today-button="오늘"
                    reset-button label-reset-button="초기화"
                    close-button label-close-button="닫기"
                  />
                  <b-form-datepicker
                    v-model.trim="detail.toDate"
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                    placeholder="YYYY-MM-DD" locale="ko"
                    today-button label-today-button="오늘"
                    reset-button label-reset-button="초기화"
                    close-button label-close-button="닫기"
                  />
                </b-input-group>
              </b-form-group>
            </b-col>

            <b-col cols="12">
              <b-form-group
                label="이벤트 내용"
                label-for="sys-event-content"
              >
                <validation-provider
                  #default="{ errors }"
                  name="'이벤트 내용'"
                  rules="required"
                >
                  <b-form-textarea
                    id="sys-event-content"
                    v-model="detail.content"
                    rows="10"
                    max-rows="10"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col cols="12" md="4">
              <b-form-group
                label="진행 여부"
                label-for="sys-event-evntStatus"
              >
                <b-form-radio-group
                  id="sys-event-evntStatus"
                  v-model="detail.evntStatus"
                  :options="[{text:'진행', value:'EVNT_ST_0100'},{text:'종료', value:'EVNT_ST_0200'}]"
                  button-variant="outline-primary"
                  buttons name="radio-btn-outline"
                />
              </b-form-group>
            </b-col>
            <b-col cols="12" md="4">
              <b-form-group
                label="노출 여부"
                label-for="sys-event-dispYn"
              >
                <b-form-radio-group
                  id="sys-event-dispYn"
                  v-model="detail.dispYn"
                  :options="[{text:'노출', value:'Y'},{text:'숨김', value:'N'}]"
                  button-variant="outline-primary"
                  buttons name="radio-btn-outline"
                />
              </b-form-group>
            </b-col>
            <b-col cols="12" md="4">
              <b-form-group
                label="사용 여부"
                label-for="sys-event-useYn"
              >
                <b-form-radio-group
                  id="sys-event-useYn"
                  v-model="detail.useYn"
                  :options="[{text:'사용', value:'Y'},{text:'미사용', value:'N'}]"
                  button-variant="outline-primary"
                  buttons name="radio-btn-outline"
                />
              </b-form-group>
            </b-col>
          </b-row>
        </b-card>
      </b-card-group>

      <b-card class="mt-2">
        <b-row>
          <b-col cols="12" md="6" v-if="!isRegMode">
            <b-form-group label="작성자"
              label-for="bi-form-writerName">
              <b-form-input
                readonly
                id="bi-form-writerName"
                type="text"
                v-model="detail.writer"
                :formatter="eufmtMaxLength50"
              />
            </b-form-group>
          </b-col>
          <b-col md="6" v-if="!isRegMode" class="mt-1">
            <label>등록일 : {{detail.rgtDttm | eFmtDateTime}}</label><br/>
            <label>최종수정일 : {{detail.chngDttm | eFmtDateTime}}</label>
          </b-col>
        </b-row>
      </b-card>
      <b-col class="mb-2">
        <!-- 버튼 영역 -->
        <div class="text-center mt-4">
          <b-button v-if="!isRegMode"
            v-ripple.400="'rgba(113, 102, 240, 0.15)'"
            variant="outline-danger"
            class="mr-1"
            @click.prevent="onClickDelete"
          > 삭제
          </b-button>
          <b-button
            v-ripple.400="'rgba(113, 102, 240, 0.15)'"
            variant="primary"
            class="mr-1"
            @click.prevent="onClickSave"
          > {{isRegMode ? '등록': '저장'}}
          </b-button>
          <b-button
            v-ripple.400="'rgba(113, 102, 240, 0.15)'"
            variant="outline-primary"
            @click="onClickCancel"
          > 취소
          </b-button>
        </div>
        <!-- // 버튼 영역 -->
      </b-col>
    </validation-observer>
  </b-card>
</template>

<script>
import {
  getDetail,
  newEvent,
  setEvent,
  delEvent
} from '@/api/coching-bo/system/event';

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'

import Vue2Dropzone from 'vue2-dropzone'
import 'vue2-dropzone/dist/vue2Dropzone.min.css'
import { required } from "@validations";

const DEFAULT_EVENT = {
  evntSeq: 0,
  title: '',
  fromDate: '',
  toDate: '',
  content: '',
  evntStatus: 'EVT_STATUS_1000',
  dispYn: 'Y',
  useYn: 'Y',
  delYn: 'N',
  userName: '',
  files: []
}

export default {
  name: 'coching-BackOffice-Sys-eventForm',
  mixins: [ernsUtils],
  components : {
    Vue2Dropzone
  },
  props: {

  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    },
    isRegMode(){
      const params = this.params;

      if(!params.hasOwnProperty('evntSeq')){
        return true;
      }

      const pEvntSeq = params.evntSeq;
      return !(pEvntSeq && pEvntSeq > 0);
    }
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
  data(){
    return {
      detail: {...DEFAULT_EVENT},
      selected: false,
      disabled: true,
      params: {},
      delFiles: [],

      dropzoneOptions: {
        url: 'https://httpbin.org/post',
        thumbnailWidth: 100,
        thumbnailHeight: 100,
        autoQueue : false,
        duplicateCheck : true,
        addRemoveLinks: true,
        maxFilesize: 100,
        maxFiles : 1,
        dictRemoveFileConfirmation: "삭제 하시겠습니까?",
        dictDefaultMessage: "첨부파일을 선택 하거나 여기에 드래그 해 주십시오...",
      },
    }
  },
  mounted(){
    const _vm = this;
    _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    getInitParam(){
      const _vm = this;
      const query = _vm.$route.query, params = _vm.$route.params;

      _vm.params = {
        ...query
      };
    },

    fetchData(){
      const _vm = this;
      _vm.getInitParam();
      _vm.loadDetail();
    },

    //데이터 로드
    async loadDetail(){
      const _vm = this;

      //파일 초기화
      _vm.delFiles = [];

      if(_vm.isRegMode){
        //등록모드
        _vm.detail = {...DEFAULT_EVENT};
        return;
      }

      const params = {
        evntSeq : _vm.params.evntSeq,
      };

      const res = await getDetail(params);

      _vm.detail = {
        ...res.resultData
      }
      _vm.detail.files = [...res.resultData.files];
      _vm.eumDzAddMockupFilesInDropzone(_vm.$refs.dzVue, _vm.detail.files);

      //잘못된 접근
      // if(!_vm.detail.evntSeq){
      //   await _vm.alertError(`내용을 찾을 수 없습니다.`);
      //   _vm.$router.replace({ name: 'coching-bo-sys-event-main'});
      // }
    },

    async onClickSave() {
      const _vm = this;

      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      try {
        _vm.loading(true);
        const params = {
          ..._vm.detail
        };

        let res = null;

        const formData = new FormData();

        for( const key in params) {
          formData.append(key, params[key]);
        }
        //뭐하는건지 이해가안됨
        const dzFiles = _vm.$refs.dzVue.getAcceptedFiles();
          for( const [idx, file] of dzFiles.entries() ){
            formData.append("event_files_"+idx, file);
          }

        { //삭제할 파일
          formData.append("delFileIds", _vm.delFiles.join());
        }

        if(_vm.isRegMode) {
          _vm.loading(false);
          res = await newEvent(formData);

          await _vm.alertSuccess(`이벤트가 등록 되었습니다.`);
          _vm.$router.replace({ name: 'coching-bo-sys-event-edit', query: {evntSeq: res.resultData.evntSeq}});
        } else {
          res = await setEvent(formData);
          _vm.detail = {
            ...res.resultData
          };

          _vm.alertSuccess(`이벤트를 수정 했습니다.`);

        }

      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    async onClickDelete() {
      const _vm = this;

      const result = await _vm.$swal({
        title: '확인',
        text: "이벤트를 삭제 하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: '삭제',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      if(!result.value) {
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          evntSeq: _vm.detail.evntSeq,
          delYn : "Y" //삭제 처리
        };

        let res = null;
        if(_vm.isRegMode){
          return;
        }else{
          //수정모드
          res = await delEvent(params);
          _vm.detail = {
            ...res.resultData
          };

          _vm.loading(false);
          await _vm.alertSuccess(`이벤트를 삭제 했습니다.`);
          _vm.onClickCancel();
        }

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    //파일 삭제
    dzOnClickRemoveFile(file, error, xhr){
      const _vm = this;

      //console.debug(file);
      //console.debug(error);
      //console.debug(xhr);

      if(file && file.fileId){
        _vm.detail.files = _vm.detail.files.filter(item=>{
          return item.fileId != file.fileId;
        });
        _vm.delFiles.push(file.fileId);
      }
    },
  }
}
</script>

<style lang="scss" scoped>
.fileInput{
  background-color: #edecfc;
  border: 0;
  border: 1px dotted #7367f0;
  height: inherit;
  line-height: 400px;
  text-align: center;
  font-size: 50px;
  color: #7367f0;
  padding: 0;
}

.fileInput:hover{
  background-color: #e2e0ff;
  cursor: pointer;
}

.imgs:hover {
  cursor: pointer;
}
</style>