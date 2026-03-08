const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/raw/main`,
    name: 'coching-bo-raw-main',
    component: () => import('@/views/coching-bo/raw/Main'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '원료리스트',
      breadcrumb: [
        {
          text: '원료관리',
        },
        {
          text: '원료리스트',
          active: true,
        },
      ],
    },
  },
  {
    path: `/raw/type/main`,
    name: 'coching-bo-raw-type-main',
    component: () => import('@/views/coching-bo/rawType/Main.vue'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '성분구분 관리',
      breadcrumb: [
        {
          text: '원료관리',
        },
        {
          text: '성분구분 관리',
          active: true,
        },
      ],
    },
  },
  {
    path: `/raw/detail`,
    name: 'coching-bo-raw-detail',
    component: () => import('@/views/coching-bo/raw/Detail'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '원료 상세',
      breadcrumb: [
        {
          text: '원료 관리',
        },
        {
          text: '원료 상세',
          active: true,
        },
      ],
    },
  },
  {
    path: `/raw/type/editForm`,
    name: 'coching-bo-raw-type-editForm',
    component: () => import('@/views/coching-bo/rawType/EditForm'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '성분 구분 등록',
      breadcrumb: [
        {
          text: '원료 관리',
        },
        {
          text: '성분 구분 등록',
          active: true,
        },
      ],
    },
  },
]
