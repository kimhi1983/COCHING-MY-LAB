<template>
<div>
  <LoginView></LoginView>

  <AlertModal ref="alertModal"></AlertModal>
</div>
  
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

import LoginView from './Login';

import { 
  isUserLoggedIn
  , getUserData
  , getHomeRouteForLoggedInUser
  , defineAbilitiesForUserAuth } from '@/auth/utils';
import useJwt from '@/auth/jwt/useJwt';

export default {
  name: 'coching-Sns-Login',
  mixins: [ernsUtils],
  components : {
    LoginView,
  },
  computed : { 
    publicPath(){
      return process.env.PUBLIC_PATH
    },   
  },
  props: {
    nextUrl: {
      type: String,
      default : ''
    },
    retRoute: {
      type: Object
    }    
  },
  data(){
    return {
      rejectModal: {
        rejectReason: "",
        bsnSeq: 0
      },
      joinDttm: "",

    }
  },
  mounted(){
    const _vm = this;
    _vm.docReady();

    
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    async docReady(){
      const _vm = this;

      const action = window.$giCochingPartner.getServerSideSnsAction();
      const message = window.$giCochingPartner.getServerSideSnsMessage();
      const loginInfo = window.$giCochingPartner.getServerSideSnsLoginRet();
      const joinInfo = window.$giCochingPartner.getServerSideSnsJoinRet();

      console.debug(`action:${action}`);
      console.debug(`message:${message}`);
      console.debug(`loginInfo:${loginInfo}`);
      console.debug(`joinInfo:${joinInfo}`);
      //console.debug(`${useJwt.jwtConfig.registerEndpoint}`);

      switch(action){
        case "leaveUser":          
          await _vm.alertError('탈퇴한 회원입니다.\n메인화면으로 이동합니다.');
          location.href = `/`;
          return;

        case "join":                    
          const joinData = {
            userInfo : {
              scode : joinInfo.scode,
              snsType : joinInfo.snsType,
              userId : joinInfo.snsId,
              userPw : null,
              userName : joinInfo.name,
              phone : joinInfo.phone,
              email : joinInfo.email
            }
          };
          localStorage.setItem(useJwt.jwtConfig.storageJoinUserDataKeyName,  JSON.stringify(joinData));
          //await _vm.alertSuccess('회원가입 페이지로 이동합니다.');
          location.href = `${useJwt.jwtConfig.registerEndpoint}`;
          return;
        case "login": //일반 SNS 로그인
          useJwt.setToken(loginInfo.accessToken);
          useJwt.setRefreshToken(loginInfo.refreshToken); 

          const resUserData = loginInfo.userData;

          const userData = {
              userSeq : resUserData.userSeq
              ,userId : resUserData.id
              ,ptnSeq: resUserData.ptnSeq
              ,userName : resUserData.userName
              ,email : resUserData.email
              ,userType : resUserData.userType
              //개인 정보 사항 추가 금지(ex:phone)
            };
          userData.ability = defineAbilitiesForUserAuth([]);
          _vm.$ability.update(userData.ability);
          localStorage.setItem(useJwt.jwtConfig.storageUserDataKeyName, JSON.stringify(userData));
          _vm.$store.dispatch('coching/setUserInfo', userData);

          {
            //Load User Info
            await _vm.ermLoadUserBasicInfo();

            //Load User base url
            await _vm.ermLoadUserBaseUrl();
          }
          
          _vm.ermReplacePage({name:'coching-raw-main'});

          break;

        default:
          location.href = `${useJwt.jwtConfig.loginPage}`;
          break;
      }      
    },

  }  
}

</script>

<style lang="scss" scoped>
  
</style>

<style lang="scss">

</style>