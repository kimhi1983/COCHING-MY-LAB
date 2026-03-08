const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/sys/event/main`,
    name: 'coching-bo-sys-event-main',
    component: () => import('@/views/coching-bo/sys/SysEvent.vue'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '이벤트 설정',
      breadcrumb: [
        {
          text: '시스템관리',
        },
        {
          text: '이벤트 설정',
          active: true,
        },
      ],
    },
  },
  {
    path: `/sys/event/add`,
    name: 'coching-bo-sys-event-add',
    component: () => import('@/views/coching-bo/sys/SysEventForm.vue'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '이벤트 등록',
      breadcrumb: [
        {
          text: '시스템관리',
        },
        {
          text: '이벤트 설정',
          active: true,
        },
      ],
    },
  },
  {
    path: `/sys/event/edit`,
    name: 'coching-bo-sys-event-edit',
    component: () => import('@/views/coching-bo/sys/SysEventForm.vue'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '이벤트 수정',
      breadcrumb: [
        {
          text: '시스템관리',
        },
        {
          text: '이벤트 설정',
          active: true,
        },
      ],
    },
  },
 
  {
    path: `/sys/account/main`,
    name: 'coching-bo-sys-account-main',
    component: () => import('@/views/coching-bo/sys/SysAccount.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '계정관리',
      breadcrumb: [
        {text: '시스템관리',},
        {
          text: '계정관리',
          active: true,
        },
      ],
    },
  },  

  {
    path: `/sys/account/add`,
    name: 'coching-bo-sys-account-add',
    component: () => import('@/views/coching-bo/sys/SysAccountForm.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '계정관리',
      breadcrumb: [
        {text: '시스템관리',},
        {
          text: '계정관리',
          active: true,
        },
      ],
    },
  },  

  {
    path: `/sys/account/edit`,
    name: 'coching-bo-sys-account-edit',
    component: () => import('@/views/coching-bo/sys/SysAccountForm.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '계정관리',
      breadcrumb: [
        {text: '시스템관리',},
        {
          text: '계정관리',
          active: true,
        },
      ],
    },
  },  
  

  {
    path: `/sys/holiday/main`,
    name: 'coching-bo-sys-holiday-main',
    component: () => import('@/views/coching-bo/sys/SysHolidayMain.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '공휴일관리',
      breadcrumb: [
        {text: '시스템관리',},
        {
          text: '공휴일관리',
          active: true,
        },
      ],
    },
  },  
]
