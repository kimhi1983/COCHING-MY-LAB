//Layout
import MainLayout from '@/layouts/MainLayout';
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: MainLayout,
    children: [
      {
        path: 'main',
        name: 'coching-main',
        component: () => import('@/views/coching/Main.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 0,
          wrapClass : [],
          pageTitle :{
            "ko" : "홈",
            "en" : "Home"
          },
          breadcrumb: [
            {
              text: '홈',
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

