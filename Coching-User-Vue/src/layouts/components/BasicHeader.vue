<template>
  <!--header-->
  <header>
    <div class="inner">
          <div class="container">
            <!--logo-->
            <!-- <a href="javascript:;" @click="onClickHome()" class="logo">
              <img src="@/assets/images/logo.svg" alt="COCHING" />
            </a> -->
            <!--left-->
            <div class="left flex">
              <div class="ic-wrap menu-wrap m-none">
                <div @click="onclickGnbMenu" class="ic-lg ic-menu"></div>
              </div>
              <!--logo-->
              <a href="javascript:;" @click="onClickHome()" class="logo">
                <img src="@/assets/images/logo.svg" alt="COCHING" />
              </a>
            </div>
            <!--right-->
            <div class="right" :class="{'flex' : isLoggedIn}">
              <!--login-->
              <div v-if="!isLoggedIn" class="login flex m-none">
                <button @click="onClickLogin()" type="button" class="btn btn-sm btn-primary">로그인</button>
                <button @click="onDoJoin()" type="button" class="btn btn-sm btn-primary-outline">회원가입</button>
              </div>

              <div v-show="isLoggedIn"
                @mouseover="toggle.userMenu = true" @mouseleave="toggle.userMenu = false"
                class="user-container m-none">
                <div class="ic-wrap user-wrap">
                  <div class="ic-lg ic-user" data-template="user"></div>
                </div>
                <div :class="{'on' : toggle.userMenu}"
                  class="dropdown for-user">
                  <a @click="onClickMyPage()" href="javascript:;">{{ $t('') || '마이페이지' }}</a>
                  <a v-if="eumLoginUser && 0 < eumLoginUser.ptnSeq" 
                    @click="onClickPartner()" href="javascript:;">{{ $t('') || '원료사 홈' }}</a>
                  <a @click="onClickLogout()" href="javascript:;">{{ $t('') || '로그아웃' }}</a>
                </div>
              </div>

              <!--heart-->
              <div v-show="isLoggedIn" 
                ref="heartContainer"
                class="heart-container toggle-container m-none">
              <div @click="onClickToggleUserHeart('')" class="ic-wrap">
                <div class="ic-lg ic-heart"></div>
              </div>
              <div ref="userHeartLayer"
                :class="{'on' : toggle.userHeart}"
                class="dropdown box toggle-content for-heart">
                <!--header-->
                <div class="header">
                  <div class="title tabs style-line">
                    <!--탭네비-->
                    <div class="tabs-nav">
                      <ul>
                        <li
                          :class="{ active: rawTabType === 'favorite' }"
                          @click="rawTabType = 'favorite'">
                          <a href="javascript:;">찜한 원료</a>
                        </li>
                        <li
                          :class="{ active: rawTabType === 'recent' }"
                          @click="rawTabType = 'recent'">
                          <a href="javascript:;">최근 본 원료</a>
                        </li>
                      </ul>
                    </div>
                  </div>
                  <button @click="onClickCloseUserHeart" type="button" class="ic-md ic-close"></button>
                </div>
                <!--body-->
                <div class="body">
                  <div class="tabs-content like-recently-list-content">
                    <!--like-list-->
                    <div id="like-list" class="tabs-panel"
                      :class="{ active: rawTabType === 'favorite' }" >
                      <!--찜한 원료 없음-->
                      <div v-show="rawFavoriteData.list.length == 0" class="raw-none">찜한 원료가 없습니다.</div>
                      <!--item-->
                      <div class="item"
                        v-for="(item, index) of rawFavoriteData.list"
                        :key="'favorite-' + index"
                        @click="onClickRaw(item)">
                        <div class="title-wrap" onclick="location.href='javascript:;'">
                          <div class="title">[{{ item.rawName }}] {{ item.title }}</div>
                          <div class="info">{{item.managerName}}</div>
                        </div>
                        <div class="heart-wrap active">
                          <button type="button" class="ic-md ic-heart"></button>
                        </div>
                      </div>
                      <div class="btn-area">
                        <button type="button" class="btn btn-xsm btn-gray" @click="onClickRawList()">전체보기</button>
                      </div>
                    </div>

                    <!--recently-list-->
                    <div id="recently-list" class="tabs-panel"
                      :class="{ active: rawTabType === 'recent' }" >
                      <!--최근 본 원료 없음-->
                      <div v-show="rawRecentData.list.length == 0" class="raw-none">최근 본 원료가 없습니다.</div>
                      <!--item-->
                      <div class="item"
                        v-for="(item, index) of rawRecentData.list"
                        :key="'recent-' + index"
                        @click="onClickRaw(item)">
                        <div class="title-wrap" onclick="location.href='javascript:;'">
                          <div class="title">[{{ item.rawName }}] {{ item.title }}</div>
                          <div class="info">{{item.managerName}}</div>
                        </div>
                      </div>
                      <!--item-->
                      <div class="btn-area">
                        <button type="button" class="btn btn-xsm btn-gray" @click="onClickRawList()">전체보기</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

              <!--noti-->
              <div v-show="isLoggedIn"
                ref="userNotiContainer" 
                class="noti-container toggle-container m-none">
                <div @click="onClickToggleUserNoti" 
                  class="ic-wrap noti-wrap">
                  <!--noti-badge 두자리 수 까지만 99+-->
                  <div class="noti-badge">{{ notiData.newCnt }}</div>
                  <div class="ic-lg ic-noti"></div>
                </div>
                <div 
                  ref="userNotiLayer"
                  :class="{'on' : toggle.userNoti}"
                  class="dropdown box toggle-content for-noti">
                  <!--header-->
                  <div class="header">
                    <div class="title">알림</div>
                    <button @click="onClickCloseUserNoti"
                      type="button" class="ic-md ic-close"></button>
                  </div>
                  <!--body-->
                  <div class="body">
                    <!--알림 없음-->
                    <div v-show="notiData.list.length == 0" class="noti-none">알림이 없습니다.</div>

                    <!--item-->
                    <div v-for="(item, index) of notiData.list"
                      @click="onClickNoti(item)"
                      :key="index"
                      :class="item.chkYn === 'N' ? 'item' : 'item off'" >
                      <div class="title">{{ getNotiTitle(item) }}</div>
                      <div class="info">
                        <div class="data">{{ item.content }}</div>
                      </div>
                      <div class="date">{{ item.rgtDttm | eFmtDateHMFormat('YYYY.MM.DD') }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <!--mobile menu-->
              <div @click="onClickMenu" class="ic-wrap menu-wrap pc-none">
                <div class="ic-lg ic-menu"></div>
              </div>
            </div>
          </div>
          
          <!--search-->
          <SearchBar></SearchBar>

        </div>    
    <!--오류-->
    <AlertModal ref="alertModal"></AlertModal>
    <ConfirmModal ref="confirmModal"></ConfirmModal>
      
    <MobileMenuModal ref="mobileMenuModal"></MobileMenuModal>
  </header>
</template>

<script>
import appConfig from '@/config';
import ernsUtils from '@/components/mixins/ernsUtils';
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser, isFirstVisit } from '@/auth/utils'
import {loadLocaleMessages, defaultLocale as DEFAULT_LOCALE} from '@/utils/i18n';
import { localize } from 'vee-validate';
import { i18n } from '@/utils/i18n';

