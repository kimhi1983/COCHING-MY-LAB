const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/search/recommend/keyword`,
    name: 'coching-bo-search-recommend-keyword',
    component: () => import('@/views/coching-bo/search/RecommendKeyword.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: '추천 검색어 관리',
          active: true,
        },
      ],
    },
  }, 
  {
    path: `/search/coching/ingredient`,
    name: 'coching-bo-search-choching-ingredient',
    component: () => import('@/views/coching-bo/search/CochingIngredient.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: 'ES 성분사전(코칭)',
          active: true,
        },
      ],
    },
  }, 
  {
    path: `/search/hwahae/products`,
    name: 'coching-bo-search-hwahae-products',
    component: () => import('@/views/coching-bo/search/HwahaeProducts.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: 'ES 상품(화해)',
          active: true,
        },
      ],
    },
  }, 
  {
    path: `/search/coching/raws`,
    name: 'coching-bo-search-choching-raws',
    component: () => import('@/views/coching-bo/search/CochingRaws.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: 'ES 원료(코칭)',
          active: true,
        },
      ],
    },
  },
  {
    path: `/search/coching/raws/manager`,
    name: 'coching-bo-search-choching-raws-es-manager',
    component: () => import('@/views/coching-bo/search/CochingEsRawsManager.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: 'ES 원료 관리',
          active: true,
        },
      ],
    },
  },
  {
    path: `/search/coching/tv`,
    name: 'coching-bo-search-choching-tv',
    component: () => import('@/views/coching-bo/search/CochingTv.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: 'ES 코칭TV',
          active: true,
        },
      ],
    },
  },
  {
    path: `/search/coching/tv/manager`,
    name: 'coching-bo-search-choching-tv-es-manager',
    component: () => import('@/views/coching-bo/search/CochingEsTvManager.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '검색 관리',
      breadcrumb: [
        {
          text: '검색 관리',
        },
        {
          text: 'ES 코칭TV 관리',
          active: true,
        },
      ],
    },
  },
]
