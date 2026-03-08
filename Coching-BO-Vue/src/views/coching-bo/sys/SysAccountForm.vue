<template>
  <div>
    <b-card
      title="기본정보">
      <validation-observer ref="membAccountRules">
        <b-form>

          <b-row>
            <b-col md="4">
              <b-form-group label="ID"
                label-for="ua-form-id">
                <validation-provider #default="{ errors }"
                  name="'ID'"
                  :rules="isRegMode ? 'required|userId|min:6|max:16' : ''"
                 >
                  <b-input-group>
                    <b-form-input
                      id="ua-form-id"
                      v-model="detail.membId"
                      :readonly="!isRegMode"
                      :state="errors.length > 0 ? false:null"
                      placeholder="아이디를 입력하세요."         
                      @change="isCheckedMembIdOverlap=false"             
                      :formatter="(val)=>{return eufmtMaxLength(val, 16)}"
                    />
                    <b-input-group-append v-if="isRegMode">                                            
                      <b-button variant="primary"
                        :disabled="detail.membId.length <= 0 || errors.length > 0"                        
                        @click.prevent="checkDuplicateId"
                      >중복확인</b-button>
                    </b-input-group-append>                                        
                  </b-input-group>
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>                
              </b-form-group>
            </b-col>

            <b-col md="4">
              <b-form-group label="Password"
                label-for="ua-form-pswd">
                <validation-provider #default="{ errors }"
                  name="'Password'"
                  :rules="'min:6|max:16|password' + (isRegMode ? '|required':'')"
                >
                  <b-form-input
                    id="ua-form-pswd" type="password"    
                    placeholder="비밀번호를 입력하세요."
                    v-model="detail.pswd"
                    :state="errors.length > 0 ? false:null"                    
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="4">
              <b-form-group label="Password 확인"
                label-for="ua-form-pswd-ck">
                <validation-provider #default="{ errors }"
                  name="'Password 확인'"
                  :rules="'checkPass' + ( detail.pswd.length > 0 ? '|required':'')"
                >
                  <b-form-input
                    id="ua-form-pswd-ck" type="password"  
                    placeholder="확인을 위해 비밀번호를 한번더 입력하세요."
                    v-model="detail.checkPswd"
                    :state="errors.length > 0 ? false:null"                    
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="4">
              <b-form-group label="이름"
                label-for="ua-form-membName">
                <validation-provider #default="{ errors }"
                  name="'이름'"
                  rules="required|min:2|max:30"
                >
                  <b-form-input
                    id="ua-form-membName"     
                    placeholder="성명을 입력하세요."
                    v-model="detail.membName"
                    :state="errors.length > 0 ? false:null"
                    :formatter="(val)=>{return eufmtMaxLength(val, 16)}"                
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="4">
              <b-form-group label="연락처"
                label-for="ua-form-phone">
                <validation-provider #default="{ errors }"
                  name="'연락처'"
                  rules="required|min:2|phone|max:30"
                >
                  <b-form-input
                    id="ua-form-phone"     
                    placeholder="연락처를 입력하세요."
                    v-model="detail.phone"
                    :state="errors.length > 0 ? false:null"                    
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="4">
              <b-form-group label="e-mail"
                label-for="ua-form-email">
                <validation-provider #default="{ errors }"
                  name="'e-mail'"
                  rules="required|email"
                >
                  <b-form-input
                    id="ua-form-email"     
                    placeholder="이메일을 입력하세요."
                    v-model="detail.email"
                    :state="errors.length > 0 ? false:null"                    
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>
            </b-col>

            <b-col md="4">
              <b-form-group label="사용여부" label-for="ua-form-use-yn">
                <b-form-radio-group
                  id="ua-form-use-yn"
                  v-model="detail.useYn"
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

            <b-col md="6" v-if="!isRegMode">
              <label>등록일 : {{detail.rgtDttm | eFmtDateTime}}</label><br/>
              <label>최종수정일 : {{detail.chngDttm | eFmtDateTime}}</label>
            </b-col>
            
          </b-row>

        </b-form>
      </validation-observer>

      <div class="text-center mt-4">
        <!-- <b-button v-if="!isRegMode"
          v-ripple.400="'rgba(113, 102, 240, 0.15)'"
          variant="outline-danger"                
          class="mr-1"
          @click.prevent="onClickDelete"
        > 삭제
        </b-button> -->
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
    </b-card>
    
  </div>
</template>

<script>