import { getCodes } from '@/api/coching/comm/code';
import { getNotiList, updateChkYn } from '@/api/coching/comm/notification';
import { getMyFavoriteList } from '@/api/coching/member/favorite';
import { getUserRecentInfoList } from '@/api/coching/member/recentInfo';

import SearchBar from './SearchBar.vue';
import MobileMenuModal from '@/components/modal/MobileMenuModal.vue';

const DEFAULT_NAVINFO = appConfig.DEFAULT_NAVINFO;

const LOCALE_IMG_MAIN = require("@/assets/images/ic-lang-wh-md.svg");
const LOCALE_IMG_NO_MAIN = require("@/assets/images/ic-lang-md.svg");

export default {
  name: 'BasicHeader',
  mixins: [ernsUtils],
  components: { 
    SearchBar,
    MobileMenuModal   
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
      CODES: {
        CH012: [],
      },
      toggle: {
        userMenu: false,
        userNoti: false,
        userHeart: false,
      },

      navInfo:{...DEFAULT_NAVINFO},
      fMeta:{
        header    : {...DEFAULT_NAVINFO.meta.header},
      },

      notiData :{
        list: [],
        newCnt: 0,
      },

      rawFavoriteData :{
        list: [],
      },
      rawRecentData :{
        list: [],
      },

      rawTabType: 'favorite',
    }
  },
  created(){
    
  },
  beforeDestroy(){
    window.removeEventListener("click", this.handleClickNotiOutside);
  },
  async mounted() {
    const _vm = this;
    
    await _vm.loadCodes();
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

    async loadCodes() {
        const _vm = this;

        const codeRes = await getCodes({ grpCode: "CH012", etc5: _vm.$i18n.locale });
        const { resultCode, resultFailMessage, resultData } = codeRes;

        _vm.CODES.CH012 = [...resultData.list];
    },

    //gnb 메뉴 클릭
    onclickGnbMenu(){
      const _vm = this;
      _vm.$store.dispatch('coching/setIsSmallGnb', _vm.$store.state.coching.isSmallGnb ? false : true);
    },

    //로그인
    onClickLogin(){
      const _vm = this;
      // console.debug(_vm.$route);
      _vm.ermPushPage({
        name:'coching-login',
        query: { retRoute: _vm.$route.fullPath }
      });
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

    //회원가입 이동
    onDoJoin(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-user-type'});
    },

    //홈이동
    onClickHome(){
      const _vm = this;
      _vm.ermReplacePage({name:'coching-main'});
    },

    //마이페이지
    onClickMyPage(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-mypage-myWrite'});
    },

    //게시판
    onClickBoard(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-rawSourcing-board-list'});
    },

    //원료
    onClickSearchRaw(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-search-raws'});
    },

    //성분
    onClickSearchIngd(){
      const _vm = this;
      _vm.ermPushPage({name:'coching-search-ingredients'});
    },

    onClickMenu() {
      const _vm = this;

      _vm.$refs.mobileMenuModal.open({
        isLoggedIn : _vm.isLoggedIn
      });
    },

    onClickWish() {
      const _vm = this;
      _vm.ermPushPage({name:'coching-mypage-wish'});
    },

    getNotiTitle(item) {
      const _vm = this;
      const matchingCode = _vm.CODES.CH012.find(code => code.etc1 === item.refCode);
      return matchingCode ? matchingCode.codeName : "알림이 도착했습니다."; 
    },

    async loadNoti(){
      const _vm = this;

      _vm.loading(true);
        try {
            const params = {
              page: 1,
              rowSize: 10
            };
            const res = await getNotiList(params);
            const resData = res.resultData;

            //데이터 바인딩
            const dataMap = _vm.notiData;
            dataMap.list = resData.list;
            dataMap.newCnt = resData.newCnt;
        } catch (err) {
            _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
            _vm.loading(false);
        }
    },

    async loadRaw(){
      const _vm = this;
      _vm.loading(true);
        try {

          const params = {
            refCode : 'raw',
            page: 1,
            rowSize: 5
          };

          //찜한 원료
          const favoriteRes = await getMyFavoriteList(params);
          _vm.rawFavoriteData.list = favoriteRes?.resultData?.list || [];

          //최근 본 원료
          const recentRes = await getUserRecentInfoList(params);
          _vm.rawRecentData.list = recentRes?.resultData?.list || [];

        } catch (err) {
            _vm.defaultApiErrorHandler(err); //기본에러처리
        } finally {
            _vm.loading(false);
        }
    },

    onClickRaw(item) {
      const _vm = this;

      const tabType = _vm.rawTabType;
      var rawDetailSeq = '';
      if(tabType === 'favorite'){
        rawDetailSeq = item.refSeq;
      } else if(tabType === 'recent'){
        rawDetailSeq = item.refSeq1;
      }
      _vm.onClickCloseUserHeart();
      _vm.ermPushPage({
        name:'coching-raw-detail', 
        query : {
          rawSeq : item.rawSeq,
          rawDetailSeq : rawDetailSeq
        }
      });

    },

    onClickRawList() {
      const _vm = this;
      const tabType = _vm.rawTabType;
      if(tabType === 'favorite'){
        _vm.onClickCloseUserHeart();
        _vm.ermPushPage({name:'coching-mypage-wish'});
      } else if(tabType === 'recent'){
        _vm.onClickCloseUserHeart();
        _vm.ermPushPage({name:'coching-mypage-recentView'});
      }
    },

    //사용자 하트 토글
    onClickToggleUserHeart() {
      const _vm = this;

      _vm.toggle.userHeart = !_vm.toggle.userHeart;
      if (_vm.toggle.userHeart) {
        _vm.$nextTick(() => {          
          window.addEventListener("click", _vm.handleClickHeartOutside);
        });
      } else {
        window.removeEventListener("click", _vm.handleClickHeartOutside);
      }      
    },

    //사용자 하트트 닫기
    onClickCloseUserHeart() {
      const _vm = this;

      _vm.toggle.userHeart = false;
      window.removeEventListener("click", _vm.handleClickHeartOutside);
    },
    
    async onClickNoti(item){
      const _vm = this;

      if(item.chkYn === 'N'){
        await updateChkYn({notiSeq: item.notiSeq, chkYn: 'Y'});
      }
      _vm.onClickCloseUserNoti();
      _vm.eumGoNotiRouter(item);
    },

    //사용자 하트창 밖에 영역 클릭 감지
    handleClickHeartOutside(event) {
      const _vm = this;
      if (_vm.$refs.heartContainer && _vm.$refs.heartContainer.contains(event.target)) {
        return;
      }

      if (_vm.$refs.userHeartLayer && _vm.$refs.userHeartLayer.contains(event.target)) {
        return;
      }

      _vm.toggle.userHeart = false;
      window.removeEventListener("click", _vm.handleClickHeartOutside);
    },

    //사용자 알림 토글
    onClickToggleUserNoti() {
      const _vm = this;

      _vm.toggle.userNoti = !_vm.toggle.userNoti;
      if (_vm.toggle.userNoti) {
        _vm.$nextTick(() => {          
          window.addEventListener("click", _vm.handleClickNotiOutside);
        });
      } else {
        window.removeEventListener("click", _vm.handleClickNotiOutside);
      }      
    },

    //사용자 알림 닫기
    onClickCloseUserNoti() {
      const _vm = this;

      _vm.toggle.userNoti = false;
      window.removeEventListener("click", _vm.handleClickNotiOutside);
    },

    //사용자 알림창 밖에 영역 클릭 감지
    handleClickNotiOutside(event) {
      const _vm = this;
      if (_vm.$refs.userNotiContainer && _vm.$refs.userNotiContainer.contains(event.target)) {
        return;
      }

      if (_vm.$refs.userNotiLayer && _vm.$refs.userNotiLayer.contains(event.target)) {
        return;
      }

      _vm.toggle.userNoti = false;
      window.removeEventListener("click", _vm.handleClickNotiOutside);
    },

    async onClickPartner(){
      const _vm = this;

      _vm.loading(true);
      try {

        // 새 창으로 URL 열기
        const partnerBaseUrlData = _vm.eumPartnerBaseUrlData;
        const baseUrl = partnerBaseUrlData.baseUrl ? partnerBaseUrlData.baseUrl : 'https://partner.cochingprt.co.kr';
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

      if(_vm.isLoggedIn){
        await _vm.loadNoti();
        await _vm.loadRaw();
      }
    },

    docReady(){
      const _vm = this;

    },
  }
}
</script>

<style lang="scss" scoped>
.like-recently-list-content .raw-none {
  font-size: 0.875rem;
  text-align: center;
  min-height: 6.25rem;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--color--gray-999);
}
</style>

<style lang="scss">
</style>
