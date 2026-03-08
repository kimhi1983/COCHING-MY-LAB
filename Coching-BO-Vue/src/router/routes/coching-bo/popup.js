const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

import { POPUP_INFO, POPUP_MODE } from '@/constants/popup';

const DEF_POPUP_MODE_INFO = [
  { mode: POPUP_MODE.LIST,
    name: '목록', addBreadcrumb: false, last: true,
    props: (route) => {
      const page = 
        route.params.page ||
        route.query.page || 1;
      return {
        popupMstCd: route.meta.popupMstCd,
        popupMode: POPUP_MODE.LIST,
        page,
      };
    },
  },  
  { mode: POPUP_MODE.WRITE,
    name: '등록', addBreadcrumb: true , last: false,
    props: (route) => {
      return {
        popupMstCd: route.meta.popupMstCd,
        popupMode: POPUP_MODE.WRITE,
        popupSeq: 0,
      };
    },
  },
  { mode: POPUP_MODE.EDIT,
    name: '수정', addBreadcrumb: true , last: false,
    props: (route) => {      
      const popupSeq = 
        route.params.popupSeq ||
        route.query.popupSeq || "0";
      return {
        popupMstCd: route.meta.popupMstCd,
        popupMode: POPUP_MODE.EDIT,
        popupSeq,
      };
    },
  },
];

const makeRouteList = function(routeInfos, component, popupModeInfoArr, menuInfoArr, addMeta){
  const retRoutes = [];

  for(const menuInfo of menuInfoArr){
    const locale = menuInfo.locale;
    const menuName = menuInfo.name;

    for(const popupModeInfo of popupModeInfoArr){
      const rPath = routeInfos.pathTempleate
        .replace('${locale}', locale)
        .replace('${mode}', popupModeInfo.mode);

      const rName = routeInfos.nameTemplate
        .replace('${locale}', locale)
        .replace('${mode}', popupModeInfo.mode);

      const rMetaBreadcrumb = [];
      rMetaBreadcrumb.push(
        { 
          text: menuName,
          active: popupModeInfo.last ? true : false,
        },
      );
      if(!popupModeInfo.last) {
        rMetaBreadcrumb.push(
          { 
            text: popupModeInfo.name,
            active: true,
          },
        );
      }
  
      const addRoute = {
        path: rPath,
        name: rName,
        component: () => component,
        props: popupModeInfo.props,
        meta:{
          breadcrumb: rMetaBreadcrumb,
          ...addMeta,
          popupMstCd: menuInfo.id,
          locale: menuInfo.locale
        }
      };

      retRoutes.push(addRoute);
    }
  }

  return retRoutes;
};

//사용자 팝업
const routeList_userMainPopup = makeRouteList(
  {
    pathTempleate: '/userMainPopup/${locale}/${mode}',
    nameTemplate: 'coching-bo-userMain-${locale}-popup-${mode}',
  },
  import('@/views/coching-bo/popup/UserMainPopup.vue'),
  DEF_POPUP_MODE_INFO,
  [
    {locale:'ko', ...POPUP_INFO.POPUP_0001,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//파트너 팝업
const routeList_partnerMainPopup = makeRouteList(
  {
    pathTempleate: '/partnerMainPopup/${locale}/${mode}',
    nameTemplate: 'coching-bo-partnerMain-${locale}-popup-${mode}',
  },
  import('@/views/coching-bo/popup/PartnerMainPopup.vue'),
  DEF_POPUP_MODE_INFO,
  [
    {locale:'ko', ...POPUP_INFO.POPUP_0002,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

// console.info(routeList_userMainPopup);

export default [
  ...routeList_userMainPopup,
  ...routeList_partnerMainPopup,
  
]
