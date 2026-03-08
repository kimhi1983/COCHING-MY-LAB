//Layout
import BasicLayout from '@/layouts/BasicLayout';
import { i18n } from '@/utils/i18n';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;
import { BOARD_MODE } from '@/constants/board';

const routeMeta_faq = {
  requireLogin : true,
  mainNavId : 5,
  wrapClass : [],
  menuType: 'FAQ',
  pageTitle :{
    "ko" : "자주 묻는 질문",
    "en" : "E자주 묻는 질문"
  },
  breadcrumb: [
    { text: '홈'},
    {
      text: '가이드',
      active: true,
    },
  ],
  header : {
    show:true,
  },
};

export default [
  //FAQ
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'board/faq',
        name: 'coching-faq-board-list',
        component: () => import('@/views/coching/board/FaqMain.vue'),
        props: (route) => {
          const page = 
            route.params.page ||
            route.query.page || 1;
          return {
            boardMode: BOARD_MODE.LIST,
            page,
          };
        },
        meta: routeMeta_faq,
      },
      {
        path: 'board/faq/view',
        name: 'coching-faq-board-view',
        component: () => import('@/views/coching/board/FaqMain.vue'),
        props: (route) => {
          const boardSeq = 
            route.params.boardSeq ||
            route.query.boardSeq || "0";
          return {
            boardMode: BOARD_MODE.VIEW,
            boardSeq,
          };
        },
        meta: routeMeta_faq,
      },
      {
        path: 'board/faq/write',
        name: 'coching-faq-board-write',
        component: () => import('@/views/coching/board/FaqMain.vue'),
        props: (route) => {
          return {
            boardMode: BOARD_MODE.WRITE,
            boardSeq: 0,
          };
        },
        meta: routeMeta_faq,
      },
    ],
  },  
]

