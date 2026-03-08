const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

import { BOARD_INFO, BOARD_MODE } from '@/constants/board';


const DEF_BOARD_MODE_INFO = [
  { mode: BOARD_MODE.LIST,
    name: '목록', addBreadcrumb: false, last: true,
    props: (route) => {
      const page = 
        route.params.page ||
        route.query.page || 1;
      return {
        boardMstId: route.meta.boardMstId,
        boardMode: BOARD_MODE.LIST,
        page,
      };
    },
  },
  { mode: BOARD_MODE.VIEW,
    name: '상세', addBreadcrumb: true , last: false,
    props: (route) => {
      const boardSeq = 
        route.params.boardSeq ||
        route.query.boardSeq || "0";
      return {
        boardMstId: route.meta.boardMstId,
        boardMode: BOARD_MODE.VIEW,
        boardSeq,
      };
    },
  },
  { mode: BOARD_MODE.WRITE,
    name: '등록', addBreadcrumb: true , last: false,
    props: (route) => {
      return {
        boardMstId: route.meta.boardMstId,
        boardMode: BOARD_MODE.WRITE,
        boardSeq: 0,
      };
    },
  },
  { mode: BOARD_MODE.EDIT,
    name: '수정', addBreadcrumb: true , last: false,
    props: (route) => {      
      const boardSeq = 
        route.params.boardSeq ||
        route.query.boardSeq || "0";
      return {
        boardMstId: route.meta.boardMstId,
        boardMode: BOARD_MODE.EDIT,
        boardSeq,
      };
    },
  },
];

const makeRouteList = function(routeInfos, component, boardModeInfoArr, menuInfoArr, addMeta){
  const retRoutes = [];

  for(const menuInfo of menuInfoArr){
    const locale = menuInfo.locale;
    const menuName = menuInfo.name;

    for(const boardModeInfo of boardModeInfoArr){
      const rPath = routeInfos.pathTempleate
        .replace('${locale}', locale)
        .replace('${mode}', boardModeInfo.mode);

      const rName = routeInfos.nameTemplate
        .replace('${locale}', locale)
        .replace('${mode}', boardModeInfo.mode);

      const rMetaBreadcrumb = [];
      rMetaBreadcrumb.push(
        { 
          text: menuName,
          active: boardModeInfo.last ? true : false,
        },
      );
      if(!boardModeInfo.last) {
        rMetaBreadcrumb.push(
          { 
            text: boardModeInfo.name,
            active: true,
          },
        );
      }
  
      const addRoute = {
        path: rPath,
        name: rName,
        component: () => component,
        props: boardModeInfo.props,
        meta:{
          breadcrumb: rMetaBreadcrumb,
          ...addMeta,
          boardMstId: menuInfo.id,
          locale: menuInfo.locale
        }
      };

      retRoutes.push(addRoute);
    }
  }

  return retRoutes;
};

//FAQ
const routeList_board_faq = makeRouteList(
  {
    pathTempleate: '/faq/${locale}/${mode}',
    nameTemplate: 'coching-bo-faq-${locale}-board-${mode}',
  },
  import('@/views/coching-bo/board/Faq.vue'),
  DEF_BOARD_MODE_INFO,
  [
    {locale:'ko', ...BOARD_INFO.FAQ_KO,},
    {locale:'en', ...BOARD_INFO.FAQ_EN,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//공지사항
const routeList_board_notice = makeRouteList(
  {
    pathTempleate: '/notice/${locale}/${mode}',
    nameTemplate: 'coching-bo-notice-${locale}-board-${mode}',
  },
  import('@/views/coching-bo/board/Notice.vue'),
  DEF_BOARD_MODE_INFO,
  [
    {locale:'ko', ...BOARD_INFO.NOTICE_KO,},
    {locale:'en', ...BOARD_INFO.NOTICE_EN,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//원료소싱
const routeList_board_rawSourcing = makeRouteList(
  {
    pathTempleate: '/rawSourcing/${locale}/${mode}',
    nameTemplate: 'coching-bo-rawSourcing-${locale}-board-${mode}',
  },
  import('@/views/coching-bo/board/RawSourcing.vue'),
  DEF_BOARD_MODE_INFO,
  [
    {locale:'ko', ...BOARD_INFO.RAW_SOURCING_KO,},
    {locale:'en', ...BOARD_INFO.RAW_SOURCING_EN,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//1:1문의
const routeList_board_inqr = makeRouteList(
  {
    pathTempleate: '/inqr/${locale}/${mode}',
    nameTemplate: 'coching-bo-inqr-${locale}-board-${mode}',
  },
  import('@/views/coching-bo/board/Inquiry.vue'),
  DEF_BOARD_MODE_INFO,
  [
    {locale:'ko', ...BOARD_INFO.INQR_KO,},
    {locale:'en', ...BOARD_INFO.INQR_EN,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//Weekly News
const routeList_board_weeklyNews = makeRouteList(
  {
    pathTempleate: '/weeklyNews/${locale}/${mode}',
    nameTemplate: 'coching-bo-weeklyNews-${locale}-board-${mode}',
  },
  import('@/views/coching-bo/board/WeeklyNews.vue'),
  DEF_BOARD_MODE_INFO,
  [
    {locale:'ko', ...BOARD_INFO.WEEKLYNEWS_KO,},
    {locale:'en', ...BOARD_INFO.WEEKLYNEWS_EN,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//console.info(routeList_board_faq);

export default [
  ...routeList_board_faq,
  ...routeList_board_notice,
  ...routeList_board_rawSourcing,
  ...routeList_board_inqr,
  ...routeList_board_weeklyNews,
  // //FAQ_KO
  // {
  //   path: `/faq/ko/list`,
  //   name: 'coching-bo-faq-ko-list',
  //   component: () => import('@/views/coching-bo/board/Faq.vue'),
  //   meta: {
  //     ...routeMeta_faq_ko,
  //     breadcrumb: [
  //       { text: BOARD_INFO.FAQ_KO.name,
  //         active: true,
  //       },
  //     ],
  //   },
  // },  
  // {
  //   path: '/faq/ko/view',
  //   name: 'coching-bo-faq-ko-edit',
  //   component: () => import('@/views/coching-bo/board/Faq.vue'),
  //   meta: {
  //     ...routeMeta_faq_ko,
  //     breadcrumb: [
  //       { text: BOARD_INFO.FAQ_KO.name,},
  //       {
  //         text: '상세',
  //         active: true,
  //       },
  //     ],
  //   },    
  // },
]
