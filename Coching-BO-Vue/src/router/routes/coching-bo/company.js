const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
 
  {
    path: `/company/membEm`,
    name: 'coching-bo-memb-emulator-main',
    component: () => import('@/views/coching-bo/company/EUserList'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '기업 정보',
      breadcrumb: [
        {
          text: '기업정보',
        },
        {
          text: 'Test 회원에뮬레이터',
          active: true,
        },
      ],
    },
  },
]
