//Layout
import BasicLayout from '@/layouts/BasicLayout';
import MembType from '@/views/coching/join/MembType.vue';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'userType',
        name: 'coching-user-type',
        component: () => import('@/views/coching/join/MembType.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          pageTitle :{
            "ko" : "회원가입",
            "en" : "Sign-up"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '회원가입',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'userJoin',
        name: 'coching-user-join',
        component: () => import('@/views/coching/join/UserJoin.vue'),
        props: (route) => ({ membType: route.params.membType }),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          pageTitle :{
            "ko" : "회원가입",
            "en" : "Sign-up"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '회원가입',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
    ]
  }
]

