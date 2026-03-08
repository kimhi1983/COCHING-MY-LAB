<template>
  <nav :class="{'active': !showNav}">
    <!--btn-acd-->
    <button @click="onClickNavShow" type="button" class="btn-acd"><span class="ic-md ic-arrow-right-primary"></span></button>
    <!--header-->
    <div class="header">
      <!--logo-->
      <a @click="onClickManage()" href="javascript:;" class="logo">
        <img src="@/assets/images/logo.png" alt="COCHING" />
      </a>
      <!--my-profile-->
      <div class="my-profile">
        <div class="img">
          <img src="@/assets/images/ic-profile.svg" alt="profile" />
        </div>
        <div class="my-id">{{ eumLoginUserName }}</div>
        <!-- 로그인인 -->
        <button v-show="!isLoggedIn" @click="onClickLogin()" type="button" class="btn btn-sm btn-primary">로그인</button>
        <!--로그아웃-->
        <button v-show="isLoggedIn" @click="onClickLogout()" type="button" class="btn btn-sm btn-gray-outline">로그아웃</button>
      </div>
    </div>
    <!--body-->
    <div class="body scroller">
      <div class="gnb">
        <!-- <div class="group">
          <a @click="onClickHome()" href="javascript:;" :class="menuType === 'HOME' ? 'active' : ''" class="ic-home-md">홈</a>
        </div> -->
        <div class="group">
          <label>원료</label>
          <a @click="onClickManage()" 
            :class="menuType === 'MANAGE' ? 'active' : ''" 
            href="javascript:;" class="ic-drop-md">원료 등록 및 관리</a>
          <a v-if="eumLoginUser && '002' == eumLoginUser.userType"
            @click="onClickGarbage()" 
            :class="menuType === 'GARBAGE' ? 'active' : ''" 
            href="javascript:;" class="ic-trash-md">휴지통</a>
          <a v-if="eumLoginUser && '002' == eumLoginUser.userType" 
            @click="onClickDesign()" 
            :class="menuType === 'DESIGN' ? 'active' : ''" 
            href="javascript:;" class="ic-user-md">담당자 지정</a>
        </div>
        <div class="group">
          <label>가이드</label>
          <a @click="onClickGuide()" :class="menuType === 'GUIDE' ? 'active' : ''" href="javascript:;" class="ic-book-md">원료 등록 가이드</a>
          <!-- <a @click="onClickFaq()" :class="menuType === 'FAQ' ? 'active' : ''" href="javascript:;" class="ic-faq-md">자주 묻는 질문</a> -->
        </div>
        <div v-if="eumLoginUser && '002' == eumLoginUser.userType" class="group">
          <label>계정</label>
          <a @click="onClickAccount()" :class="menuType === 'ACCOUNT' ? 'active' : ''" href="javascript:;" class="ic-setting-md">계정관리</a>
        </div>
      </div>
    </div>
    <!--footer-->
    <div class="footer">
      <button @click="goCochingUser()"
        type="button" class="btn btn-md btn-primary-outline w-100">COCHING 홈페이지</button>
    </div>

      <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
  </nav>

</template>

<script>
import appConfig from '@/config';
import ernsUtils from '@/components/mixins/ernsUtils';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser, isFirstVisit } from '@/auth/utils'
import {loadLocaleMessages, defaultLocale as DEFAULT_LOCALE} from '@/utils/i18n';
import { localize } from 'vee-validate';
import { i18n } from '@/utils/i18n';

const DEFAULT_NAVINFO = appConfig.DEFAULT_NAVINFO;
const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;


