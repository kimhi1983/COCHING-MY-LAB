const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/app/version/mgr`,
    name: 'coching-bo-app-version-main',
    component: () => import('@/views/coching-bo/ver/VerList.vue'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: 'APP 버전 관리',
      breadcrumb: [
        {
          text: 'APP 관리',
        },
        {
          text: 'APP 버전 관리',
          active: true,
        },
      ],
    },
  },

]
