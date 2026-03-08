const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

import { BANNER_INFO, BANNER_MODE } from '@/constants/banner';

const DEF_BANNER_MODE_INFO = [
  { mode: BANNER_MODE.LIST,
    name: '목록', addBreadcrumb: false, last: true,
    props: (route) => {
      const page = 
        route.params.page ||
        route.query.page || 1;
      return {
        bannerMstCd: route.meta.bannerMstCd,
        bannerMode: BANNER_MODE.LIST,
        page,
      };
    },
  },  
  { mode: BANNER_MODE.WRITE,
    name: '등록', addBreadcrumb: true , last: false,
    props: (route) => {
      return {
        bannerMstCd: route.meta.bannerMstCd,
        bannerMode: BANNER_MODE.WRITE,
        bannerSeq: 0,
      };
    },
  },
  { mode: BANNER_MODE.EDIT,
    name: '수정', addBreadcrumb: true , last: false,
    props: (route) => {      
      const bannerSeq = 
        route.params.bannerSeq ||
        route.query.bannerSeq || "0";
      return {
        bannerMstCd: route.meta.bannerMstCd,
        bannerMode: BANNER_MODE.EDIT,
        bannerSeq,
      };
    },
  },
];

const makeRouteList = function(routeInfos, component, bannerModeInfoArr, menuInfoArr, addMeta){
  const retRoutes = [];

  for(const menuInfo of menuInfoArr){
    const locale = menuInfo.locale;
    const menuName = menuInfo.name;

    for(const bannerModeInfo of bannerModeInfoArr){
      const rPath = routeInfos.pathTempleate
        .replace('${locale}', locale)
        .replace('${mode}', bannerModeInfo.mode);

      const rName = routeInfos.nameTemplate
        .replace('${locale}', locale)
        .replace('${mode}', bannerModeInfo.mode);

      const rMetaBreadcrumb = [];
      rMetaBreadcrumb.push(
        { 
          text: menuName,
          active: bannerModeInfo.last ? true : false,
        },
      );
      if(!bannerModeInfo.last) {
        rMetaBreadcrumb.push(
          { 
            text: bannerModeInfo.name,
            active: true,
          },
        );
      }
  
      const addRoute = {
        path: rPath,
        name: rName,
        component: () => component,
        props: bannerModeInfo.props,
        meta:{
          breadcrumb: rMetaBreadcrumb,
          ...addMeta,
          bannerMstCd: menuInfo.id,
          locale: menuInfo.locale
        }
      };

      retRoutes.push(addRoute);
    }
  }

  return retRoutes;
};

//메인화면 상단
const routeList_userMainTopBanner = makeRouteList(
  {
    pathTempleate: '/userMainTopBanner/${locale}/${mode}',
    nameTemplate: 'coching-bo-userMainTop-${locale}-banner-${mode}',
  },
  import('@/views/coching-bo/banner/UserMainTopBanner.vue'),
  DEF_BANNER_MODE_INFO,
  [
    {locale:'ko', ...BANNER_INFO.BANNER_0001,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

//광고 배너
const routeList_userAdBanner = makeRouteList(
  {
    pathTempleate: '/userAdBanner/${locale}/${mode}',
    nameTemplate: 'coching-bo-userAd-${locale}-banner-${mode}',
  },
  import('@/views/coching-bo/banner/UserAdBanner.vue'),
  DEF_BANNER_MODE_INFO,
  [
    {locale:'ko', ...BANNER_INFO.BANNER_0002,},
  ],
  { //meta
    userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
  }
);

// console.info(routeList_userMainTopBanner);

export default [
  ...routeList_userMainTopBanner,
  ...routeList_userAdBanner,
]
