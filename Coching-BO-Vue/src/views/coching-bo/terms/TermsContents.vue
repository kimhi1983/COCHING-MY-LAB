<template>
  <b-card border-variant="primary" class="p-2">
    <validation-observer ref="simpleRules">
      <!--내용 -->
      <b-form>
        <b-row>
          <b-col cols="12" md="4">
            <b-form-group
              label="수정일자"
              label-for="sys-terms-form-udtDate">
              <b-form-input id="sys-terms-form-udtDate"
                v-model="data.chngDttm" disabled>
              </b-form-input>
            </b-form-group>
          </b-col>
          
          <b-col cols="12" md="4">
            <b-form-group
              label="약관 게시일자"
              label-for="sys-terms-form-postingDate">
              <b-input-group id="sys-terms-form-postingDate">
                <validation-provider
                  #default="{ errors }"
                  name="'게시시작일'"
                  rules="required"
                  style="width: 50%;"
                >
                  <b-form-datepicker v-model="data.fromDate"
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                    placeholder="YYYY-MM-DD" locale="ko" 
                    today-button label-today-button="오늘"
                    reset-button label-reset-button="초기화"
                    close-button label-close-button="닫기"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
                <!-- <b-input-group-text class="date-range">~</b-input-group-text> -->
                <validation-provider
                  #default="{ errors }"
                  name="'게시종료일'"
                  rules="required"
                  style="width: 50%;"
                >
                  <b-form-datepicker v-model="data.toDate"
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                    placeholder="YYYY-MM-DD" locale="ko"
                    today-button label-today-button="오늘"
                    reset-button label-reset-button="초기화"
                    close-button label-close-button="닫기"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>  
              </b-input-group>
            </b-form-group>
          </b-col>
          <b-col cols="12" md="4">
            <b-form-group
              label="개정 횟수"
              label-for="sys-terms-revision-cnt">
              <b-input-group id="sys-terms-revision-cnt">
                <b-form-input v-model="data.rvsCnt"
                  class="text-right" disabled>
                </b-form-input>
                <b-input-group-append>
                  <b-button
                    variant="outline-primary"
                    :disabled="!data.rvsCnt"
                    @click="onClickRevisionList">
                    개정이력 보기
                  </b-button>
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>

          <b-col cols="12">
            <b-form-group
              label="약관 개정 메모"
              label-for="sys-terms-form-memo"
            >
              <b-form-textarea v-model="data.remarks">
                
              </b-form-textarea>
            </b-form-group>
          </b-col> 

          <!-- <b-col cols="12">
            <b-form-group
              label="Version."
              label-for="sys-terms-form-version"
            >
              <b-form-input 
                id="sys-terms-form-version"
                v-model="data.version"       
                placeholder="Version 을 입력하세요."
              />
            </b-form-group>
          </b-col>  -->
          <b-col cols="12">
            <b-form-group
              label="내용"
              label-for="sys-terms-form-content"
            >
              <validation-provider
                #default="{ errors }"
                name="'내용'"
                rules="required"
              >
                <b-form-textarea
                  id="sys-terms-form-content"
                  rows="5"
                  max-rows="15"
                  v-model="data.content"
                />
                <small class="text-danger">{{ errors[0] }}</small>
              </validation-provider>
            </b-form-group> 
          </b-col>
        </b-row>
      </b-form>
      <!-- //내용 -->
    </validation-observer>

    <!-- 버튼 영역 -->
    <div class="text-center mt-4">
      <b-button v-if="data.version"
        v-ripple.400="'rgba(113, 102, 240, 0.15)'"
        variant="primary"
        class="mr-1"
        @click="onClickSaveTerms('set')"
      >저장</b-button>
      <b-button
        v-ripple.400="'rgba(113, 102, 240, 0.15)'"
        variant="primary"
        class="mr-1"
        @click="onClickSaveTerms('new')"
      >{{data.version ? '개정' : '등록'}}</b-button>
      <b-button
        v-ripple.400="'rgba(113, 102, 240, 0.15)'"
        variant="outline-primary"
        @click="onClickCancel"
      >취소</b-button>
    </div>
    <!-- // 버튼 영역 -->

    <!-- 개정확인 모달 -->
    <b-modal v-model="termsRevision.show"
      title="개정확인" size="sm"
      centered no-close-on-backdrop>
      <b-row>
        <b-col cols="12">
          <b-form-group
            label="개정횟수"
            label-for="revision-cnt"
            label-cols-md="4"
          >
            <b-form-input
              id="revision-cnt" disabled
              v-model="termsRevision.data.revisionCnt"
            />
          </b-form-group>
        </b-col>
        <b-col cols="12">
          <b-form-group
            label="수정일자"
            label-for="revision-date"
            label-cols-md="4"
          >
            <b-form-input
              id="revision-date" disabled
              v-model="termsRevision.data.revisionDate"
            />
          </b-form-group>
        </b-col>
        <b-col cols="12" class="mt-2 mb-1">
          <b-jumbotron class="p-1" bg-variant="transparent" border-variant="second">
            현재 작성한 내용은 [개정이력 보기]에 추가합니다.
            개정하시겠습니까? 
          </b-jumbotron>
        </b-col>
      </b-row>
      <template #modal-footer="{ ok, cancel }">
        <b-button @click="termsRevision.show = false;"
          variant="outline-primary">취소</b-button>            
        <b-button @click="onClickNewTerms"
          variant="primary">확인</b-button>              
      </template>
    </b-modal>

    <!--개정이력 모달 -->
    <b-modal v-model="termsRevisionList.show"
      size="lg"
      centered no-close-on-backdrop>
      <b-row>
        <b-col cols="4">
          <b-form-group
            label="Version."
            label-for="revision-date">
            <v-select id="revision-date"
              v-model="termsRevisionList.data.version"
              :options="termsRevisionList.revisionVersionList" :reduce="op => op.value"
              :dir="$store.state.appConfig.isRTL ? 'rtl' : 'ltr'"
              class="w-100"
              @input="loadRevision"
            />
          </b-form-group>
        </b-col>
        <b-col cols="4">
          <b-form-group
            label="개정횟수"
            label-for="revision-cnt">
            <b-input-group append="회">
              <b-form-input class="text-right" disabled
                v-model="termsRevisionList.revisionCnt">
              </b-form-input>
            </b-input-group>
          </b-form-group>
        </b-col>
        <b-col cols="4">
          <b-form-group
            label="수정일자"
            label-for="revision-date">
            <b-form-input disabled
              v-model="termsRevisionList.data.chngDttm" />
          </b-form-group>
        </b-col>
        <b-col cols="12" md="6">
          <b-form-group
            label="약관 게시일자"
            label-for="sys-terms-form-postingDate">
            <b-input-group id="sys-terms-form-postingDate">
              <validation-provider
                #default="{ errors }"
                name="'게시시작일'"
                rules="required"
              >
                <b-form-datepicker v-model="termsRevisionList.data.fromDate" disabled
                  :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                  placeholder="YYYY-MM-DD" locale="ko" 
                  today-button label-today-button="오늘"
                  reset-button label-reset-button="초기화"
                  close-button label-close-button="닫기"
                />
                <small class="text-danger">{{ errors[0] }}</small>
              </validation-provider>
              <b-form-datepicker v-model="termsRevisionList.data.toDate" disabled
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
            label="약관 개정 메모"
            label-for="revision-memo">
            <b-textarea id="revision-memo" disabled
              v-model="termsRevisionList.data.remarks">

            </b-textarea>
          </b-form-group>
        </b-col>
        <b-col cols="12">
          <b-form-group
            label="내용"
            label-for="revision-content">
              <b-textarea id="revision-content" disabled
                v-model="termsRevisionList.data.content"
                rows="10" max-rows="15">
              </b-textarea>
          </b-form-group>
        </b-col>
      </b-row>
      <template #modal-footer="{ ok, cancel }">
        <b-button @click="termsRevisionList.show = false;"
          variant="outline-primary">닫기</b-button>            
      </template>
    </b-modal>
  </b-card>
