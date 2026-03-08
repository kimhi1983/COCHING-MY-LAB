<template>
  
  <b-card id="banner-form">
    <validation-observer ref="simpleRules">
      <b-card-group>
        <b-card no-body class="col-md-7" title="배너정보">
          <b-row>
            <b-col cols="12" class="mt-2">
              <b-form-group
                label="제목"
                label-for="banner-title"
              >
                <validation-provider #default="{ errors }"
                  name="'제목'"
                  rules="required"
                >
                  <b-form-input
                    id="banner-title"
                    placeholder="제목을 입력하세요."
                    v-model="detail.title"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider> 
              </b-form-group>
            </b-col>      

            <b-col cols="6">
              <b-form-group
                label="배너위치"
                label-for="banner-location"
              >
                <b-input-group :prepend="detail.bannerType">
                  <b-form-input
                    id="banner-location" disabled
                    :value="bannerMaster.bannerMstName"
                  />
                </b-input-group>
              </b-form-group>
            </b-col>

            <b-col cols="3">
              <b-form-group 
                label="사용 여부"
                label-for="banner-useYn"
              >
                <b-form-radio-group
                  id="banner-useYn"
                  v-model="detail.useYn"
                  :options="[{text:'사용', value:'Y'},{text:'미사용', value:'N'}]"
                  button-variant="outline-primary"                
                  buttons name="radio-btn-outline"
                />
              </b-form-group>
            </b-col>
            <b-col cols="3">
              <b-form-group 
                label="노출 여부"
                label-for="banner-dispYn"
              >
                <b-form-radio-group
                  id="banner-dispYn"
                  v-model="detail.dispYn"
                  :options="[{text:'표시', value:'Y'},{text:'숨김', value:'N'}]"
                  button-variant="outline-primary"                
                  buttons name="radio-btn-outline"
                />
              </b-form-group>
            </b-col>

            <b-col cols="12">
              <b-form-group
                label="사용기간"
                label-for="banner-exposure-date"
              >
                <b-input-group 
                id="banner-exposure-date"
                >
                  <b-input-group-prepend is-text>
                    <b-form-radio-group 
                      v-model="dateSelected"  
                      id="banner-exposure-yn" 
                      name="check-button">
                      <b-form-radio value="N" id="banner-exposure-yn-Y">상시유지</b-form-radio>
                      <b-form-radio value="Y" id="banner-exposure-yn-N">설정</b-form-radio>
                    </b-form-radio-group>
                  </b-input-group-prepend>                   
                  <b-form-datepicker 
                    v-model="detail.fromDate"
                    :disabled="dateSelected!='Y'"
                    placeholder="YYYY-MM-DD" locale="ko" 
                    :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                    today-button label-today-button="오늘"
                    reset-button label-reset-button="초기화"
                    close-button label-close-button="닫기"
                  />                  
                  <b-form-datepicker 
                    v-model="detail.toDate"
                    :disabled="dateSelected!='Y'"
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
                label="내용"
                label-for="banner-content"
              >
                <b-form-textarea
                  rows="5"
                  id="banner-content"
                  placeholder="내용을 입력하세요."
                  v-model="detail.content"
                />
              </b-form-group>
            </b-col>
          </b-row>
        </b-card>

        <b-card class="col-md-5" title="배너이미지">
          <b-row align-content="center">
            <b-col cols="12" >
              <b-card no-body>
                <div v-if="(detail.file && detail.file.fileId) || imageFile">
                  <b-img
                    fluid
                    class="mb-2 imgs"
                    :src="imageFile ? imageFile : eumFileImagePath(detail.file.fileId, 0)"
                    img-alt=""
                    @click="$refs.inputFile.click()"
                  >
                  </b-img>
                  <div>
                    <h6>
                      <a :href="detail.file ? eumFileDownPath(detail.file.fileId) : ''"
                      v-text="detail.file ? detail.file.fileName : imageFileName" 
                      />
                      <b-badge id="del-badge" class="ml-1"
                        @click="onClickDelFile(detail.file ? detail.file.fileId : null)">삭제</b-badge> 
                    </h6>
                      
                  </div>
                </div>  
                <div
                  v-else 
                  @click="$refs.inputFile.click()"
                >
                  <validation-provider #default="{ errors }"
                    name="'배너이미지'"
                    rules="required"
                  >
                    <div class="fileInput" >
                    +
                    </div>
                    <small class="text-danger">{{ errors[0] }}</small>
                  </validation-provider>
                </div>
                <input
                  type="file"
                  ref="inputFile"
                  id="inputFile"
                  accept=".jpg, .png, .gif"
                  @change="onChangeImages"
                  style="display:none;"
                />
                <p class="mt-2">
                  ※ 배너 이미지 최적화 사이즈는 
                  {{bannerMaster.width}}px * {{bannerMaster.height}}px 
                  입니다.</p>
              </b-card>
            </b-col>
          </b-row>
        </b-card>

      </b-card-group>

      <b-card no-body class="col-12 bottom-contents">
        <b-row>
          <b-col cols="12" >  
            <b-form-group
              label="연결URL"
              label-for="banner-url"
            >
              <b-form-input
                id="banner-url"
                placeholder="URL을 입력하세요."
                v-model="detail.refUrl"
              />
            </b-form-group>
          </b-col>

          <b-col md="6" v-if="!isRegMode">
            <b-form-group label="작성자"
              label-for="bi-form-writerName">
              <b-form-input
                readonly
                id="bi-form-writerName"
                type="text"              
                placeholder="작성자 이름을 입력하세요."
                v-model="detail.rgtrName"                  
                :formatter="eufmtMaxLength50"
              />
            </b-form-group>
          </b-col>
          <b-col md="6" v-if="!isRegMode">
            <label>등록일 : {{detail.rgtDttm | eFmtDateTime}}</label><br/>
            <label>최종수정일 : {{detail.chngDttm | eFmtDateTime}}</label>
          </b-col>
        </b-row>

        <b-row>
          <b-col>
            <!-- 버튼 영역 -->
            <div class="text-center mt-4">
              <b-button v-if="!isRegMode"
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-danger"                
                class="mr-1"
                @click.prevent="onClickDelete"
              > 삭제
              </b-button>
              <!--
              <b-button 
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-info"                
                class="mr-1"
                @click.prevent="onClickPreview"
              > 미리보기
              </b-button>              
              -->
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="primary"
                class="mr-1"
                @click.prevent="onClickSave"
              > {{isRegMode ? '등록': '수정'}}
              </b-button>
              <b-button
                v-ripple.400="'rgba(113, 102, 240, 0.15)'"
                variant="outline-secondary"
                class="mr-1"
                @click="onClickCancel"
              > 뒤로가기
              </b-button>
            </div>
            <!-- // 버튼 영역 -->
          </b-col>
        </b-row>

      </b-card>
    </validation-observer>



    <form ref="previewForm" accept-charset="utf-8">
      <input type="hidden" name="previewData" v-model="previewData">
    </form>

    <!-- detail:{{ detail }} -->

  </b-card>  
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import {getCodes} from '@/api/coching-bo/comm/code';
import {getBannerMaster} from '@/api/coching-bo/system/bannerMaster';

