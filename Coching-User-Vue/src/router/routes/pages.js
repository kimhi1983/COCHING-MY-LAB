const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: '/error-404',
    name: 'error-404',
    component: () => import('@/views/error/Error404.vue'),
    meta: {
      layout: 'full',
      resource: 'Auth',
      action: 'read',
    },
  },
  {
    path: `${VUE_APP_BASE_ROUTER_PATH}/error-400`,
    name: 'error-400',
    component: () => import('@/views/error/Error400.vue'),
    meta: {
      layout: 'full',
      resource: 'Auth',
      action: 'read',
    },
  },
  {
    path: `${VUE_APP_BASE_ROUTER_PATH}/error-410`,
    name: 'error-410',
    component: () => import('@/views/error/Error410.vue'),
    meta: {
      layout: 'full',
      resource: 'Auth',
      action: 'read',
    },
  }, 
]
