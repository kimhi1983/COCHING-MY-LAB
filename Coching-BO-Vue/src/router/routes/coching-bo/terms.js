const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/terms/join`,
    name: 'coching-bo-terms-join',
    component: () => import('@/views/coching-bo/terms/TermsMain.vue'),
    props:{
      termsType:"TERMS"
      , termsCodes:['001'] //TODO : 약관코드 매핑필요 => T_CODE_INF 테이블 etc1 기준
    },
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '가입 약관',
      breadcrumb: [
        {
          text: '약관관리',
        },
        {
          text: '가입 약관',
          active: true,
        },
      ],
    },
  },

  {
    path: `/terms/privacy`,
    name: 'coching-bo-terms-privacy',
    component: () => import('@/views/coching-bo/terms/TermsMain.vue'),
    props:{
      termsType:"TERMS"
      , termsCodes:['002']
    },
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '개인정보처리방침',
      breadcrumb: [
        {
          text: '약관관리',
        },
        {
          text: '개인정보처리방침',
          active: true,
        },
      ],
    },
  },
]