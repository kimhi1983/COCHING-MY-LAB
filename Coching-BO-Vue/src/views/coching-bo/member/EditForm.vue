<template>
  <div class="sys-mid-wrap">
    
    <b-card no-body>
      <validation-observer ref="userInfoForm">
        <!-- 입력 영역 -->
        <b-card-group class="mt-1">
          <b-card title="회원정보" class="col-md-12">
              <b-row>
                <b-col md="6" ref="membId" class="membId_under">
                  <b-form-group label-for="membId">
                    <template v-slot:label>
                        <span>아이디</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'아이디'"
                      rules="required"
                    >
                      <b-form-input
                        id="membId"
                        v-model="localData.membInfo.membId"
                        :state="errors.length > 0 ? false:null"                     
                        autoptnlete="off"
                        aria-readonly="true"
                        :readonly="true"
                      />                    
                    </validation-provider>               
                  </b-form-group>
                </b-col>

                <b-col md="6" ref="membName" class="membName_under">
                  <b-form-group label-for="membName">
                    <template v-slot:label>
                        <span>이름</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'이름'"
                      rules="required"
                    >
                      <b-form-input
                        id="membName"
                        v-model="localData.membInfo.membName"
                        :state="errors.length > 0 ? false:null"
                        autoptnlete="off"
                      />                    
                    </validation-provider>               
                  </b-form-group>
                </b-col>

                <b-col md="6">
                  <b-form-group label-for="phone">
                    <template v-slot:label>
                        <span>닉네임</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'닉네임'"
                      rules="required|duplicateNick"
                    >
                      <b-input-group>
                        <b-form-input
                          ref="nicknameInput"
                          id="nickname"
                          v-model="localData.membInfo.nickname"
                          :state="errors.length > 0 ? false:null"
                          autoptnlete="off"
                        />          
                        <b-input-group-append>
                          <b-button variant="outline-primary"
                            @click="checkDuplicateNick"
                          >중복확인</b-button>      
                        </b-input-group-append>
                      </b-input-group>          
                      <small class="text-danger">{{ errors[0] || MSG.duplicateNick }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>
                <validation-provider #default="{ errors }"
                  :name="$t('') || '닉네임 체크'" rules="required">
                  <input v-model="localData.membInfo.nicknameConfirm" type="hidden" />
                </validation-provider>

                <b-col md="6">
                  <b-form-group label-for="phone">
                    <template v-slot:label>
                        <span>휴대폰번호</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'휴대폰 번호'"
                      rules="required"
                    >
                      <b-form-input
                        ref="phoneInput"
                        id="phone"
                        v-model="localData.membInfo.phone"
                        :state="errors.length > 0 ? false:null"                     
                        autoptnlete="off"
                      />                    
                      <small class="text-danger">{{ errors[0] }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>

                <b-col md="6" style="margin-top:10px">
                  <b-form-group label-for="email">
                    <template v-slot:label>
                        <span>이메일</span>
                        <span class="text-pink">*</span>
                    </template>
                    <validation-provider #default="{ errors }"
                      name="'이메일'"
                      rules="required|email|duplicateEmail"
                    >
                      <b-input-group>
                        <b-form-input
                          id="email"
                          v-model="localData.membInfo.email"
                          :state="errors.length > 0 ? false:null"
                          autoptnlete="off"
                        />        
                        <b-input-group-append>
                          <b-button variant="outline-primary"
                            @click="checkDuplicateEmail"
                          >중복확인</b-button>      
                        </b-input-group-append>
                      </b-input-group>                      
                      <small class="text-danger">{{ errors[0] || MSG.duplicateEmail }}</small>
                    </validation-provider>                
                  </b-form-group>
                </b-col>
                <validation-provider #default="{ errors }"
                  :name="$t('') || '이메일 체크'" rules="required">
                  <input v-model="localData.membInfo.emailConfirm" type="hidden" />
                </validation-provider>

                <b-col md="3" style="margin-top:10px">
                  <b-form-group
                    label="사용 여부"
                    label-for="useYn"
                  >
                    <b-form-radio-group
                      id="useYn"
                      v-model="localData.membInfo.useYn"
                      :options="[{text:'사용', value:'Y'},{text:'미사용', value:'N'}]"
                      button-variant="outline-primary"
                      buttons name="radio-btn-outline"
                    />
                  </b-form-group>
                </b-col>
                <b-col md="3" style="margin-top:10px">
                  <b-form-group label-for="pswdInit">
                    <template v-slot:label>
                        <span>비밀번호 초기화</span>
                    </template>
                    <b-button variant="outline-primary"
                            @click="onClickInitPasswd"
                          >초기화</b-button>      
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
              v-if="emulator"
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary"                
              class="mr-1"
              @click.prevent="onClickUserEmulator"
            > 사용자 에뮬레이터
            </b-button>
            <b-button
              v-if="emulator && localData.membInfo.membType != '003'"
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary"                
              class="mr-1"
              @click.prevent="onClickPtnEmulator"
            > 파트너사 에뮬레이터
            </b-button>
            <b-button
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
    

    <!-- ptnInfo:{{localData.ptnInfo}}<br/> -->
    <!-- membInfo:{{localData.memberInfo}}<br/>  -->

  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import {getCodes} from '@/api/coching-bo/comm/code';

import { 
  getMember, 
  updateMember, 
  chkDuplicate, 
  initPasswd,
  getCmMgrEmulatorUser,
  getCmMgrEmulatorPartner } from '@/api/coching-bo/member/member';

import { extend } from 'vee-validate';
import { required, passwordCochingJoin, noWhiteSpace, min, max, telOnlyNumber, email } from '@validations';

import Ripple from 'vue-ripple-directive'

const DEF_MEMB_INF = {
    ptnSeq: '',
    membSeq: null,
    membId: '',
    membIdConfirm: '',
    email: '',
    emailConfirm: '',
    membName: '',
    phone: '',
    nickname: '',
    nicknameConfirm: '',
    useYn: '',
};

export default {
  name: 'coching-bo-member-editForm',
  mixins: [ernsUtils],
  directives: {
    Ripple
  },
  computed : {
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
    'localData.membInfo.nickname' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateNick = '';
        return;
      }

      if(val !== _vm.membBackup.nickname){
        _vm.localData.membInfo.nicknameConfirm = '';
      }else{
        _vm.localData.membInfo.nicknameConfirm = true;
      }
      _vm.MSG.duplicateNick = '';

    },
    'localData.membInfo.email' : function(val){
      const _vm = this;

      if(val.length == 0) {
        _vm.MSG.duplicateEmail = '';
        return;
      }

      if(val !== _vm.membBackup.email){
        _vm.localData.membInfo.emailConfirm = '';
      }else{
        _vm.localData.membInfo.emailConfirm = true;
        _vm.MSG.duplicateEmail = '';
      }

    },
  },
  data(){
      return {
        isFocus: false,
        emulator : false,
        
        localData: {
          membInfo: {...DEF_MEMB_INF},
        },
        membBackup: {...DEF_MEMB_INF},
        codes: {
          GROUP_CODE: [],
        },

        //중복체크 문구
        MSG: {
          duplicateNick: '',
          duplicateEmail: '',
        },

        membList: [],
      }
  },
  async mounted(){
    const _vm = this;

    {//에뮬레이터 활성화
      const ee = localStorage.getItem('coching.backoffice.ee', "erns");
      _vm.emulator = "erns" == ee;
    }

    await _vm.loadCodes();
    await _vm.fetchData();

    extend('duplicateEmail', {
        validate: value => {
          return _vm.localData.membInfo.emailConfirm == true || _vm.$t('') || '이메일 중복확인을 해주세요.';
      },
    });

    extend('duplicateNick', {
        validate: value => {
          return _vm.localData.membInfo.nicknameConfirm == true || _vm.$t('') || '닉네임 중복확인을 해주세요.';
      },
    });
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
        const res = await getMember({membSeq: _vm.$route.query.membSeq});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.localData.membInfo = {...DEF_MEMB_INF, ...resultData};
        _vm.membBackup = {...DEF_MEMB_INF, ...resultData};

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
    },

    setParamData(){
      const _vm = this;
      const param = {
              ..._vm.localData.membInfo
            };

      return param; 
    },

    async onClickUpdate() {
      const _vm = this;

      const isValid = await _vm.$refs.userInfoForm.validate();
      if(!isValid){
        return;
      }

      try {      
        const result = await _vm.$swal({
          title: '회원 정보',
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

        _vm.loading(true);

        const param = _vm.setParamData();        

        const res = await updateMember(param);
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(resultCode == '0000'){
         await _vm.alertSuccess('수정되었습니다.','회원 정보');

          _vm.$router.push({ name: 'coching-bo-member-main'});
          return;
        }

      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }
    },

    async checkDuplicateEmail(){
      //닉네임 중복체크
      const _vm = this;

      _vm.loading(true);
      try{
        const res = await chkDuplicate({email: _vm.localData.membInfo.email});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.localData.membInfo.emailConfirm = true;
          _vm.MSG.duplicateEmail = _vm.$t('') || '사용 가능한 이메일입니다.';
        }else{
          _vm.localData.membInfo.emailConfirm = false;
          _vm.MSG.duplicateEmail = _vm.$t('') || '이미 사용 중인 이메일입니다.';
        }
        _vm.$refs.userInfoForm.validate();
        
      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }
    },
    async checkDuplicateNick(){
      //닉네임 중복체크
      const _vm = this;

      _vm.loading(true);
      try{
        const res = await chkDuplicate({nickname: _vm.localData.membInfo.nickname});
        const { resultCode, resultFailMessage, resultData } = res;

        _vm.loading(false);
        if(!resultData.isDuplicate){
          _vm.localData.membInfo.nicknameConfirm = true;
          _vm.MSG.duplicateNick = _vm.$t('') || '사용 가능한 닉네임입니다.';
        }else{
          _vm.localData.membInfo.nicknameConfirm = false;
          _vm.MSG.duplicateNick = _vm.$t('') || '이미 사용 중인 닉네임입니다.';
        }
        _vm.$refs.userInfoForm.validate();
        
      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }
    },

    async onClickInitPasswd(){
      //비밀번호 초기화
      const _vm = this;

      const result = await _vm.$swal({
          title: '비밀번호 초기화',
          text: '초기화 하시겠습니까?',
          showCancelButton: true,
          confirmButtonText: '초기화',
          customClass: {
            confirmButton: 'btn btn-danger ml-1',
            cancelButton: 'btn btn-outline-primary',
          },
          buttonsStyling: false,
        });

        if(!result.value) {
          return;
        }

        _vm.loading(true);
      try{
        await initPasswd({membSeq: _vm.$route.query.membSeq});

        _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '비밀번호 초기화',
          content : _vm.$t('') || '초기화 되었습니다.'
        });

      } catch(err) {
        console.error(err)
      } finally {
        _vm.loading(false);
      }

    },

    //사용자 에뮬레이터
    async onClickUserEmulator(){
      const _vm = this;

      try{
        _vm.loading(true);
        const emRes = await getCmMgrEmulatorUser({membSeq : _vm.localData.membInfo.membSeq});
        //console.debug(emRes);

        const baseUrl = emRes.resultData.targetUrl;
        const wh = window.open(baseUrl, "emUser");

        const loginUrl = baseUrl + '/common/sns/login/emulator/callback.do';
        const emParams = {
          ...emRes.resultData
        };
        setTimeout(() => {
          //console.debug(emParams);
          _vm.post_to_url(loginUrl, emParams, "emUser", "post");

        }, 1000);
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },

    //파트너 에뮬레이터
    async onClickPtnEmulator(){
      const _vm = this;

      try{
        _vm.loading(true);
        const emRes = await getCmMgrEmulatorPartner({membSeq : _vm.localData.membInfo.membSeq});
        //console.debug(emRes);

        const baseUrl = emRes.resultData.targetUrl;
        const wh = window.open(baseUrl, "emPtn");

        const loginUrl = baseUrl + '/common/sns/login/emulator/callback.do';
        const emParams = {
          ...emRes.resultData
        };
        setTimeout(() => {
          //console.debug(emParams);
          _vm.post_to_url(loginUrl, emParams, "emPtn", "post");

        }, 1000);
      }catch(err){
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }
    },


  
    post_to_url(path, params, target, method) {
      method = method || "post";

      target = target || null;
      var form = document.createElement("form");
      form.setAttribute("method", method);
      form.setAttribute("action", path);

      if (target != null)
        form.setAttribute("target", target);

        for (var key in params) {
          var hiddenField = document.createElement("input");
          hiddenField.setAttribute("type", "hidden");
          hiddenField.setAttribute("name", key);
          hiddenField.setAttribute("value", params[key]);
          form.appendChild(hiddenField);
      }

      document.body.appendChild(form);
      form.submit();

      return false;
    },
  }
}
</script>

<style lang="scss" scoped>
 .ptnName_under {
    position: relative;

    .ptnName_error {
      position: absolute;
      top: 0;
      left: 0;
      color: blue;
    }

    .ptnName_list {
      position: absolute;
      top: calc(100% + 20px);
      left: 0;
      width: 100%;
      min-height: 100px;
      max-height: 100px;
      overflow-y: scroll;
      z-index: 1; 
      border: 1px solid #ccc; 
      background-color: #fff;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); 
    }

    .auto-box-content {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        padding: 10px;
        border-bottom: 1px solid #eee;
        transition: background-color 0.3s;

        &:hover {
          background-color: #f5f5f5;
          cursor: pointer;
        }

        a {
          color: #333;
          text-decoration: none;
          font-weight: bold;

          &:hover {
            color: #e44d26;
          }
        }
      }
    }
  }

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
</style>

<style lang="scss">

</style>