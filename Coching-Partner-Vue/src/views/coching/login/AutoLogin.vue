<template>
  <LoginView></LoginView>
</template>
<script>
import LoginView from './Login';
import ernsUtils from '@/components/mixins/ernsUtils';
import { getHomeRouteForLoggedInUser } from '@/auth/utils';
import useJwt from '@/auth/jwt/useJwt';
import moment from 'moment';

export default {
  name: 'coching-partner-auto-Login',
  mixins: [ernsUtils],
  components : {
    LoginView
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

      _vm.eumClearLoginInfo();

      const action = window.$giCochingPartner.getServerSideAutoAction();
      const message = window.$giCochingPartner.getServerSideAutoMessage();
      const loginInfo = window.$giCochingPartner.getServerSideAutoLoginRet();
      const joinInfo = window.$giCochingPartner.getServerSideAutoJoinRet();

      // console.debug(`action:${action}`);
      // console.debug(`message:${message}`);
      // console.debug(`loginInfo:${loginInfo}`);
      // console.debug(`joinInfo:${joinInfo}`);
      // console.debug(`${useJwt.jwtConfig.registerEndpoint}`);

      switch(action){
        case "leaveUser":          
          //await _vm.alertError('탈퇴한 회원입니다.\n메인화면으로 이동합니다.');
          //Move login
          _vm.ermReplacePage({name:'coching-partner-login', params:{
            joinErrorCode: "20901",
            joinFailMessage: message,
          }});
          return;

        case "join":                    
          const joinData = {
            userInfo : {
              userId : joinInfo.id,
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

        case "login":
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
          };
          localStorage.setItem(useJwt.jwtConfig.storageUserDataKeyName,  JSON.stringify(userData));
          _vm.$store.dispatch('coching/setUserInfo', userData);
          //Last Login Info
          const lastLoginInfo = {
            lastLoginDate : moment().format("YYYY.MM.DD HH:mm")
          };
          localStorage.setItem(useJwt.jwtConfig.storageLastLoginInfoKeyName,  JSON.stringify(lastLoginInfo));
          { 
            //Load User Info
            await _vm.ermLoadUserBasicInfo();

            //Load User base url
            await _vm.ermLoadUserBaseUrl();
          }
          if(_vm.retRoute){
            _vm.ermReplacePage(_vm.retRoute);
          }else{
            _vm.$router.replace(getHomeRouteForLoggedInUser());
          }
          //location.href = `/`;
          break;
        default:
          location.href = `${useJwt.jwtConfig.loginPage}`;
          break;
      }      
    }
  }  
}

</script>

<style lang="scss" scoped>
  
</style>

<style lang="scss">

</style>