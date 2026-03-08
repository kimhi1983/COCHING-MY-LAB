//Layout
import EmptyLayout from '@/layouts/EmptyLayout.vue';
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: EmptyLayout,
    children: [
      {
        path: 'introduce',
        name: 'coching-introduce',
        component: () => import('@/views/coching/introduce/Introduce.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          pageTitle :{
            "ko" : "코칭 소개",
            "en" : "Coching Introduce"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '코칭 메뉴얼',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'userManual',
        name: 'coching-user-manual',
        component: () => import('@/views/coching/introduce/UserManual.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],          
          pageTitle :{
            "ko" : "사용자 메뉴얼",
            "en" : "User Manual"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '코칭 메뉴얼',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'partnerManual',
        name: 'coching-partner-manual',
        component: () => import('@/views/coching/introduce/PartnerManual.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          pageTitle :{
            "ko" : "파트너 메뉴얼",
            "en" : "Partner Manual"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '코칭 메뉴얼',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
    ]
  },
  {
      path: VUE_APP_BASE_ROUTER_PATH,
      component: BasicLayout,
      children: [
        {
          path: 'cochingUserManual',
          name: 'coching-user-manual-new',
          component: () => import('@/views/coching/introduce/UserManualNew.vue'),
          meta: {
            requireLogin : false,
            mainNavId : 0,
            wrapClass : [],
            menuType: 'MANUAL',
            pageTitle :{
              "ko" : "사용자 메뉴얼",
              "en" : "User Manual"
            },
            breadcrumb: [
              { text: '홈',},
              { text: '코칭 메뉴얼',
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

