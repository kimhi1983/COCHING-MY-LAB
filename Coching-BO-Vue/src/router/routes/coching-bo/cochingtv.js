const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/cochingtv/main`,
    name: 'coching-bo-cochingtv-main',
    component: () => import('@/views/coching-bo/cochingtv/Main.vue'),
    meta: {
      pageTitle: '코칭TV 리스트',
      breadcrumb: [
        {
          text: '코칭TV 관리',
        },
        {
          text: '코칭TV 리스트',
          active: true,
        },
      ],
    },
  },
]