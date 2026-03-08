const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/dashboard`,
    name: 'coching-bo-dashboard',
    component: () => import('@/views/coching-bo/dashboard/DashBoard.vue'),
    meta: {
      
    },    
  },  
]
