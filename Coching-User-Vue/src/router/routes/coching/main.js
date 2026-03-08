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
          menuType: 'HOME',
          pageTitle :{
            "ko" : "COCHING",
            "en" : "COCHING"
          },
          breadcrumb: [
            {
              text: 'COCHING',
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

