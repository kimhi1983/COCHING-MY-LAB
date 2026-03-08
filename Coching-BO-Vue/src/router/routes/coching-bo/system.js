const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/system/code/main`,
    name: 'coching-bo-system-code-main',
    component: () => import('@/views/coching-bo/system/CodeMasterList.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '코드 관리',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '코드 관리',
          active: true,
        },
      ],
    },
  },
  {
    path: `/system/code/detail`,
    name: 'coching-bo-system-code-detail',
    component: () => import('@/views/coching-bo/system/CodeList.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '코드 관리',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '코드 관리',
        },
        {
          text: '코드 목록',
          active: true,
        },
      ],
    },
  },

  {
    path: `/system/boardMaster`,
    name: 'coching-bo-system-boardMaster',
    component: () => import('@/views/coching-bo/system/SysBoardMaster.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '게시판마스터 관리',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '게시판마스터 관리',
          active: true,
        },
      ],
    },
  },
  {
    path: `/system/boardMasterEdit`,
    name: 'coching-bo-system-boardMasterEdit',
    component: () => import('@/views/coching-bo/system/SysBoardMasterForm.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '게시판마스터 관리',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '게시판마스터 관리',
          to: `/bo/system/boardMaster`
        },
        {
          text: '수정',
          active: true,
        },
      ],
    },
  },
  {
    path: `/system/boardMasterAdd`,
    name: 'coching-bo-system-boardMasterAdd',
    component: () => import('@/views/coching-bo/system/SysBoardMasterForm.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '게시판마스터 관리',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '게시판마스터 관리',
          to: `/bo/system/boardMaster`
        },
        {
          text: '등록',
          active: true,
        },
      ],
    },
  },

  {
    path: `/system/bannerMaster/main`,
    name: 'coching-bo-system-bannerMaster-main',
    component: () => import('@/views/coching-bo/system/SysBannerMaster.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '배너마스터',
      breadcrumb: [
        { text: '시스템설정',},
        {
          text: '배너마스터',
          active: true,
        },
      ],
    },
  },

  {
    path: `/system/popupMaster/main`,
    name: 'coching-bo-system-popupMaster-main',
    component: () => import('@/views/coching-bo/system/SysPopupMaster.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '팝업마스터',
      breadcrumb: [
        { text: '시스템설정',},
        {
          text: '팝업마스터',
          active: true,
        },
      ],
    },
  },
  
  {
    path: `/system/log/main`,
    name: 'coching-bo-system-log-main',
    component: () => import('@/views/coching-bo/system/UserLog.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '사용자로그',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '사용자로그',
          active: true,
        },
      ],
    },
  },

  {
    path: `/system/zipcode/main`,
    name: 'coching-bo-system-zipcode-main',
    component: () => import('@/views/coching-bo/system/SimpleZipCode.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '우편번호',
      breadcrumb: [
        {
          text: '시스템설정',
        },
        {
          text: '우편번호',
          active: true,
        },
      ],
    },
  },

  {
    path: `/system/sysProp/main`,
    name: 'coching-bo-system-sysProp-main',
    component: () => import('@/views/coching-bo/system/SysProp.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '환경변수',
      breadcrumb: [
        {
          text: '환경변수',
        },
        {
          text: '환경변수 설정',
          active: true,
        },
      ],
    },
  },
]