export default {
  name: 'BasicNav',
  mixins: [ernsUtils],
  components: {    
	},
  filters : {
    fmtLocale(val){
      if('KO' == val.toUpperCase()){
        return 'KR';
      }
      return val.toUpperCase();
    }
  },
  computed: {
    //언어선택 아이콘
    localeIcon(){
      const _vm = this;
      return _vm.isMainPage ? LOCALE_IMG_MAIN : LOCALE_IMG_NO_MAIN
    },
    menuType(){
      const _vm = this;
      const menuType = _vm.$route.meta.menuType;
      let ret = '';
      if(!menuType){
        return 'HOME';
      }

      ret = menuType;

      return ret;
    },  
    showNav() {
      return this.$store.state.coching.isNavShow;
    },  
  },
  watch: {
    $route(oldVak, newVal) { //라우트가 변경되면 메소드를 다시 호출됩니다.
      this.fetchData();
      this.docReady();
    }
  },
  data(){
    return {
      //로그인 여부
      isLoggedIn : false,

      toggleUserMenu : false,

      navInfo:{...DEFAULT_NAVINFO},
      fMeta:{
        header    : {...DEFAULT_NAVINFO.meta.header},
      },
    }
  },
  created(){
    
  },
  beforeDestroy(){
    
  },
  mounted() {
    const _vm = this;

    _vm.fetchData();    
    _vm.docReady();
    //console.log(this.$router.currentRoute);
  },
  methods:{
    //언어 변경
    async onChangeLocale(){
      const _vm = this;

      const newLocale = 'ko' == _vm.$i18n.locale ? 'en' : 'ko';
      
      if (!_vm.$i18n.messages[newLocale]) {
        _vm.$i18n.setLocaleMessage(newLocale, await loadLocaleMessages(newLocale));
      }
      _vm.$i18n.locale = newLocale;
      _vm.$store.dispatch('coching/setLocale', _vm.$i18n.locale);

      //validate 언어변경
      localize(_vm.$i18n.locale);

      let pageTitle = "";

      if(i18n.locale === 'ko'){
        pageTitle = _vm.$router.currentRoute.meta.pageTitle.ko;
      }else{
        pageTitle = _vm.$router.currentRoute.meta.pageTitle.en;
      }

      const title = _vm.$router.currentRoute.meta.pageTitle === undefined ? 'COCHING' : pageTitle;      
      document.title = title;
    },

    onClickNavShow(){
      const _vm = this;
      this.$store.dispatch('coching/setNavShow', !this.$store.state.coching.isNavShow);
    },

    //홈이동
    // onClickHome(){
    //   const _vm = this;

    //   _vm.ermReplacePage({name:'coching-main'});
    // },

    //원료 등록 및 관리
    onClickManage(){
      const _vm = this;
      
      _vm.ermPushPage({name:'coching-raw-main'});
    },

    //휴지통
    onClickGarbage(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-garbage-main'});      
    },

    //담당자 지정
    onClickDesign(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-design-main'});      
    },

    //원료 등록 가이드
    onClickGuide(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-guide-main'});      
    },

    //자주 묻는 질문
    onClickFaq(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-faq-board-list'});      
    },

    //계정관리
    onClickAccount(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-account-main'});      
    },

    //계정관리
    onClickLogin(){
      const _vm = this;

      _vm.ermPushPage({name:'coching-login'});
    },

     //로그아웃
		async onClickLogout(){
			const _vm = this;

			const ret = await _vm.$refs["confirmModal"].open({
				title: _vm.$t('') || '로그아웃 하시겠습니까?',
				content : ''
			});

			if(!ret){
				return;
			}

			_vm.eumLogout(); 
		},

    goCochingUser(){
      const _vm = this;

      _vm.loading(true);
      try {

        // 새 창으로 URL 열기
        const userBaseUrlData = _vm.eumUserBaseUrlData;
        const baseUrl = userBaseUrlData.baseUrl ? userBaseUrlData.baseUrl : 'https://www.coching.co.kr';

        if(!_vm.eumRefreshToken){
          window.open(baseUrl, '_blank');
        }else{

          const url = baseUrl +"/common/user/login";

          // 새 창 열기
          const newWindow = window.open('', '_blank'); // 빈 새 창을 열음

          // 새 창이 열렸는지 확인 (팝업 차단 방지)
          if (!newWindow) {
            alert('팝업이 차단되었습니다. 팝업 차단을 해제해주세요.');
            return;
          }

          // 새 창에서 POST 요청을 위한 form 생성
          const form = newWindow.document.createElement('form');
          form.method = 'POST';
          form.action = url;

          // 전달할 데이터
          const params = {
            refreshToken: _vm.eumRefreshToken,
          };

          // form에 데이터 추가
          for (const key in params) {
            if (params.hasOwnProperty(key)) {
              const input = document.createElement('input');
              input.type = 'hidden'; // 숨겨진 input으로 추가
              input.name = key; // 파라미터 이름
              input.value = params[key]; // 파라미터 값
              form.appendChild(input); // form에 추가
            }
          }

          // 새 창 body에 form 추가
          newWindow.document.body.appendChild(form);

          // form 제출
          form.submit();
        }

      } catch (err) {
        console.error(err);
      } finally {
        _vm.loading(false);
      }
    },

    async fetchData(){
      const _vm = this;

      _vm.isLoggedIn = (isUserLoggedIn() || null) ? true : false;
     
      const curRoute = _vm.$router.currentRoute;
      _vm.navInfo = {
        ...DEFAULT_NAVINFO,
        curPath : curRoute.path,
        meta : {...curRoute.meta}
      };

      _vm.fMeta.header = {...DEFAULT_NAVINFO.meta.header, ...curRoute.meta.header};
      _vm.fMeta.bottomNav = {...DEFAULT_NAVINFO.meta.bottomNav, ...curRoute.meta.bottomNav};
    },

    docReady(){
      const _vm = this;

      // 다른 영역 클릭 시 link-dropdown 클래스 삭제
      $(document).on('click', function (e) {
        //console.debug($(e.target));

        const $userMenuLayer = $("header .userMenuLayer");

        if (!$userMenuLayer.is(e.target) && $userMenuLayer.has(e.target).length === 0) {
          _vm.toggleUserMenu = false;
        }
      });

    },
  }
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">
</style>
