<template>
  <!--section-->
  <section class="h-100">
       <div class="container h-100">
         <div class="content-wrap h-100">
          <div class="h1">로그인</div>
           <!--login-->
           <div class="content-inner h-100 account-wrap login-wrap">
             <div class="inner"> 
                 <validation-observer 
                   ref="loginForm"
                   #default="{invalid}"
                 >
                 <div class="input-wrap input-line only">
                   <validation-provider #default="{ errors }" tag="div"
                     class="label-set" :name="$t('') || '아이디'" rules="required">
                     <div class="input-set" :class="{'block': errors.length > 0}">
                       <input type="text" autocomplete="username"
                         v-model="userInfo.id"
                         :state="errors.length > 0 ? false:null"
                         :placeholder="$t('') || '아이디'">
                       <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                     </div>
                   </validation-provider>

                   <validation-provider #default="{ errors }" tag="div"
                     class="label-set" :name="$t('') || '비밀번호'" rules="required">
                     <div class="input-set" :class="{'block': errors.length > 0}">
                       <input name="password" autocomplete="current-password"
                         v-model="userInfo.password"
                         :state="errors.length > 0 ? false:null"
                         @keypress.enter="onDoLogin"
                         type="password" :placeholder="$t('') || '비밀번호'">
                       <div class="info error" :class="{'active' : errors.length > 0}">{{ errors[0] || status}}</div>
                     </div>
                   </validation-provider>
                 </div>
                 <div class="bottom">
                   <!--auto login-->
                   <div class="checkbox">
                     <input v-model="userInfo.autoLogin"
                       id="auto-login" type="checkbox" />
                     <label for="auto-login" class="checkbox-label">로그인 유지</label>
                   </div>
                   <!--auto login-->
                   <!-- <a @click="onClickFindIdPw"
                     href="javascript:;" class="id-pw-find modal-open id-pw-find-modal">아이디/비밀번호 찾기</a> -->
                 </div>
                 <!--button-->
                 <div class="btn-area vertical">
                   <button @click="onDoLogin" 
                     :disabled="invalid"
                     :class="{'btn-disabled' : invalid , 'btn-primary' : !invalid}"
                     type="button" class="btn btn-lg w-100">로그인</button>
                   <button @click="onDoJoin"
                     type="button" class="btn btn-lg btn-primary-outline w-100">회원가입</button>
                 </div>
               </validation-observer>
             </div>
           </div>
         </div>

         <!--id-pw-find-->
         <div ref="findIdPwModal" class="modal for-id-pw-find">
           <div class="layer">
             <div class="layer-content">
               <div class="modal-inner modal-md">
                 <div class="modal-content">
                   <div class="modal-header">
                     <div @click="onClickFindIdPwClose" class="modal-close"></div>
                   </div>
                   <div class="modal-body p-t-0">
                     <div class="tabs style-line">
                       <!--탭네비-->
                       <div class="tabs-nav">
                         <ul>
                           <li :class="findType === 'ID'? 'active' : ''"><a @click="onClickFindTab('ID')" href="javascript:;">아이디 찾기</a></li>
                           <li :class="findType === 'PSWD'? 'active' : ''"><a @click="onClickFindTab('PSWD')" href="javascript:;">비밀번호 찾기</a></li>
                         </ul>
                       </div>
                       <!--컨텐츠-->
                       <div class="tabs-content id-pw-find-content">
                         <!--id-->

                         <div id="id-find" :class="findType === 'ID'? 'active' : ''" class="tabs-panel">
                           <!--title-->
                           <div class="title">
                             가입 시 등록된<br />
                             이메일 주소를 입력하세요.
                           </div>
                           <validation-observer 
                               ref="findIdForm"
                               #default="{invalid}"
                             >
                             <!--input-->
                             <div class="input-wrap input-line">
                               <div class="label-set">
                                 <div class="input-set">
                                   <input type="email" placeholder="가입된 이메일 입력" />
                                 </div>
                               </div>
                             </div>
                             <!--button-->
                             <div class="btn-area">
                               <button type="button" class="btn btn-md btn-primary w-100">확인</button>
                             </div>
                           </validation-observer>
                         </div>

                         <!--pw-->
                         <div id="pw-find" :class="findType === 'PSWD'? 'active' : ''" class="tabs-panel">
                           <!--title-->
                           <div class="title">
                             가입 시 등록된<br />
                             이메일 주소를 입력하세요.
                           </div>
                             <validation-observer 
                               ref="findPswdForm"
                               #default="{invalid}"
                             >
                             <!--input-->
                             <div class="input-wrap input-line only">
                               <div class="label-set">
                                 <div class="input-set">
                                   <input type="email" placeholder="가입된 이메일 입력" />
                                 </div>
                               </div>
                               <div class="label-set">
                                 <div class="input-set">
                                   <input type="text" placeholder="아이디 입력" />
                                 </div>
                               </div>
                             </div>
                             <!--button-->
                             <div class="btn-area">
                               <button type="button" class="btn btn-md btn-primary w-100">비밀번호 초기화</button>
                             </div>
                           </validation-observer>
                         </div>
                       </div>
                     </div>
                   </div>
                 </div>
               </div>
             </div>
           </div>
         </div>

       <!--오류-->
       <AlertModal ref="alertModal"></AlertModal>
       <ConfirmModal ref="confirmModal"></ConfirmModal>

       <!-- 사용자 비밀번호 재설정 -->
      <UserPasswdResetModal ref="userPasswdResetModal"></UserPasswdResetModal>
       </div>
     </section>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { extend } from 'vee-validate';
