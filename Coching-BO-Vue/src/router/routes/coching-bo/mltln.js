const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/mltln/main`,
    name: 'coching-bo-mltln-main',
    component: () => import('@/views/coching-bo/mltln/Mltln'),
    meta: {
      userGroups:['UGC_BO_01000','UGC_BO_03000','UGC_BO_04000'],
      pageTitle: '다국어 관리',
      breadcrumb: [
        {
          text: '다국어 관리',
        },
        {
          text: '다국어 관리',
          active: true,
        },
      ],
    },
  },
  {
    path: `/mltln/mltlnAdd`,
    name: 'coching-bo-mltln-mltlnAdd',
    component: () => import('@/views/coching-bo/mltln/MltlnForm'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '다국어 관리',
      breadcrumb: [
        {
          text: '다국어 관리',
        },
        {
          text: '등록',
          active: true,
        },
      ],
    },
  },
  {
    path: `/mltln/mltlnEdit`,
    name: 'coching-bo-mltln-mltlnEdit',
    props: (route) => ({ code: route.query.code }),
    component: () => import('@/views/coching-bo/mltln/MltlnForm'),
    meta: {
      userGroups: ['UGC_BO_01000', 'UGC_BO_03000', 'UGC_BO_04000'],
      pageTitle: '다국어 관리',
      breadcrumb: [
        {
          text: '다국어 관리',
        },
        {
          text: '수정',
          active: true,
        },
      ],
    },
  },
]
