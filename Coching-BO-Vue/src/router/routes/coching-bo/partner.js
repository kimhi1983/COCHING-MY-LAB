const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/partner/main`,
    name: 'coching-bo-partner-main',
    component: () => import('@/views/coching-bo/partner/Main'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '원료사 정보',
      breadcrumb: [
        {
          text: '원료사정보',
        },
        {
          text: '원료사 정보',
          active: true,
        },
      ],
    },
  },
  {
    path: `/partner/editForm`,
    name: 'coching-bo-partner-editForm',
    component: () => import('@/views/coching-bo/partner/EditForm'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '원료사 정보',
      breadcrumb: [
        {
          text: '원료사 정보',
        },
        {
          text: '계정 생성',
          active: true,
        },
      ],
    },
  },
]
