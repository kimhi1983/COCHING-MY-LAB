//Layout
import MainLayout from '@/layouts/MainLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: MainLayout,
    children: [
      {
        path: 'autoLogin',
        alias: ['autoLogin:param*'],
        name: 'coching-auto-login',
        props: true,
        component: () => import('@/views/coching/login/AutoLogin.vue'),
        meta: {
          wrapClass : [],
          pageTitle: {
            "ko" : "자동 로그인",
            "en" : "Auto Login"
          },
          breadcrumb: [
            { text: '홈', },
            {
              text: '로그인',
              active: true,
            },
          ],
          bottomNav : {
            show:false
          },
          isSupportLeftBack : false
        },
      },
      {
        path: 'login',
        name: 'coching-login',
        props: (route) => {
          const loginErrorCode = route.params.loginErrorCode || route.query.loginErrorCode;
          const loginFailMessage = route.params.loginFailMessage || route.query.loginFailMessage;
          const retRoute = route.params.retRoute || route.query.retRoute;
    
          return {
            loginErrorCode
            , loginFailMessage
            , retRoute
          };
        },
        component: () => import('@/views/coching/login/Login.vue'),        
        meta: {
          requireLogin : false,
          wrapClass : [],
          pageTitle :{
            "ko" : "로그인",
            "en" : "Log in"
          },
          breadcrumb: [
            { text: '홈', },
            {
              text: '로그인',
              active: true,
            },
          ],
          header : {
            show:true,
          }
        },
      },

      {
        path: 'snsLogin',
        alias: ['snsLogin:param*'],
        name: 'coching-emulator-login',
        props: true,
        component: () => import('@/views/coching/login/SnsLogin.vue'),
        meta: {
          wrapClass : [],
          pageTitle: {
            "ko" : "에뮬레이터 로그인",
            "en" : "Emulator Login"
          },
          breadcrumb: [
            { text: '홈', },
            {
              text: '로그인',
              active: true,
            },
          ],
          bottomNav : {
            show:false
          },
          isSupportLeftBack : false
        },
      },
    ]
  },  
]