const DEFAULT_ADMIN_MEMB = {
  membSeq : 0,
  membId : "",  
  pswd : "",
  checkPswd : "",
  membType: "001",
  membName : "",
  phone : "",
  email : "",
  useYn : "Y",

  rgtrSeq : 0,
  rgtDate : "",
  chnrSeq : 0,
  chngDate : ""
};

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive';
import { 
  getAdminMemb, 
  checkDuplicateId,
  insertAdminMemb,
  updateAdminMemb
} from '@/api/coching-bo/user/adminUser';

import { extend } from 'vee-validate';
import { required } from '@validations';

export default {
  name: '',
  mixins: [ernsUtils],  
  components : {
    required
  },
  computed: {
    isRegMode(){
      const params = this.params;
      if(!params.hasOwnProperty('membSeq')){
        return true;
      }

      const pMembSeq = parseInt(params.membSeq || "0", 10);
      return pMembSeq <= 0;
    }
  },
  props: {

  },
  directives: {
    Ripple
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
    "detail.pswd"(newValue) {
      const _vm = this;
      _vm.$refs.membAccountRules.validate(); // 전체 Validation 실행
    }
  },
  data(){
    return {
      detail : {...DEFAULT_ADMIN_MEMB},
      params : {},
      isCheckedMembIdOverlap : false,
    }
  },
  mounted(){
    const _vm = this;    
    extend('checkPass', {
      validate : value => {
        return _vm.detail.pswd === value;
      },
      message: '비밀번호가 일치하지 않습니다.',
    }); 

    _vm.fetchData();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    getInitParam(){
      const _vm = this;      
      const query = _vm.$route.query, params = _vm.$route.params;
      console.debug(query);
      _vm.params = {
        ...query
      };
    },

    fetchData(){
      const _vm = this;
      _vm.getInitParam();
      _vm.loadDetail();
    },

    async loadDetail() {
      const _vm = this;

      if(_vm.isRegMode){
        //등록모드
        _vm.detail = {
          ...DEFAULT_ADMIN_MEMB,
          membSeq : _vm.params.membSeq,
        };
        _vm.isCheckedMembIdOverlap = false;          
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          membSeq : _vm.params.membSeq,          
        };

        const res = await getAdminMemb(params);
        console.debug(res);
        _vm.detail = {
          ...DEFAULT_ADMIN_MEMB,
          ...res.resultData
        };
        
        //잘못된 접근
        if(!_vm.detail.membSeq || _vm.detail.membSeq <= 0){
          _vm.loading(false);

          await _vm.alertError(`사용자 계정을 찾을 수 없습니다.`);
          //_vm.$router.replace({ name: 'coching-bo-sys-account-main'});        
        }
      }catch(err){
        console.error(err);
      }finally{
        _vm.loading(false);
      }    
    },

    async checkDuplicateId(){
      //id 중복체크
      const _vm = this;

      _vm.loading(true);
      try{
        const res = await checkDuplicateId({ membId : _vm.detail.membId });
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.isCheckedMembIdOverlap = true;
          _vm.alertSuccess(`사용 가능한 아이디 입니다.`);
        }else{
          _vm.isCheckedMembIdOverlap = false;
          _vm.alertError(`이미 가입된 아이디 입니다.`);
        }
        
      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }
    },

    async onClickSave() {
      const _vm = this;

      const isValid = await _vm.$refs.membAccountRules.validate();
      if(!isValid){
        return;        
      }

      if(_vm.isRegMode && !_vm.isCheckedMembIdOverlap){
        _vm.alertError("아이디 중복 확인을 진행해 주십시오.");
        return;
      }

      try{
        _vm.loading(true);
        const params = {
          ..._vm.detail
        };

        let res = null;
        if(_vm.isRegMode){

          //등록모드
          res = await insertAdminMemb(params);
          _vm.loading(false);

          await _vm.alertSuccess(`계정이 등록 되었습니다.`);

          _vm.$router.replace({ name: 'coching-bo-sys-account-main'});
          return;
        }else{
          //수정모드
          res = await updateAdminMemb(params);
          _vm.detail = {
            ...DEFAULT_ADMIN_MEMB,
            ...res.resultData
          };
          _vm.detail.pswd = _vm.detail.pswd || "";

          await _vm.alertSuccess(`계정정보를 수정 했습니다.`);
          _vm.$router.replace({ name: 'coching-bo-sys-account-main'});
        }

      }catch(err){
        console.error(err);
      }finally{
        _vm.loading(false);
      }
    },

    onClickCancel() {
      const _vm = this;
      _vm.$router.back();
    }
  }  
}
</script>

<style lang="scss" scoped>

</style>