import useJwt from '@/auth/jwt/useJwt';
import Promise from "bluebird";
import moment from 'moment';


import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils';
import { required, email, telOnlyNumber, certificationNumber, min, max, password, } from '@validations';

import { 
 resendMailCheck
} from '@/api/coching/comm/join';

import { 
 updatePwChngDttm,
 changePasswd
} from '@/api/coching/member/member';

import UserPasswdResetModal from '@/components/dialog/UserPasswdResetModal.vue';

const base = process.env.VUE_APP_API_BASE_URL;

const DEF_USERINFO = {
 id: '',
 userPw: '',
 autoLogin : false,
}

export default {
 name: 'coching-partner-login',
 mixins: [ernsUtils],
 components : {
  UserPasswdResetModal,
 },
 computed : {
 },
 props: {
   nextUrl: {
     type: String,
     default : ''
   },
   retRoute: {
      type: String,
      default : ''
    },
   joinErrorCode: {
     type: String,
     default: "0000",
   },
   joinFailMessage: {
     type: String,
     default: "알수 없는 오류가 발생 하였습니다.",
   },
   joinFailData: {
     type: Object,
     default: () => ({}),
   },
 },
 watch: {
     '$i18n.locale' : function(){
       const _vm = this;
       _vm.$nextTick(() => _vm.$refs.loginForm.validate());
     },
   },
 data(){
   return {
     status: '',
     userInfo : {...DEF_USERINFO},
     findType: 'ID',

     lastLoginInfo : {
       lastLoginDate : ""
     }
   }
 },
 mounted(){
   const _vm = this;
   _vm.docReady();
   _vm.fetchData();
   _vm.showJoinError();
 },
 beforeDestroy(){
   const _vm = this;
 },
 methods: {
   //가입오류
   showJoinError(){
     const _vm = this;
     if(_vm.joinErrorCode != "0000"){
       switch(_vm.joinErrorCode){
         case "20001": //기가입
           $(_vm.$refs["errorModal_have_account"]).fadeIn(300);
           break;
         
         case "20023": //만14세
           $(_vm.$refs["errorModal_14age"]).fadeIn(300);
           break;

         case "20901": //탈회회원 30일 제안
           _vm.$refs["alertModal"].open({
             title: _vm.$t('common.login.error.label001') ||  '오류',
             content : _vm.joinFailMessage || _vm.$t('common.login.error.label002') || "탈회한 회원입니다."
           });
           break;

         case "9999":
         default:
           _vm.$refs["alertModal"].open({
             title: _vm.$t('common.login.error.label001') ||  '오류',
             content : _vm.joinFailMessage || _vm.$t('common.login.error.label003') || "회원가입 도중 오류가 발생했습니다."
           });
         break;
       }
     }
   },

   //아이디/비밀번호 찾기
   onClickFindIdPw(){
     const _vm = this;
     //팝업 띄우기
     $(_vm.$refs["findIdPwModal"]).fadeIn(300);
   },

   onClickFindTab(tab){
     const _vm = this;
     _vm.findType = tab;
   },

   //약관 닫기
   onClickFindIdPwClose(){
     const _vm = this;

     $(_vm.$refs["findIdPwModal"]).fadeOut(300);
     $("body").css("overflow", "visible");
   },

   //회원가입 이동
   onDoJoin(){
     const _vm = this;
     _vm.ermPushPage({name:'coching-join'});
   },

   //로그인
   async onDoLogin(){
     const _vm = this;

     const isValid = await _vm.$refs.loginForm.validate();
     if(!isValid){
       return;
     }

     _vm.loading(true);
     try{
       _vm.eumClearLoginInfo();
       
       const loginRes = await useJwt.login({
         id: _vm.userInfo.id.trim(),
         password: _vm.userInfo.password.trim(),
         autoLogin : _vm.userInfo.autoLogin,
       });
       //console.debug(loginRes);

       const { resultCode, resultFailMessage, resultData } = loginRes;
       if(resultCode != "0000"){
         await _vm.$refs["alertModal"].open({
           title:  _vm.$t('common.login.error.label001') ||  '오류',
           content : resultFailMessage || _vm.$t('common.login.error.label004') || '알수 없는 오류가 발생했습니다.'
         });
         _vm.status = _vm.$t('common.login.error.label005') || '비밀번호가 일치하지 않습니다.';
         return;
       }else{
         _vm.status = "";
       }    
   
       useJwt.setToken(resultData.accessToken);
       useJwt.setRefreshToken(resultData.refreshToken); 

       const resUserData = resultData.userData;

       const userData = {
           userSeq : resUserData.userSeq
           ,userId : resUserData.id
           ,ptnSeq: resUserData.ptnSeq
           ,userName : resUserData.userName
           ,email : resUserData.email
           ,userType : resUserData.userType
         };
       localStorage.setItem(useJwt.jwtConfig.storageUserDataKeyName,  JSON.stringify(userData));
       _vm.$store.dispatch('coching/setUserInfo', userData);

       {
         //Load User Info
         await _vm.ermLoadUserBasicInfo();

         //Load User base url
         await _vm.ermLoadUserBaseUrl();
       }

       { //비밀번호 초기화 체크
        const pswdInitYn = resultData.pwdInitYn;
        if("Y" === pswdInitYn){
          _vm.loading(false);
          const ret = await _vm.$refs["alertModal"].open({
            title: _vm.$t('') || '안내',
            content : _vm.$t('') || '비밀번호가 초기화 되었습니다. 비밀번호를 변경해 주세요.'
          });
          if(ret) {
            await _vm.openChangePswdModal();
            return;
          }
        }
       }

       { //비밀번호 변경일 확인
         const pwdChangeDate = moment((""+resultData.pwdChangeDate).substring(0,10), "YYYY-MM-DD");
         const currentDate = moment();
         const daysPassed = currentDate.diff(pwdChangeDate, 'days');
         const maxCheckDate = 90;

         // console.debug(`daysPassed:${daysPassed}`);

         if(daysPassed > maxCheckDate){
           _vm.loading(false); 

           const ret = await _vm.$refs["confirmModal"].open({
             title: _vm.$t('') || '안내',
             content : _vm.$t('', { maxCheckDate: maxCheckDate }) ||
             `비밀번호 설정 후 ${maxCheckDate}일이 지났습니다.\n마이페이지에서 비밀번호를 변경해 주십시오.`,
             cancelButtonText : '다음에 변경',
           });
           
           if(!ret) {

             const params = {
                membSeq : resUserData.userSeq
               ,id : resUserData.id
             };

             const res = await updatePwChngDttm(params);
             _vm.ermReplacePage({name:'coching-raw-main'});
           } else {
            await _vm.openChangePswdModal();
           }
           return;            
         }
       }

       if(_vm.retRoute){
         _vm.ermReplacePage(_vm.retRoute);
       }else{
         _vm.ermReplacePage({name:'coching-raw-main'});
       }        

     }catch(err){
       //오류
       if(err.response){
         const { resultCode, resultFailMessage, resultMsg } = err.response.data;
         _vm.defaultApiErrorHandler(err); //기본에러처리    
       }

       _vm.userInfo = {...DEF_USERINFO};
       _vm.$refs.loginForm.reset();

     }finally{
       _vm.loading(false);        
     }
   },

   async openChangePswdModal(){
      const _vm = this;  
      const modalRet = await _vm.$refs["userPasswdResetModal"].open({
        title: _vm.$t('') || '비밀번호 변경',
      });
      if(!modalRet){
        if(_vm.retRoute){
         _vm.ermReplacePage(_vm.retRoute);
       }else{
         _vm.ermReplacePage({name:'coching-raw-main'});
       } 
        return;
      }

      console.debug(`modalRet:${modalRet}`);

      try{
        _vm.loading(true);
        const param = {
          membSeq : _vm.eumLoginUser.userSeq,
          pswd : modalRet.userPw,
        };

        const res = await changePasswd(param);
        const { resultCode, resultFailMessage, resultData } = res;
        
        _vm.loading(false);
        const ret = await _vm.$refs["alertModal"].open({
          title: _vm.$t('') || '안내',
          content : _vm.$t('') || '비밀번호가 변경되었습니다.\n변경된 비밀번호로 로그인해 주십시오.'
        });

        if(ret){
          _vm.eumReLogin();
        }

      } catch(err) {
        _vm.loading(false);
        await _vm.defaultApiErrorHandlerSyncAlert(err); //기본에러처리

        _vm.openChangePswdModal(); //다시 띄우기
        
      } finally {
        _vm.loading(false);
      }      
    
    },

   fetchData(){
     const _vm = this;

     const lastJoinJson = localStorage.getItem(useJwt.jwtConfig.storageLastLoginInfoKeyName);
     if(lastJoinJson){
       _vm.lastLoginInfo = JSON.parse(lastJoinJson); 
     }
   },

   async docReady(){
     const _vm = this;

   },
 }  
}

</script>

<style lang="scss" scoped>
/*퍼블에 맞추기 위해 추가*/
.account .input-wrap>.form-input:nth-child(n+2) .input-set{
 padding-top: 1rem;
}
</style>

<style lang="scss">
.modal{
 display: none;
}
</style>