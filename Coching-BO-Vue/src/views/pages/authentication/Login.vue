<template>
  <div class="auth-wrapper auth-v2">
    <b-row class="auth-inner m-0">

      <!-- Brand logo-->
      <b-link class="brand-logo">
        <!-- <vuexy-logo /> -->
        <h2 class="brand-text text-primary ml-1">
          Coching BackOffice
        </h2>
      </b-link>
      <!-- /Brand logo-->

      <!-- Left Text-->
      <b-col
        lg="8"
        class="d-none d-lg-flex align-items-center p-5"
      >
        <div class="w-100 d-lg-flex align-items-center justify-content-center px-5">
          <b-img
            fluid
            :src="imgUrl"
            alt="Login V2"
          />
        </div>
      </b-col>
      <!-- /Left Text-->

      <!-- Login-->
      <b-col
        lg="4"
        class="d-flex align-items-center auth-bg px-2 p-lg-5"
      >
        <b-col
          sm="8" md="6" lg="12" class="px-xl-2 mx-auto"
        >
          <b-card-title
            class="mb-1 font-weight-bold"
            title-tag="h2"
          >
            안녕하세요.<br/>
            Coching BackOffice 입니다.
          </b-card-title>
          <b-card-text class="mb-2">
            관리자 계정으로 로그인 하십시오.
          </b-card-text>

          <b-alert
            variant="primary" show
          >
            <div class="alert-body font-small-2">
              <p>
                <small class="mr-50">사용자 계정관련 사항은 관리자에게 문의하십시오.</small>
              </p>
              <!-- <p>
                <small class="mr-50"><span class="font-weight-bold">TEL:</span> 000-0000-0000</small>
              </p> -->
            </div>
            <!-- <feather-icon
              v-b-tooltip.hover.left="'This is just for ACL demo purpose'"
              icon="HelpCircleIcon"
              size="18"
              class="position-absolute"
              style="top: 10; right: 10;"
            /> -->
          </b-alert>

          <!-- form -->
          <validation-observer
            ref="loginForm"
            #default="{invalid}"
          >
            <b-form
              class="auth-login-form mt-2"
              @submit.prevent="login"
            >
              <!-- userId -->
              <b-form-group
                label="아이디" label-for="login-userId"
              >
                <validation-provider
                  #default="{ errors }"
                  name="아이디" vid="userId" rules="required">
                  <b-form-input
                    id="login-userId"
                    v-model="userId"
                    :state="errors.length > 0 ? false:null"
                    name="login-userId"
                    placeholder="아이디를 입력하세요"
                  />
                  <small class="text-danger">{{ errors[0] }}</small>
                </validation-provider>
              </b-form-group>

              <!-- forgot password -->
              <b-form-group>
                <!-- <div class="d-flex justify-content-between">
                  <label for="login-password">Password</label>
                  <b-link :to="{name:'auth-forgot-password'}">
                    <small>Forgot Password?</small>
                  </b-link>
                </div> -->
                <validation-provider
                  #default="{ errors }"
                  name="Password"
                  vid="password"
                  rules="required"
                >
                  <b-input-group
                    class="input-group-merge"
                    :class="errors.length > 0 ? 'is-invalid':null"
                  >
                    <b-form-input
                      id="login-password"
                      v-model="password"
                      :state="errors.length > 0 ? false:null"
                      class="form-control-merge"
                      :type="passwordFieldType"
                      name="login-password"
                      placeholder="Password"
                    />
                    <b-input-group-append is-text>
                      <feather-icon
                        class="cursor-pointer"
                        :icon="passwordToggleIcon"
                        @click="togglePasswordVisibility"
                      />
                    </b-input-group-append>
                  </b-input-group>
                  <small class="text-danger">{{ errors[0]}}</small>
                </validation-provider>
              </b-form-group>

              <!-- checkbox -->
              <b-form-group>
                <b-form-checkbox
                  v-model="status" 
                  id="remember-me" name="checkbox-1"
                > 로그인 상태 유지
                </b-form-checkbox>
              </b-form-group>

              <!-- submit buttons -->
              <b-button
                type="submit" variant="primary" block
                :disabled="invalid"
              > Sign in
              </b-button>
            </b-form>
          </validation-observer>

          <!-- 회원가입
          <b-card-text class="text-center mt-2">
            <span>New on our platform? </span>
            <b-link :to="{name:'auth-register'}">
              <span>&nbsp;Create an account</span>
            </b-link>
          </b-card-text>

          <div class="divider my-2">
            <div class="divider-text">or</div>
          </div>

          <div class="auth-footer-btn d-flex justify-content-center">
            <b-button
              variant="facebook"
              href="javascript:void(0)"
            >
              <feather-icon icon="FacebookIcon" />
            </b-button>
            <b-button
              variant="twitter"
              href="javascript:void(0)"
            >
              <feather-icon icon="TwitterIcon" />
            </b-button>
            <b-button
              variant="google"
              href="javascript:void(0)"
            >
              <feather-icon icon="MailIcon" />
            </b-button>
            <b-button
              variant="github"
              href="javascript:void(0)"
            >
              <feather-icon icon="GithubIcon" />
            </b-button>
          </div> -->
        </b-col>
      </b-col>
    <!-- /Login-->    
    </b-row>
  </div>
