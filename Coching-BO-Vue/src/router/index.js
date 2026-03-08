import Vue from 'vue'
import VueRouter from 'vue-router'

// Routes
import { canNavigate } from '@/libs/acl/routeProtection'
import { isUserLoggedIn, getUserData, getHomeRouteForLoggedInUser } from '@/auth/utils'
import pages from './routes/pages'

//Coching BackOffice
import cochingBo from './routes/coching-bo';
import {join} from 'path';
import store from '@/store';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;
const VUE_APP_PUBLIC_PATH = process.env.VUE_APP_PUBLIC_PATH;

Vue.use(VueRouter)

// `getHomeRouteForLoggedInUser`가 현재 경로와 동일한지 체크하여 리디렉트 방지
const homeRoute = getHomeRouteForLoggedInUser();
const isValidRedirect = (route) => route && route !== '/' && route !== VUE_APP_BASE_ROUTER_PATH;

const router = new VueRouter({
  mode: 'history',
  base: join(VUE_APP_PUBLIC_PATH, VUE_APP_BASE_ROUTER_PATH),
  scrollBehavior() {
    return { x: 0, y: 0 }
  },
  routes: [
    { path: '/', redirect: isValidRedirect(homeRoute) ? homeRoute : { name: 'auth-login' } },
    { path: VUE_APP_PUBLIC_PATH, redirect: isValidRedirect(homeRoute) ? homeRoute : { name: 'auth-login' } },
    
    //{ path: '/', redirect: "/coching/ernsCoching-bo" },
    //{ path: '/', redirect: { name: 'coching-bo-dashboard' } },

    ...cochingBo,

    ...pages,

    //{path: '*', redirect: `${VUE_APP_BASE_ROUTER_PATH}/error-404`,},
    { path: '*', redirect : "/error-404" },
  ],
});

// BO 관리자 메뉴 확인
const checkAuthForUserRole = function(to, role){
  const cRole = role || 'ROLE_GUEST';

  if("ROLE_ADMIN" == cRole){
    return true;
  }
  
  const resource = to.meta.resource || 'GUEST_MENU';
  if("ROLE_GUEST" == cRole && resource == "ADMIN_MENU"){
    return false;
  }

  return true;
}

router.beforeEach((to, from, next) => {
  const isLoggedIn = isUserLoggedIn()
  const userData = getUserData()

  // console.debug(to);
  // console.debug(from);

  if (!canNavigate(to)) {
    // Redirect to login if not logged in
    return next({ 
      name: 'coching-login',
      query: { retRoute: to.fullPath }
    })

    // If logged in => not authorized
    return next({ name: 'misc-not-authorized' })
  }

  if(isLoggedIn){
    //BO 유저 라우터 확인
    if(!checkAuthForUserRole(to, userData.role)){
      return next({ name: 'misc-not-authorized' });
    }
  }

  // Redirect if logged in
  if (to.meta.redirectIfLoggedIn && isLoggedIn) {    
    next(getHomeRouteForLoggedInUser(userData ? userData.role : null))
  }

  return next();
});

router.afterEach((to, from)=>{

  // to: 이동할 대상 Route 객체
  // from: 현재 라우트에서 벗어날 Route 객체
  //console.log(`We've moved from ${from.path} to ${to.path}`);

  // 스토어의 액션을 디스패치합니다.
  store.dispatch('erns/setLogRouteChange', {
    from: from
    , to: to
  });
  //console.log(store.state.erns.logRouteChange);
});

export default router