import {
  addBannerAd, 
  getBannerAd, 
  setBannerAd,
  delBannerAd
} from '@/api/coching-bo/comm/banner';
import { BANNER_MODE, DEF_BANNER_AD_INF } from '@/constants/banner';
import bannerMixin from './bannerMixin';


import Ripple from 'vue-ripple-directive'
import moment from 'moment';
import { required } from '@validations';

export default {
	name: 'coching-banner-ad-write',
	mixins: [ernsUtils, bannerMixin],
	components : {
    required
  },
  directives: {
    Ripple
  },
  computed : {
    isRegMode(){
      const params = this.params;
      if(!params.hasOwnProperty('bannerSeq')){
        return true;
      }

      const pbannerSeq = params.bannerSeq;
      return !(pbannerSeq && pbannerSeq > 0);
    }
  },
	watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
  },
	props: {
    bannerMode:{
			type: String,
			default: BANNER_MODE.WRITE
		},
    bannerMstCd:{
      type: String,
      require: true,
    },
		bannerSeq:{
      type : [String, Number],
			default : 0
    },
  },
  async mounted(){
    const _vm = this;        

    await _vm.loadBannerMaster();
    await _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
	},
	data() {
		return {
      detail : {...DEF_BANNER_AD_INF, bannerMstCd : this.bannerMstCd,},

      file: null,
      imageFile : null,
      imageFileName : "",
      delFiles : [],
      selected: '',      
      params: {},
      dateSelected: 'N',
      previewData: {},

      bannerMaster : {}
    }
	},
	methods: {    
    //$route.query, $route.params, HistoryParam 등으로 초기값 설정
    getInitParam(){
      const _vm = this;      
      const query = _vm.$route.query, params = _vm.$route.params;

      _vm.params = {
        ...query
      };
    },

    async fetchData(){
      const _vm = this;
      _vm.getInitParam();
      if(!_vm.isRegMode) {
        await _vm.loadDetail();
      }
    },

    //데이터 로드
    async loadDetail(){
      const _vm = this; 
      
      //_vm.$refs["inputFile"].reset(); // b-form-file
      _vm.$refs.inputFile.value = null; // input type=file
      _vm.delFiles = [];

      if(_vm.isRegMode){
        //등록모드
        _vm.detail = {...DEFAULT_BANNER, bannerMstCd : _vm.bannerMstCd};
        return;
      }

      const params = {      
        bannerMstCd : _vm.bannerMstCd,
        bannerSeq : _vm.params.bannerSeq
      };
      
      console.log("params", params);
      const res = await getBannerAd(params);
      _vm.detail = {
        ...res.resultData
      }

      if(_vm.detail.fromDate || _vm.detail.toDate) {
        _vm.dateSelected = "Y";        
      } else {
        _vm.dateSelected = "N";        
      }
      
      //_vm.setValidDate();

      //잘못된 접근
      if(!_vm.detail.bannerSeq){
        await _vm.alertError(`내용을 찾을 수 없습니다.`);
        _vm.onClickCancel(); //뒤로가기   
      }
    },

    //등록/저장(수정)
    async onClickSave() {
      const _vm = this;
      
      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid) {
        return;
      }

      if('Y' == _vm.dateSelected){
        if(!_vm.detail.fromDate || !_vm.detail.toDate){
          await _vm.alertError(`배너 사용기간을 입력해 주십시오.`);
          return;
        }
      }

      const setType = _vm.isRegMode ? "등록" : "수정";
      const result = await _vm.$swal({
        title: `배너 ${setType}`,
        text: `배너를 ${setType} 하시겠습니까?`,
        showCancelButton: true,
        confirmButtonText: `${setType}`,
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });

      if(!result.value) {
        return;
      }
      
      try {
        _vm.loading(true);

        const params = {
          ..._vm.detail
        };
        if(_vm.dateSelected == "N"){
          params.fromDate = "";
          params.toDate = "";
        }
        
        const formData = new FormData();

        for( const key in params) {
          if("" != (""+params[key]).trim()){
            formData.append(key, params[key]);
          }
        }
        
        if(_vm.file) {
          formData.append("banner_files_01", _vm.file[0]);
        }
        // formData.append("ban_files_03", _vm.file[0]);
        
        { //삭제할 파일
          formData.append("delFileIds", _vm.delFiles.join());
        }

        let res = null;
        _vm.loading(false);
        if(_vm.isRegMode) {
          
          res = await addBannerAd(formData);
          await _vm.alertSuccess(`배너가 등록 되었습니다.`);

          _vm.goEdit({bannerSeq: res.resultData.bannerSeq}, true);
        } else {
          res = await setBannerAd(formData);
          _vm.detail = {
            ...res.resultData
          };

          _vm.alertSuccess(`배너를 수정 했습니다.`); 
          await _vm.loadDetail();
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

    onChangeImages(event) {
      const _vm = this;
      if(event.target.files) {
        const reader = new FileReader();
        reader.onload = (e) => {
          _vm.imageFile = e.target.result;

        }
        reader.readAsDataURL(event.target.files[0]);
        _vm.imageFileName = event.target.files[0].name;
        _vm.file = event.target.files;

        if(_vm.detail.file){
          _vm.delFiles.push(_vm.detail.file.fileId);
        }
      }
    },
    
    //배너 이미지 삭제
    onClickDelFile(fileId) {
      const _vm = this;

      if(fileId){
        _vm.delFiles.push(fileId);
        _vm.detail.file.fileId = null;
      }
      _vm.imageFile = null;
        
    },

    setValidDate() {
      const _vm = this;
      
      let isValid = false;
      const today = moment();
      const fromDate = _vm.detail.fromDate;
      const toDate = _vm.detail.toDate;
      if(_vm.detail.useYn == 'N') {
        _vm.selected = false;
      } 

      if(fromDate && toDate) {
        isValid = moment().isBetween(_vm.detail.fromDate, _vm.detail.toDate);
        _vm.selected = isValid;
      }

      if(fromDate && !toDate) {
        const differ = moment(fromDate).diff(today);
        if(differ > 0) {
          _vm.selected = false;
        }
      }

      if(!fromDate && !toDate) {
        _vm.selected = false;
      }
    },

    async onClickDelete() {
      const _vm = this; 

      const result = await _vm.$swal({
        title: '확인',
        text: "배너를 삭제 하시겠습니까?",
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
          ..._vm.detail
          , delYn : "Y" //삭제 처리
        };

        let res = null;
        if(_vm.isRegMode){
          return;
        }else{
          //수정모드
          res = await delBannerAd(params);
          
          await _vm.alertSuccess(`배너를 삭제 했습니다.`);      
          _vm.onClickCancel();
        }

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    //미리보기
    async onClickPreview(){
      const _vm = this;

      await _vm.alertError(`준비중 입니다.`);

      //TODO : 미리보기
    },

    //데이터 로드(마스터)
    async loadBannerMaster(){
      const _vm = this; 
      
      const params = {      
        bannerMstCd : _vm.bannerMstCd
      };

      const res = await getBannerMaster(params);
      _vm.bannerMaster = {
        ...res.resultData
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
  line-height: 200px;
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
#del-badge {
  cursor: pointer;
}

.bottom-contents{
  padding: 1rem !important;
  padding-top : 0rem !important;
}
</style>