</template>

<script>
/* eslint-disable global-require */
import { ValidationProvider, ValidationObserver } from 'vee-validate'
import VuexyLogo from '@core/layouts/components/Logo.vue'
import {
  VBTooltip,
} from 'bootstrap-vue'
import useJwt from '@/auth/jwt/useJwt'
import { required, email } from '@validations'
import { togglePasswordVisibility } from '@core/mixins/ui/forms'
import store from '@/store/index'
import { getHomeRouteForLoggedInUser, getUserGroupsArray  } from '@/auth/utils'

import ToastificationContent from '@core/components/toastification/ToastificationContent.vue'
import ernsUtils from '@/components/mixins/ernsUtils';

export default {
  directives: {
    'b-tooltip': VBTooltip,
  },  
  components: {    
    VuexyLogo,
    ValidationProvider,
    ValidationObserver,

    // validation rules
    required,
    email,
  },
  mixins: [togglePasswordVisibility, ernsUtils],
  data() {
    return {
      status: '',
      userId: '',
      password: '',      
      sideImg: require('@/assets/images/pages/login-v2.svg'),
    }
  },
  computed: {
    passwordToggleIcon() {
      return this.passwordFieldType === 'password' ? 'EyeIcon' : 'EyeOffIcon'
    },
    imgUrl() {
      if (store.state.appConfig.layout.skin === 'dark') {
        // eslint-disable-next-line vue/no-side-effects-in-computed-properties
        this.sideImg = require('@/assets/images/pages/login-v2-dark.svg')
        return this.sideImg
      }
      return this.sideImg
    },
  },
  methods: {
    async login() {
      const _vm = this;

      const isValid = await _vm.$refs.loginForm.validate();
      if(!isValid){
        return;
      }

      try{
        const response = await useJwt.login({
          userId: this.userId,
          password: this.password,
          remainLogin : this.status,
        });
        const {resultData, resultCode, resultFailMessage} = response;
        if(resultCode != "0000"){
          _vm.alertError(resultFailMessage || '알수 없는 오류가 발생했습니다.');
          return;
        }    
    
        useJwt.setToken(resultData.accessToken);
        useJwt.setRefreshToken(resultData.refreshToken); 

        //사용자 정보처리       
        const dummyUser = {
          id: 1,
          fullName: 'John Doe',
          username: 'johndoe',
          avatar: require('@/assets/images/erns/erns_app_icon.png'),
          email: 'admin@demo.com',
          role: 'admin',
          ability: [
            {
              action: 'manage',
              subject: 'all',
            },
          ]
        };
        const resUserData = resultData.userData;
        const resUserRoles = resultData.userRoles;
        //const resUserGroups = resultData.boUserGroups;
        const resUserGroups = [{
          groupCode : "UGC_BO_01000", groupName : "관리자"
        }];
        const userData = {
          ...dummyUser
          ,userSeq : resUserData.userSeq
          ,userId : resUserData.userId
          ,fullName : resUserData.userName
          ,username : resUserData.userName
          ,email : resUserData.email
          ,phone : resUserData.phone
          ,role : resUserRoles[0]
          ,boUserGroupsRaw : resUserGroups
          ,boUserGroups : getUserGroupsArray(resUserGroups)
        };
        localStorage.setItem(useJwt.jwtConfig.storageUserDataKeyName,  JSON.stringify(userData));
        _vm.$ability.update(userData.ability);

        // ? This is just for demo purpose. Don't think CASL is role based in this case, we used role in if condition just for ease
        this.$router.replace(getHomeRouteForLoggedInUser(userData.role))
        .then(() => {
          this.$toast({
            component: ToastificationContent,
            position: 'top-right',
            props: {
              title: `Welcome ${userData.fullName || userData.username}`,
              icon: 'CoffeeIcon',
              variant: 'success',
              text: `You have successfully logged in as ${userData.role}.`,
            },
          })
        })
        .catch(error => {
          this.$refs.loginForm.setErrors(error.response.data.error)
        });

      }catch(err){
        console.error(err);
        let eMsg = "비밀번호가 일치하지 않습니다.";        
        const eRes = err.response;
        if(eRes && eRes.data && eRes.data.returnMessage){
          eMsg = eRes.data.returnMessage;
        }
        _vm.alertError(eMsg);
      }finally{
        console.debug("login finished")
      }
    },
  },
}
</script>

<style lang="scss">
@import '@core/scss/vue/pages/page-auth.scss';
</style>

