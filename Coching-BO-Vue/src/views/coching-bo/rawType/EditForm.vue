<template>
  <div class="sys-mid-wrap">
    
    <b-card no-body>
      <validation-observer ref="simpleRules">
        <!-- 입력 영역 -->
        <b-card-group class="mt-1">
          <b-card title="원료타입정보" class="col-md-12">
              <b-row>

                <b-col md="6">
                  <b-form-group label-for="code">
                    <template v-slot:label>
                        <span>그룹코드</span>
                        <span class="text-pink">*</span>
                    </template> 
                    
                    <validation-provider #default="{ errors }"
                      name="'그룹코드'"
                      rules="required"
                    >
                      <v-select 
                        v-model="localData.typeInfo.grpCode"
                        :options="codes.GROUP_CODE"
                        label="codeName" 
                        track-by="etc1" 
                        :reduce="option => option.etc1"
                        :disabled="!isNew"
                        placeholder="그룹코드를 선택하세요"
                        class="d-inline-block w-100"
                      />
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider> 
                  </b-form-group>
                </b-col>

                <b-col md="6">
                  <b-form-group label-for="codeNmKo">
                    <template v-slot:label>
                        <span>한글 타입 코드명</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'한글 타입 코드명'"
                      rules="required"
                    >
                      <b-form-input
                        id="codeNmKo"
                        v-model="localData.typeInfo.codeNmKo"
                        :state="errors.length > 0 ? false:null"
                        placeholder=""                      
                        autoptnlete="off"
                      />                    
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>

                <b-col md="6" style="margin-top:10px">
                  <b-form-group label-for="codeNmEn">
                    <template v-slot:label>
                        <span>영문 타입 코드명</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'영문 타입 코드명'"
                      rules="required"
                    >
                      <b-form-input
                        id="codeNmEn"
                        v-model="localData.typeInfo.codeNmEn"
                        :state="errors.length > 0 ? false:null"
                        placeholder=""                      
                        autoptnlete="off"
                      />                    
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>

                <b-col md="6" style="margin-top:10px">
                  <b-form-group label="사용여부" label-for="ua-form-use-yn">
                    <b-form-radio-group
                      id="ua-form-use-yn"
                      v-model="localData.typeInfo.useYn"
                      :options="[
                        { text: '사용', value: 'Y' },
                        { text: '미사용', value: 'N' },
                      ]"
                      button-variant="outline-primary"
                      buttons
                      name="radio-btn-outline"
                    />
                  </b-form-group>
                </b-col>

               
              </b-row>
          </b-card>

        </b-card-group>
        <!-- // 입력 영역 -->

        <!-- 버튼 영역 -->
        <b-card-body>
          <div class="text-center mt-1">
            <b-button
              v-if="isNew"
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary"
              class="mr-1"
              @click.prevent="onClickAdd"
            > 저장
            </b-button>
            <b-button
              v-else
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary"
              class="mr-1"
              @click.prevent="onClickUpdate"
            > 수정
            </b-button>
            <b-button
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="outline-primary"
              @click="onClickCancel"
            > 취소
            </b-button>
          </div>
        </b-card-body>
        <!-- // 버튼 영역 -->

      </validation-observer>
    </b-card>

  </div>
</template>
<script>
import {getCodes} from '@/api/coching-bo/comm/code';

import { getRawType, setRawType, updateRawType } from '@/api/coching-bo/rawType/rawType'; 

import { extend } from 'vee-validate';
import { required, noWhiteSpace, min, max} from '@validations';


import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';

import vSelect from 'vue-select';
import { integer } from 'vee-validate/dist/rules';

const DEF_RAW_TYPE_INF= {
  typeSeq: '',
  grpCode: '',
  code: '',
  codeNmKo: '',
  codeNmEn: '',
  inptType: '',
  useYn: 'Y',
};

export default {
  name: 'coching-bo-raw-type-editForm',
  mixins: [ernsUtils],
  components : {
    vSelect,
  },
  directives: {
    Ripple
  },
  computed : {

  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',

  },
  data(){
      return {
        isFocus: false,
        
        localData: {
          typeInfo: {...DEF_RAW_TYPE_INF},
        },
        codes: {
          GROUP_CODE: [],
        },
        isNew: true,
      }
  },
  async mounted(){
    const _vm = this;

    _vm.isNew = _vm.$route.query.typeSeq == 0;
    await _vm.loadCodes();
    await _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
  },
  methods: {

    async fetchData(){
      const _vm = this;

      _vm.loading(true);   

      try{
        //원료사 상세

        if(!_vm.isNew){
          const res = await getRawType({typeSeq: _vm.$route.query.typeSeq});
          const { resultCode, resultFailMessage, resultData } = res;

          _vm.localData.typeInfo = {...DEF_RAW_TYPE_INF, ...resultData};
        }
        
      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }

    },

    //취소
    onClickCancel(){
      const _vm = this;
      _vm.$router.back();
    },

    async loadCodes(){
      const _vm = this;

      const codeRes = await getCodes({grpCode:"CH001", etc5:"KO"});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.codes.GROUP_CODE = [...resultData.list];
      
    },

    setParamData(){
      const _vm = this;
      const param = {
              ..._vm.localData.typeInfo
            };

      if(_vm.isNew){
        param.inptType = "C";
      }            
      
      return param; 
    },

    async onClickAdd() {
      const _vm = this;

      const rawType = _vm.localData.typeInfo;

      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      try {
    
        _vm.loading(true);
        
        const result = await _vm.$swal({
          title: '타입 정보',
          text: '저장 하시겠습니까?',
          showCancelButton: true,
          confirmButtonText: '저장',
          customClass: {
            confirmButton: 'btn btn-danger ml-1',
            cancelButton: 'btn btn-outline-primary',
          },
          buttonsStyling: false,
        });

        if(!result.value) {
          return;
        }

        const param = _vm.setParamData();        

        const res = await setRawType(param);
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(resultCode == '0000'){
         await _vm.alertSuccess('저장되었습니다.','타입 정보');

          _vm.$router.push({ name: 'coching-bo-raw-type-main'});
          return;
        }

      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }
    },

    async onClickUpdate() {
      const _vm = this;

      const rawType = _vm.localData.typeInfo;

      const isValid = await _vm.$refs.simpleRules.validate();
      if(!isValid){
        return;
      }

      try {
    
        _vm.loading(true);
        
        const result = await _vm.$swal({
          title: '타입 정보',
          text: '수정 하시겠습니까?',
          showCancelButton: true,
          confirmButtonText: '수정',
          customClass: {
            confirmButton: 'btn btn-danger ml-1',
            cancelButton: 'btn btn-outline-primary',
          },
          buttonsStyling: false,
        });

        if(!result.value) {
          return;
        }

        const param = _vm.setParamData();        

        const res = await updateRawType(param);
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(resultCode == '0000'){
         await _vm.alertSuccess('수정되었습니다.','타입 정보');

          _vm.$router.push({ name: 'coching-bo-raw-type-main'});
          return;
        }

      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }
    },
  }
}
</script>

<style lang="scss" scoped>
 
</style>

<style lang="scss">  
</style>