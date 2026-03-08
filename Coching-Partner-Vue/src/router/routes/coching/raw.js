//Layout
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'raw/main',
        name: 'coching-raw-main',
        component: () => import('@/views/coching/raw/RawMain.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          menuType: 'MANAGE',
          pageTitle :{
            "ko" : "원료 등록 및 관리",
            "en" : "Manage Raw"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '원료',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'raw/garbage',
        name: 'coching-garbage-main',
        component: () => import('@/views/coching/raw/GarbageMain.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 2,
          wrapClass : [],
          menuType: 'GARBAGE',
          pageTitle :{
            "ko" : "휴지통",
            "en" : "Garbage"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '원료',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'raw/design',
        name: 'coching-design-main',
        component: () => import('@/views/coching/raw/DesignMain.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 3,
          wrapClass : [],
          menuType: 'DESIGN',
          pageTitle :{
            "ko" : "담당자 지정",
            "en" : "Design Manager"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '원료',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'raw/regist',
        name: 'coching-raw-regist',
        component: () => import('@/views/coching/raw/RawEdit.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 7,
          wrapClass : ['bg-gary'],
          menuType: 'MANAGE',
          pageTitle :{
            "ko" : "원료 등록",
            "en" : "Register Raw"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '원료',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'raw/edit',
        name: 'coching-raw-edit',
        component: () => import('@/views/coching/raw/RawEdit.vue'),
        props: (route) => ({ rawSeq: Number.parseInt(route.query.rawSeq, 10), 
          managerSeq: Number.parseInt(route.query.managerSeq, 10) }),
        meta: {
          requireLogin : true,
          mainNavId : 7,
          wrapClass : ['bg-gary'],
          menuType: 'MANAGE',
          pageTitle :{
            "ko" : "원료 수정",
            "en" : "Modify Raw"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '원료',
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

