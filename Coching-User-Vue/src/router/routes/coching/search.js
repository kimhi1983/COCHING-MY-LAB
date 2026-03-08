//Layout
import BasicLayout from '@/layouts/BasicLayout';
import EmptyLayout from '@/layouts/EmptyLayout.vue';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'search',
        name: 'coching-search-main',
        component: () => import('@/views/coching/search/SearchMain.vue'),
        props: (route) => {
          const keyword = route.params.keyword || route.query.keyword;
          let initViewType = route.params.initViewType || route.query.initViewType;
          const ingdIdsVal = route.params.ingdIds || route.query.ingdIds;          
          const ingdIds = typeof ingdIdsVal === 'string'
            ? [...ingdIdsVal.split(',')] 
            : Array.isArray(ingdIdsVal) 
              ? ingdIdsVal
              : [];
          const hintField = route.params.hf || route.query.hf || '';
          if(!initViewType && hintField.length > 0 ){
            if( hintField.indexOf('4') == 0){
              initViewType = 'ctv';
            } else if( hintField.indexOf('2') == 0){
              initViewType = 'prod';
            } else if( hintField.indexOf('1') == 0){
              initViewType = 'raws';
            } else if( hintField.indexOf('0') == 0){
              initViewType = 'ingd';
            }
          }
          const emoParam = route.params.emo || route.query.emo;
          const exactMatchOnly = emoParam === 'true' || emoParam === true || emoParam === 1 || emoParam === '1';

          const rsParam = route.params.rs || route.query.rs;
          const isRawSearch = rsParam === 'true' || rsParam === true || rsParam === 1 || rsParam === '1';
          return {
            keyword,
            ingdIds,
            initViewType,
            hintField,
            exactMatchOnly,
            isRawSearch
          };
        },
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "검색",
            "en" : "Searching"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '검색 결과',
              active: true,
            },            
          ],
          header : {
            show:true,
          },
          menuType : "TV"
        },
      },

      {
        path: 'search/raws',
        name: 'coching-search-raws',
        component: () => import('@/views/coching/search/SearchRaws.vue'),
        props: (route) => {
          const keyword = route.params.keyword || route.query.keyword;
          const ingdIdsVal = route.params.ingdIds || route.query.ingdIds;
          const ingdIds = typeof ingdIdsVal === 'string'
            ? [...ingdIdsVal.split(',')] 
            : Array.isArray(ingdIdsVal) 
              ? ingdIdsVal
              : [];
          const hintField = route.params.hf || route.query.hf;
          const emoParam = route.params.emo || route.query.emo;
          const exactMatchOnly = emoParam === 'true' || emoParam === true || emoParam === 1 || emoParam === '1';
          return {
            keyword,
            ingdIds,
            hintField,
            exactMatchOnly
          };
        },
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "검색",
            "en" : "Searching"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '원료 검색',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },

      {
        path: 'search/ingredients',
        name: 'coching-search-ingredients',
        component: () => import('@/views/coching/search/SearchIngredients.vue'),
        props: (route) => {
          const keyword = route.params.keyword || route.query.keyword;
          const hintField = route.params.hf || route.query.hf;
          const emoParam = route.params.emo || route.query.emo;
          const exactMatchOnly = emoParam === 'true' || emoParam === true || emoParam === 1 || emoParam === '1';
          return {
            keyword,
            hintField,
            exactMatchOnly
          };
        },
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "검색",
            "en" : "Searching"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '성분 결과',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },

      {
        path: 'search/rawInfo',
        name: 'coching-raw-detail',
        component: () => import('@/views/coching/search/RawDetail.vue'),
        props: (route) => {
          const rawSeq = route.params.rawSeq || route.query.rawSeq;
          const rawDetailSeq = route.params.rawDetailSeq || route.query.rawDetailSeq;          
          return {
            rawDetailSeq,
            rawSeq
          };
        },
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "원료 상세",
            "en" : "Raw Detail"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '검색'},
            { text: '원료 상세',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },

      {
        path: 'search/ingredientList',
        name: 'coching-ingredients',
        component: () => import('@/views/coching/search/Ingredients.vue'),
        props: (route) => {
          const productId = route.params.productId || route.query.productId;
          const rawSeq = route.params.rawSeq || route.query.rawSeq;
          const ingdId = route.params.ingdId || route.query.ingdId;
          const emoParam = route.params.emo || route.query.emo;
          const exactMatchOnly = emoParam === 'true' || emoParam === true || emoParam === 1 || emoParam === '1';
          return {
            productId,
            rawSeq,
            ingdId,
            exactMatchOnly
          };
        },
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "전성분",
            "en" : "Ingredients"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '검색'},
            { text: '전성분',
              active: true,
            }            
          ],
          header : {
            show:true,
          }
        },
      },

    ]
  },
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: EmptyLayout,
    children: [
      {
        path: 'search/preview',
        alias: ['search/preview:param*'],
        name: 'coching-raw-preview',
        component: () => import('@/views/coching/search/RawDetailPreview.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "원료 상세 미리보기",
            "en" : "Raw Detail"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '검색'},
            { text: '원료 상세',
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

