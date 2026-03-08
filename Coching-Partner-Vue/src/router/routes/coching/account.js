//Layout
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'account',
        name: 'coching-account-main',
        component: () => import('@/views/coching/account/AccountMain.vue'),
        meta: {
          requireLogin : true,
          userAuth: ['002'],
          mainNavId : 1,
          wrapClass : [],
          menuType: 'ACCOUNT',
          pageTitle :{
            "ko" : "계정관리",
            "en" : "My Page"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '계정',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'account/register',
        name: 'coching-account-register',
        component: () => import('@/views/coching/account/AccountEdit.vue'),
        meta: {
          requireLogin : true,
          userAuth: ['002'],
          mainNavId : 1,
          wrapClass : [],
          menuType: 'ACCOUNT',
          pageTitle :{
            "ko" : "계정 등록",
            "en" : "My Page"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '계정',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'account/edit',
        name: 'coching-account-edit',
        component: () => import('@/views/coching/account/AccountEdit.vue'),
        props: (route) => ({ membSeq: Number.parseInt(route.query.membSeq, 10) }),
        meta: {
          requireLogin : true,
          userAuth: ['002'],
          mainNavId : 1,
          wrapClass : [],
          menuType: 'ACCOUNT',
          pageTitle :{
            "ko" : "계정 수정",
            "en" : "Edit Account"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '계정',
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

