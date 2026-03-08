const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/contract/main`,
    name: 'coching-bo-contract-mgr-main',
    component: () => import('@/views/coching-bo/contract/Main'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '계약 목록',
      breadcrumb: [
        {
          text: '계약 관리',
        },
        {
          text: '계약 목록',
          active: true,
        },
      ],
    },
  },
  {
    path: `/contract/detail`,
    name: 'coching-bo-contract-mgr-detail',
    props: (route) => ({ cntrcSeq: Number.parseInt(route.query.cntrcSeq, 10) }),
    component: () => import('@/views/coching-bo/contract/Detail'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '계약 상세',
      breadcrumb: [
        {
          text: '계약 목록',
        },
        {
          text: '계약 상세',
          active: true,
        },
      ],
    },
  },
  {
    path: `/contract/settle`,
    name: 'coching-bo-contract-settle',
    component: () => import('@/views/coching-bo/contract/Settle'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '이체 이력',
      breadcrumb: [
        {
          text: '계약 관리',
        },
        {
          text: '이체 이력',
          active: true,
        },
      ],
    },
  },  
]
