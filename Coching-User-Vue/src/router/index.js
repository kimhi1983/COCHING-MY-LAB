import Vue from 'vue'
import VueRouter from 'vue-router'

// Routes
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils'

import ernscoching from './routes/coching';
import pages from './routes/pages';
import {join} from 'path';
import { i18n } from '@/utils/i18n';

import store from '@/store';
import ability from '@/libs/acl/ability';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;
const VUE_APP_PUBLIC_PATH = process.env.VUE_APP_PUBLIC_PATH;

Vue.use(VueRouter)

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }

    if(to.hash) {
      return { selector: to.hash };
    }

    return { x: 0, y: 0 }
  },

  routes: [
    { path: '/', redirect: getHomeRouteForLoggedInUser() },
    { path: VUE_APP_BASE_ROUTER_PATH, redirect: getHomeRouteForLoggedInUser() },
    { path: VUE_APP_BASE_ROUTER_PATH+'/loginTest', redirect: { name: 'coching-login-account' }},

    ...ernscoching,
    ...pages,
    {
      path: '*',
      redirect: 'error-404',
    },
  ],
})

const isRequireLogin = function(to){
  const requireLogin = false || to.meta.requireLogin;
  return requireLogin;
}

router.beforeEach((to, from, next) => {
  //로그인 체크
  const isLoggedIn = isUserLoggedIn();

  //console.debug(to);
  if (!isLoggedIn && isRequireLogin(to)) {
    
    // Redirect to login if not logged in
    return next({ 
      name: 'coching-login',
      query: { retRoute: to.fullPath }
    });
  }

  // Redirect if logged in
  if (to.meta.redirectIfLoggedIn && isLoggedIn) {
    const userData = getUserData();
    next(getHomeRouteForLoggedInUser(null));
  }

  if (to.path.includes(';jsessionid=')) {
    // ;jsessionid= 제거
    const cleanPath = to.path.replace(/;jsessionid=[^/]+/, '');
    return next({ path: cleanPath, query: to.query, replace: true });
  } else {
    return next();
  }
});


router.afterEach((to, from)=>{

  // to: 이동할 대상 Route 객체
  // from: 현재 라우트에서 벗어날 Route 객체
  //console.log(`We've moved from ${from.path} to ${to.path}`);

  // 스토어의 액션을 디스패치합니다.
  store.dispatch('coching/setLogRouteChange', {
    from: from
    , to: to
  });
  //console.log(store.state.coching.logRouteChange);
  
  let pageTitle = "";

  if(i18n.locale === 'ko'){
    pageTitle = to.meta.pageTitle.ko;
  }else{
    pageTitle = to.meta.pageTitle.en;
  }

  const title = to.meta.pageTitle === undefined ? 'COCHING' : pageTitle;
  
  Vue.nextTick(() => {
    document.title = title;

    docReadyScript();
  });
});

//동일페이지 라우팅 처리(검색페이지)
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location, onResolve, onReject) {
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject);
  }
  return originalPush.call(this, location).catch(err => {
    if (err.name !== 'NavigationDuplicated') throw err;
  });
};

export default router


const docReadyScript = function(){
  docReady_niceScroller();
}

const docReady_niceScroller = function(){
  // 스크롤시 nicescroll 업데이트
  $(window).off("scroll.cochingNiceScrollBanner").on( "scroll.cochingNiceScrollBanner", function () {
    $(".banner-wrap").getNiceScroll().resize();
  });

  const isMobile = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);
  if (!isMobile) {
    // 데스크톱에서만 nicescroll 적용
    $(".scroller").niceScroll();

    // 리사이즈 시 스크롤 재조정
    $(window).off("resize.cochingNiceScroller").on("resize.cochingNiceScroller", function () {
      $(".scroller").getNiceScroll().resize();
    });

    // 스크롤 이벤트에서 다른 스크롤 방지
    $(".scroller").off("scroll.cochingNiceScroller").on("scroll.cochingNiceScroller", function (e) {
      e.stopPropagation(); // 다른 스크롤 이벤트가 영향을 미치지 않도록 방지
    });
  } else {
    // 모바일에서는 기본 스크롤 사용
    $(".scroller").css("overflow", "auto");
  }

  var $banner = $(".banner-wrap");
  var $footer = $("footer .inner");
  var $section = $(".section-content");
  var $header = $("header .inner"); // 헤더 선택
  var $stickyBox = $(".link-box-sticky"); // link-box-sticky 선택
  var headerHeight = $header.outerHeight(); // 헤더 높이 계산
  var bannerHeight = $banner.outerHeight();

  // // 스크롤 이벤트 처리
  // $(window).off("scroll.cochingSticky").on("scroll.cochingSticky", function () {
  //   var windowScrollTop = $(window).scrollTop();
  //   if(!$section || $section.length == 0){ 
  //     return; 
  //   }

  //   var sectionTop = $section.offset().top;
  //   var footerTop = $footer.offset().top;

  //   // link-box-sticky에 sticky 클래스가 있는지 확인
  //   var stickyBoxHeight = 0;
  //   if ($stickyBox.length) {
  //     if ($stickyBox.hasClass("sticky")) {
  //       stickyBoxHeight = $stickyBox.outerHeight(); // sticky 클래스가 있을 때의 높이 계산
  //     } else {
  //       stickyBoxHeight = $stickyBox.outerHeight(); // sticky 클래스가 없을 때 높이 계산
  //     }
  //   }

  //   // link-box-sticky가 있으면 헤더 + link-box-sticky + 16px, 없으면 헤더 + 16px
  //   var referenceHeight = headerHeight + stickyBoxHeight;
  //   var bannerTopPosition = windowScrollTop - sectionTop + referenceHeight + 16;

  //   // 배너가 footer에 닿기 전까지 따라다니게 설정
  //   if (windowScrollTop + bannerHeight + referenceHeight + 16 < footerTop - 96) {
  //     if (windowScrollTop > sectionTop - referenceHeight - 16) {
  //       // 배너가 섹션 시작 후 스크롤될 때
  //       $banner.css({
  //         position: "absolute",
  //         top: bannerTopPosition + "px", // 헤더 또는 link-box-sticky에서 16px 떨어진 위치 유지
  //       });
  //     } else {
  //       // 스크롤이 위로 올라가면 초기 위치로 돌아가도록 설정
  //       $banner.css({
  //         position: "absolute",
  //         top: 0,
  //       });
  //     }
  //   } else {
  //     // 배너가 footer에서 80px+16=96 위에서 멈추게 설정
  //     $banner.css({
  //       position: "absolute",
  //       top: footerTop - sectionTop - bannerHeight - 96 + "px", // 96px 위에서 멈춤
  //     });
  //   }
  // });
};