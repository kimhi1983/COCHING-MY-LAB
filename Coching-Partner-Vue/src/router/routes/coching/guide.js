//Layout
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'guide',
        name: 'coching-guide-main',
        component: () => import('@/views/coching/guide/GuideMain.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 4,
          wrapClass : [],
          menuType: 'GUIDE',
          pageTitle :{
            "ko" : "원료 등록 가이드",
            "en" : "Faq"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '고객센터',
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

