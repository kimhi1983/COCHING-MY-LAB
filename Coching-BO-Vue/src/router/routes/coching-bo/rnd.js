const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: `/rnd/ai/prsc/demo1`,
    name: 'coching-bo-rnd-lab-list',
    component: () => import('@/views/coching-bo/rnd/LabList.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: 'AI 처방 V1',
      breadcrumb: [
        {
          text: '코칭 실험실',
        },
        {
          text: 'AI 처방 V1',
          active: true,
        },
      ],
    },
  },
  {
    path: `/rnd/ai/prsc/demo1/detail`,
    name: 'coching-bo-rnd-ai-prsc-demo1-detail',
    component: () => import('@/views/coching-bo/rnd/AiPrscDemoForm.vue'),
    props: (route) => {
      const labMstSeq = 
        route.params.labMstSeq ||
        route.query.labMstSeq || "0"; 
      const aiLabMstSeq = 
        route.params.aiLabMstSeq ||
        route.query.aiLabMstSeq || "0";
      return {
        labMstSeq,
        aiLabMstSeq,
      };
    },
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: 'AI 처방 V1',
      breadcrumb: [
        {
          text: '코칭 실험실',
        },
        {
          text: 'AI 처방 V1',
          active: true,
        },
      ],
    },
  },
  {
    path: `/rnd/dev/iconList`,
    name: 'coching-bo-rnd-dev-iconList',
    component: () => import('@/views/coching-bo/rnd/IconListView.vue'),
    meta: {
      userGroups:['UGC_BO_01000'],
      pageTitle: '아이콘 리스트',
      breadcrumb: [
        {
          text: '코칭 실험실',
        },
        {
          text: '아이콘 리스트',
          active: true,
        },
      ],
    },
  },
]
