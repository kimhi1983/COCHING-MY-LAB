import Vue from 'vue'
import VueRouter from 'vue-router'

// Routes
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils'

import coching from './routes/coching';
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

    ...coching,
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

  if(to.meta.userAuth){
    const userType = getUserData().userType;
    const userAuth = to.meta.userAuth;
    const hasAuth = userAuth.some(auth => auth === userType);
    if(!hasAuth){
      next(getHomeRouteForLoggedInUser(null));
    }
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