</template>
<script>
import {
  newTerms, 
  setTerms, 
  getTerms,
  getList
} from '@/api/coching-bo/comm/terms';
import { required } from '@validations';

import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';
import ToastificationContent from '@core/components/toastification/ToastificationContent.vue'
import vSelect from 'vue-select';
import moment from 'moment';

const DEF_PARAMS = {
  termsCd: "",
  termsName: "",
  conetnt: "",
  udtId: 1, // 사용자 아이디로 바꿀 것
  useYn: "Y"
};

const fillZero = function(width, str){
  return str.length >= width ? str:new Array(width-str.length+1).join('0')+str;//남는 길이만큼 0으로 채움
};

export default {
  name: 'coching-System-Terms-Contents',
  mixins: [ernsUtils],
  components : {
    ToastificationContent, vSelect, required
  },
  directives: {
    Ripple
  },
  props: {
    termsData: {
      type: Object,
      default: ()=>{}
    },
  },
  watch: {
    termsData() {
      const _vm = this;
      _vm.setValue();

      //console.debug(`update child w : ${this.data.termsCd}`);
    }
  },
  data(){
    return {
      data: {...DEF_PARAMS},
      lastUdtDate: "",
      versionList: [],
      currentVersion: "",

      termsRevision: {
        data: {
          revisionCnt: "",
          revisionDate: ""
        },
        show: false
      },

      termsRevisionList: {
        revisionVersionList: [],
        rawList: [],
        revisionCnt: 0,
        data: {
          remarks: "",
          content: "", 
          version: "",
          chngDttm: "",
          fromDate: "",
          toDate: "",
        },
        show: false
      }
    }
  },
  mounted(){
    const _vm = this;    
    _vm.setValue();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    setValue(termsData){
      const _vm = this;   
      const setVal = termsData ? termsData : _vm.termsData;

      _vm.data = {
        ...setVal,
        rvsCnt: (setVal.cnt || 0) > 1  ? (setVal.cnt - 1) : 0 
      };  

      //console.debug(`update child : ${_vm.data.termsCd}`);
      
      _vm.currentVersion = _vm.data.version;
    },

    async loadDetail() {
      const _vm = this;

      const params = {
        ..._vm.data
      }
      const result = await getTerms(params);
      
      const versionList = result.resultData.versionlist;
      _vm.versionList = versionList.map(item => {
        return {
          label: item.version,
          value: item.version
        }
      });

      _vm.currentVersion = result.resultData.version;

    },

    //저장(등록)
    async onClickSaveTerms(setType) {
      const _vm = this;

      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      //최초생성인지 확인
      const isFirst = setType == 'new' && '' == (_vm.data.version || '');
      
      if(!isFirst && setType == 'new') {
        const today = moment().format("YYYY-MM-DD");
        const chngDate = moment(_vm.data.chngDttm).format("YYYY-MM-DD");
        _vm.termsRevision.data.revisionCnt = _vm.data.rvsCnt + " → " + (_vm.data.rvsCnt + 1);
        _vm.termsRevision.data.revisionDate = chngDate + " → " + today;
        _vm.termsRevision.show = true;
        return;
      }

      const result = await _vm.$swal({
        title: isFirst ? '등록' : '저장',
        text: `약관 정보를 ${isFirst ? '등록' : '저장'} 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: isFirst ? '등록' : '저장',
        customClass: {
          confirmButton: 'btn btn-primary ml-1',
          cancelButton: 'btn btn-outline-danger',
        },
        buttonsStyling: false,
      });

      try{
        if(result.value) {
          _vm.loading(true);
          const params = _vm.getDataParam();
          if(isFirst){
            params.version = "Ver.0001";
            params.newVersion = "Ver.0001";
            params.oldVersion = "Ver.0001";
          }
          const res = isFirst ? await newTerms(params) : await setTerms(params);

          _vm.loading(false);

          _vm.alertSuccess('저장되었습니다.');        
          _vm.update();   
        }
      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    //개정
    async onClickNewTerms() {
      const _vm = this;
      try{
          _vm.loading(true);
          const params = _vm.getDataParam();

          const versionNum = Number(_vm.data.cnt) + 1;
          const newVersionNum = fillZero(4, versionNum.toString());
          const newVersion = _vm.data.version.split('.')[0].concat('.').concat(newVersionNum);
          
          params.newVersion = newVersion;
          params.oldVersion = _vm.data.version;
          
          const res = await newTerms(params);

          _vm.loading(false);

          _vm.alertSuccess('저장되었습니다.');       
          _vm.termsRevision.show = false;
          _vm.update();   
      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
    },

    getDataParam() {
      const _vm = this;
      const params = {
        termsCd   : _vm.data.termsCd,
        termsType : _vm.data.termsType,
        version   : _vm.data.version,
        content   : _vm.data.content,
        requiredYn: _vm.data.requiredYn,
        fromDate  : _vm.data.fromDate,
        toDate    : _vm.data.toDate,
        remarks   : _vm.data.remarks
      }

      return params;
    },
    
    onClickCancel() {
      const _vm = this;
      _vm.setValue();

    },
    update() {
      const _vm = this;
      _vm.$emit("update", true);
    },

    onSearch() {
      const _vm = this;
      _vm.loadDetail();
    },

    async onClickRevisionList() {
      const _vm = this;

      const termsCds = [];
      termsCds.push(_vm.data.termsCd);
      _vm.loading(true);

      try{
        const params = {
          termsType: _vm.data.termsType,
          termsCds: termsCds,
          sortColumn: 'RGT_DTTM'
        }
        
        const res = await getList(params);
        _vm.termsRevisionList.rawList = [...res.resultData.list];
        const versionList = res.resultData.list.map(item => {
          return {
            label: item.version,
            value: item.version,
          }
        })
        
        _vm.termsRevisionList.revisionVersionList = [...versionList];
        _vm.termsRevisionList.data.version = _vm.termsRevisionList.revisionVersionList[0].value;

        _vm.termsRevisionList.revisionCnt = 
          res.resultData.list.length ? (res.resultData.list.length - 1) : 0;
        _vm.loadRevision(_vm.termsRevisionList.data.version)  
      } catch(err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }

      _vm.termsRevisionList.show = true;
    },

    loadRevision(selectVersion) {
      const _vm = this;
     
      for(const rvsData of _vm.termsRevisionList.rawList) {
        if(rvsData.version == selectVersion) {
          console.log(rvsData)
          _vm.termsRevisionList.data = {...rvsData};
        }
      }
    }
  }  
}
</script>

<style lang="scss" scoped>

</style>