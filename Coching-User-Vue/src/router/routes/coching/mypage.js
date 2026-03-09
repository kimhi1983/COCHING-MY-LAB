//Layout
import BasicLayout from '@/layouts/BasicLayout';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

export default [
  {
    path: VUE_APP_BASE_ROUTER_PATH,
    component: BasicLayout,
    children: [
      {
        path: 'notification',
        name: 'coching-mypage-notification',
        component: () => import('@/views/coching/mypage/Notification.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          menuType: 'NOTI',
          pageTitle :{
            "ko" : "알림",
            "en" : "NOTIFICATION"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'message',
        name: 'coching-mypage-message',
        component: () => import('@/views/coching/mypage/Message.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          menuType: 'MESSAGE',
          pageTitle :{
            "ko" : "쪽지함",
            "en" : "Message"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'wish',
        name: 'coching-mypage-wish',
        component: () => import('@/views/coching/mypage/Wish.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "찜한 원료",
            "en" : "Wish Raw"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'requestRaw',
        name: 'coching-mypage-requestRaw',
        component: () => import('@/views/coching/mypage/RequestRaw.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          menuType: 'REQUEST',
          pageTitle :{
            "ko" : "원료 요청",
            "en" : "Request Raw"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'requestRawDetail',
        name: 'coching-mypage-requestRaw-detail',
        component: () => import('@/views/coching/mypage/RequestRawDetail.vue'),
        props: (route) => ({ rawRequestSeq: Number.parseInt(route.query.rawRequestSeq, 10) }),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          menuType: 'REQUEST',
          pageTitle :{
            "ko" : "원료 요청 상세",
            "en" : "Request Raw Detail"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'requestRawReply',
        name: 'coching-mypage-requestRaw-reply',
        component: () => import('@/views/coching/mypage/RequestRawReply.vue'),
        props: (route) => ({ rawRequestSeq: Number.parseInt(route.query.rawRequestSeq, 10) }),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          menuType: 'REQUEST',
          pageTitle :{
            "ko" : "원료 요청 답장",
            "en" : "Request Raw Reply"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'recentView',
        name: 'coching-mypage-recentView',
        component: () => import('@/views/coching/mypage/RecentView.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "최근 본 원료",
            "en" : "Recent View Raw"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'myWrite',
        name: 'coching-mypage-myWrite',
        component: () => import('@/views/coching/mypage/MyWrite.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "내가 쓴 글",
            "en" : "My Write"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },            
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'myInfo',
        name: 'coching-mypage-myInfo',
        component: () => import('@/views/coching/mypage/MyInfo.vue'),
        meta: {
          requireLogin : true,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "개인정보",
            "en" : "My Info"
          },
          breadcrumb: [
            { text: '홈',},
            { text: '마이페이지',
              active: true,
            },
          ],
          header : {
            show:true,
          }
        },
      },
      {
        path: 'mylab',
        name: 'coching-mypage-mylab',
        component: () => import('@/views/coching/mylab/MyLab.vue'),
        meta: {
          requireLogin : false,
          mainNavId : 1,
          wrapClass : [],
          pageTitle :{
            "ko" : "MY LAB",
            "en" : "MY LAB"
          },
          breadcrumb: [
            { text: '홈',},
            { text: 'MY LAB',
              active: true,
            },
          ],
          header : {
            show:true,
          }
        },
      },
    ]
  }
]

