//Layout
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;
import { BOARD_MODE } from '@/constants/board';

const routeMeta_rawSourcing = {
  requireLogin : false,
  mainNavId : 1,
  wrapClass : [],
  menuType: 'SOURCING',
  pageTitle :{
    "ko" : "원료소싱",
    "en" : "E원료소싱"
  },
  breadcrumb: [
    { text: '홈',},
    { text: '원료소싱',
      active: true,
    },            
  ],
  header : {
    show:true,
  },
};

const routeMeta_notice = {
  requireLogin : false,
  mainNavId : 1,
  wrapClass : [],
  menuType: 'NOTICE',
  pageTitle :{
    "ko" : "공지사항",
    "en" : "Notice"
  },
  breadcrumb: [
    { text: '홈',},
    { text: '공지사항',
      active: true,
    },            
  ],
  header : {
    show:true,
  },
};

const routeMeta_faq = {
  requireLogin : false,
  mainNavId : 1,
  wrapClass : [],
  menuType: 'FAQ',
  pageTitle :{
    "ko" : "FAQ",
    "en" : "FAQ"
  },
  breadcrumb: [
    { text: '홈',},
    { text: 'FAQ',
      active: true,
    },            
  ],
  header : {
    show:true,
  },
};

const routeMeta_inqr = {
  requireLogin : false,
  mainNavId : 1,
  wrapClass : [],
  menuType: 'INQR',
  pageTitle :{
    "ko" : "1:1 문의하기",
    "en" : "1:1 문의하기"
  },
  breadcrumb: [
    { text: '홈',},
    { text: '1:1 문의하기',
      active: true,
    },            
  ],
  header : {
    show:true,
  },
};

const routeMeta_weeklyNews = {
  requireLogin : false,
  mainNavId : 1,
  wrapClass : [],
  menuType: 'WEEKLYNEWS',
  pageTitle :{
    "ko" : "Weekly News",
    "en" : "Weekly News"
  },
  breadcrumb: [
    { text: '홈',},
    { text: 'Weekly News',
      active: true,
    },            
  ],
  header : {
    show:true,
  },
};

export default [
  //원료소싱
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'board/rawSourcing',
        name: 'coching-rawSourcing-board-list',
        component: () => import('@/views/coching/board/RawSourcing.vue'),
        props: (route) => {
          const page = 
            route.params.page ||
            route.query.page || 1;
          return {
            boardMode: BOARD_MODE.LIST,
            page,
          };
        },
        meta: routeMeta_rawSourcing,
      },
      {
        path: 'board/rawSourcing/view',
        name: 'coching-rawSourcing-board-view',
        component: () => import('@/views/coching/board/RawSourcing.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.VIEW,
            boardSeq,
          };
        },
        meta: routeMeta_rawSourcing,
      },
      {
        path: 'board/rawSourcing/write',
        name: 'coching-rawSourcing-board-write',
        component: () => import('@/views/coching/board/RawSourcing.vue'),
        props: (route) => {
          return {
            boardMode: BOARD_MODE.WRITE,
            boardSeq: 0,
          };
        },
        meta: {
          ...routeMeta_rawSourcing,
          requireLogin: true,
        },
      },
      {
        path: 'board/rawSourcing/edit',
        name: 'coching-rawSourcing-board-edit',
        component: () => import('@/views/coching/board/RawSourcing.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.EDIT,
            boardSeq,
          };
        },
        meta: {
          ...routeMeta_rawSourcing,
          requireLogin: true,
        },
      },
    ]
  },

  //공지사항
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [  
      {
        path: 'board/notice',
        name: 'coching-notice-board-list',
        component: () => import('@/views/coching/board/Notice.vue'),
        props: (route) => {
          const page = 
            route.params.page ||
            route.query.page || 1;
          return {
            boardMode: BOARD_MODE.LIST,
            page,
          };
        },
        meta: routeMeta_notice,
      },
      {
        path: 'board/notice/view',
        name: 'coching-notice-board-view',
        component: () => import('@/views/coching/board/Notice.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.VIEW,
            boardSeq,
          };
        },
        meta: routeMeta_notice,      
      },
    ]
  },

  //FAQ
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'board/faq',
        name: 'coching-faq-board-list',
        component: () => import('@/views/coching/board/Faq.vue'),
        props: (route) => {
          const page = 
            route.params.page ||
            route.query.page || 1;
          return {
            boardMode: BOARD_MODE.LIST,
            page,
          };
        },
        meta: routeMeta_faq
      },
      {
        path: 'board/faq/view',
        name: 'coching-faq-board-view',
        component: () => import('@/views/coching/board/Faq.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.VIEW,
            boardSeq,
          };
        },
        meta: routeMeta_faq
      },
    ]
  },

  //1:1 문의하기
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'board/inqr',
        name: 'coching-inqr-board-list',
        component: () => import('@/views/coching/board/Inquiry.vue'),
        props: (route) => {
          const page = 
            route.params.page ||
            route.query.page || 1;
          return {
            boardMode: BOARD_MODE.LIST,
            page,
          };
        },
        meta: routeMeta_inqr
      },
      {
        path: 'board/inqr/view',
        name: 'coching-inqr-board-view',
        component: () => import('@/views/coching/board/Inquiry.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.VIEW,
            boardSeq,
          };
        },
        meta: routeMeta_inqr
      },
      {
        path: 'board/inqr/write',
        name: 'coching-inqr-board-write',
        component: () => import('@/views/coching/board/Inquiry.vue'),
        props: (route) => {
          return {
            boardMode: BOARD_MODE.WRITE,
            boardSeq: 0,
          };
        },
        meta: {
          ...routeMeta_inqr,
          requireLogin: true,
        },
      },
    ]
  },

  //Weekly News
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'board/weeklyNews',
        name: 'coching-weeklyNews-board-list',
        component: () => import('@/views/coching/board/WeeklyNews.vue'),
        props: (route) => {
          const page = 
            route.params.page ||
            route.query.page || 1;
          return {
            boardMode: BOARD_MODE.LIST,
            page,
          };
        },
        meta: routeMeta_weeklyNews
      },
      {
        path: 'board/weeklyNews/view',
        name: 'coching-weeklyNews-board-view',
        component: () => import('@/views/coching/board/WeeklyNews.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.VIEW,
            boardSeq,
          };
        },
        meta: routeMeta_weeklyNews
      }
    ]
  }
]

