//Layout
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'partner/foreignPartner',
        name: 'coching-partner-foreign-list',
        component: () => import('@/views/coching/partner/ForeignPartner.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          menuType: 'FOREIGNPTN',
          pageTitle :{
            "ko" : "해외 원료사",
            "en" : "Foreign Partner List"
          },
          breadcrumb: [
            { text: '홈',},
            {
              text: '해외 원료사 목록',
              active: true,
            },
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'partner/foreignPartner/view',
        name: 'coching-partner-foreign-detail',
        component: () => import('@/views/coching/partner/ForeignPartnerView.vue'),
        props: (route) => {
          const ptnSeq = 
            route.params.ptnSeq ||
            route.query.ptnSeq || "0";
          return { ptnSeq };
        },
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          menuType: 'FOREIGNPTN',
          pageTitle :{
            "ko" : "해외 원료사",
            "en" : "Foreign Partner List"
          },
          breadcrumb: [
            { text: '홈',},
            {
              text: '해외 원료사 상세',
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
]
