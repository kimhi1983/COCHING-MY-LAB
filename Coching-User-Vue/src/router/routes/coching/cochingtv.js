//Layout
import BasicLayout from '@/layouts/BasicLayout';
import MainLayout from '@/layouts/MainLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'cochingtv',
        name: 'coching-tv-detail',
        component: () => import('@/views/coching/cochingtv/CochingTvDetail.vue'),
        props: (route) => {
          const tvSeq = route.params.tvSeq || route.query.tvSeq || "0";
          return {
            tvSeq,
          };
        },
        meta: {
          requireLogin : false,
          mainNavId : 0, //?
          wrapClass : [],
          menuType: 'TV_DETAIL',
          pageTitle :{
            "ko" : "코칭 TV",
            "en" : "Coching TV"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '코칭 TV',
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