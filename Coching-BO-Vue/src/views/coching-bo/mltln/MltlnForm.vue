<template>
  <div class="sys-mid-wrap">
    
    <b-card :title="isRegMode ? '다국어 등록' : '다국어 수정'">
      <!-- 입력 영역 -->
      <validation-observer ref="simpleRules">
        <b-form>
          <b-row>
            <b-col md="12">
              <b-form-group label="CODE"
                label-for="mltln-code">
                <validation-provider #default="{ errors }"
                  name="'CODE'"
                  rules="required"
                >
                  <b-form-input
                    id="mltln-code"
                    v-model="detail.code"
                    :state="errors.length > 0 ? false:null"
                    placeholder="CODE를 입력하세요."                      
                    autocomplete="off"
                    :readonly="!isRegMode"
                  />                    
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>                
              </b-form-group>
            </b-col>

            <b-col md="12">
              <b-form-group label="한국어"
                label-for="mltln-codeNmKo">
                <validation-provider #default="{ errors }"
                  name="'한국어'"
                  rules="required"
                >
                  <b-form-input
                    id="mltln-codeNmKo"
                    v-model="detail.codeNmKo"
                    :state="errors.length > 0 ? false:null"
                    placeholder="한국어를 입력하세요."                      
                    autocomplete="off"
                  />                    
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>                
              </b-form-group>
            </b-col>

            <b-col md="12">
              <b-form-group label="영어"
                label-for="mltln-codeNmEn">
                <validation-provider #default="{ errors }"
                  name="'영어'"
                  rules="required"
                >
                  <b-form-input
                    id="mltln-codeNmEn"
                    v-model="detail.codeNmEn"
                    :state="errors.length > 0 ? false:null"
                    placeholder="영어를 입력하세요."                      
                    autocomplete="off"
                  />                    
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>                
              </b-form-group>
            </b-col>
          </b-row>
        </b-form>
      </validation-observer>
      <!-- // 입력 영역 -->
       

      <!-- 버튼 영역 -->
      <div class="text-center mt-4">
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
    </b-card>

  <!-- detail:{{detail}}<br/>
    isRegMode:{{isRegMode}}<br/> 
    list.params : {{list.params}}  -->


  </div>
</template>
<script>
import {getMltlnMaster, isHaveCode, insertMltln, setMltln} from '@/api/coching-bo/comm/mltlnMaster'; 

import { extend } from 'vee-validate';
import { required } from '@validations';

import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';

import vSelect from 'vue-select';
import { integer } from 'vee-validate/dist/rules';

const DEFAULT_MLTLN = {
  code : "",
  codeNmKo : "",
  codeNmEn : ""
};

export default {
  name: 'coching-bo-mltln-mltlnAdd',
  mixins: [ernsUtils],
  components : {
    vSelect
  },
  directives: {
    Ripple
  },
  computed : {
    showRows(){
      return this.$store.state.erns.showRows;
    },

    isRegMode(){
      const _vm = this;

      return !(_vm.code && _vm.code.length > 0);
    },
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
  props: {
    code: {
      type: String,
      default: "",
    },
  },
  data(){
      return {
        detail: {...DEFAULT_MLTLN},
        list: {
          params: {},
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

    fetchData(){
      const _vm = this;
      if(!_vm.isRegMode) {
        _vm.loadDetail();
      }

    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    // 다국어 등록/수정
    async onClickSave() {
      const _vm = this;

      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      // 중복데이터 확인
      try{
        _vm.loading(true);
        const params = {
          ..._vm.detail
        };        

        const setType = _vm.isRegMode ? "등록" : "수정";

        const result = await _vm.$swal({
          title: `다국어 ${setType}`,
          text: `다국어를 ${setType} 하시겠습니까?`,
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
      
        let res = null;
        if(_vm.isRegMode){
          //TODO : 키 중복확인
          //등록모드

          // CODE 공백 검사
          if(params.code !== params.code.trim()) {
            await _vm.alertError(`CODE에는 띄어쓰기를 사용할 수 없습니다.`);
            return;
          }


          const chekingHaveCode = await isHaveCode(params);
          if(chekingHaveCode.resultData.isHave == true) {
            await _vm.alertError(`CODE가 이미 존재합니다.`);
            return;
          }

          res = await insertMltln(params);
          _vm.loading(false);

          await _vm.alertSuccess(`'${params.code}' CODE가 추가 되었습니다.`);

        }else{
          //수정모드
          res = await setMltln(params);
          _vm.detail = {
            ...res.resultData
          };

          _vm.alertSuccess(`'${params.code}' CODE를 수정 했습니다.`);
        }
        _vm.$router.replace({ name: 'coching-bo-mltln-main' });

      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },
    // -- 다국어 등록/수정

    // 데이터 로드
    async loadDetail(){
      const _vm = this;

      // 등록 모드 시
      if(_vm.isRegMode){
        _vm.detail = {...DEFAULT_MLTLN};
        return;
      }

      // 수정 모드 시
      const params = {
        code : _vm.code,
      };

      const res = await getMltlnMaster(params);
      // console.debug("수정모드 데이터 확인 : ", res);
      _vm.detail = {
        ...res.resultData
      }
    },

   
  }
}
</script>

<style lang="scss" scoped>

</style>