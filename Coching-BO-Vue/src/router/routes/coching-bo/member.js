const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/member/main`,
    name: 'coching-bo-member-main',
    component: () => import('@/views/coching-bo/member/Main'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '회원관리',
      breadcrumb: [
        {
          text: '회원관리',
        },
        {
          text: '회원정보',
          active: true,
        },
      ],
    },
  },
  {
    path: `/member/editForm`,
    name: 'coching-bo-member-editForm',
    component: () => import('@/views/coching-bo/member/EditForm'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '회원 수정',
      breadcrumb: [
        {
          text: '회원관리',
        },
        { 
          text: '회원 수정',
          active: true,
        },
      ],
    },
  },
  {
    path: `/member/membEm`,
    name: 'coching-bo-memb-emulator-main',
    component: () => import('@/views/coching-bo/member/EUserList'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '원료사 정보',
      breadcrumb: [
        {
          text: '원료사정보',
        },
        {
          text: 'Test 회원에뮬레이터',
          active: true,
        },
      ],
    },
  },
]
