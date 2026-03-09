export default [
  {
    header: 'BackOffice',
  },
  {
    title: '원료사 관리',
    icon: 'TruckIcon',
    children: [
      {
        title: '원료사 정보',
        route: { name: 'coching-bo-partner-main'},
      },
    ],
  },
  {
    title: '회원 관리',
    icon: 'UsersIcon',
    children: [
      {
        title: '회원 정보',
        route: { name: 'coching-bo-member-main'},
      },
      // {
      //   title: 'Test 회원에뮬레이터',
      //   route: { name: 'coching-bo-memb-emulator-main'},
      // },  
    ],
  },
  {
    title: '원료관리',
    icon: 'CodesandboxIcon',
    children: [
      {
        title: '원료관리',
        route: { name: 'coching-bo-raw-main'},
      },
      {
        title: '성분구분관리',
        route: { name: 'coching-bo-raw-type-main'},
      }  
    ],
  },
  {
    title: '코칭TV 관리',
    icon: 'PlayCircleIcon',
    children: [      
      {
        title: '코칭TV 리스트',
        route: { name: 'coching-bo-cochingtv-main'},
      },      
    ],
  },
  {
    title: '게시판 관리',
    icon: 'MessageSquareIcon',
    children: [      
      {
        title: 'FAQ[ko]',
        route: { name: 'coching-bo-faq-ko-board-list'},
      },
      {
        title: '공지사항[ko]',
        route: { name: 'coching-bo-notice-ko-board-list'},
      },
      {
        title: '원료소싱[ko]',
        route: { name: 'coching-bo-rawSourcing-ko-board-list'},
      },
      {
        title: '1:1문의[ko]',
        route: { name: 'coching-bo-inqr-ko-board-list'},
      },   
      {
        title: 'Weekly News[ko]',
        route: { name: 'coching-bo-weeklyNews-ko-board-list'},
      },
      
      {
        title: 'FAQ[en]',
        route: { name: 'coching-bo-faq-en-board-list'},
      },
      {
        title: '공지사항[en]',
        route: { name: 'coching-bo-notice-en-board-list'},
      },
      {
        title: '원료소싱[en]',
        route: { name: 'coching-bo-rawSourcing-en-board-list'},
      },
      {
        title: '1:1문의[en]',
        route: { name: 'coching-bo-inqr-en-board-list'},
      },  
      {
        title: 'Weekly News[en]',
        route: { name: 'coching-bo-weeklyNews-en-board-list'},
      }, 
    ],
  },
  {
    title: '배너 관리',
    icon: 'MinusSquareIcon',
    children: [
      {
        title: '[User] 메인 상단 배너',
        route: { name: 'coching-bo-userMainTop-ko-banner-list'},
      },
      {
        title: '[User] 광고 배너',
        route: { name: 'coching-bo-userAd-ko-banner-list'},
      },    
      // {
      //   title: 'AG-GRID',
      //   route: { name: 'coching-bo-banner-web-top-ag'},
      // }, 
    ],
  },
  {
    title: '팝업 관리',
    icon: 'CopyIcon',
    children: [
      {
        title: '사용자 메인 팝업',
        route: { name: 'coching-bo-userMain-ko-popup-list'},
      },
      {
        title: '파트너 메인 팝업',
        route: { name: 'coching-bo-partnerMain-ko-popup-list'},
      },
    ],
  },
  {
    title: '검색 관리',
    icon: 'SearchIcon',
    children: [
      {
        title: '추천 검색어 관리',
        route: { name: 'coching-bo-search-recommend-keyword'},
      },
      {
        title: 'ES 성분(코칭)',
        route: { name: 'coching-bo-search-choching-ingredient'},
      },
      {
        title: 'ES 상품(화해)',
        route: { name: 'coching-bo-search-hwahae-products'},
      },
      {
        title: 'ES 원료(코칭)',
        route: { name: 'coching-bo-search-choching-raws'},
      },      
      {
        title: 'ES 코칭TV(코칭)',
        route: { name: 'coching-bo-search-choching-tv'},
      },      
      {
        title: 'ES원료(관리자)',
        route: { name: 'coching-bo-search-choching-raws-es-manager'},
      },
      {
        title: 'ES코칭TV(관리자)',
        route: { name: 'coching-bo-search-choching-tv-es-manager'},
      },      
    ],
  },
  {
    title: '약관 관리',
    icon: 'AlignLeftIcon',
    children: [
      {
        title: '가입 약관',
        route: { name: 'coching-bo-terms-join' },
      },
      {
        title: '개인정보처리방침',
        route: { name: 'coching-bo-terms-privacy' },
      },
    ],
  },
  {
    title: '다국어 관리',
    icon: 'BookOpenIcon',
    children: [      
      {
        title: '다국어 관리',
        route: { name: 'coching-bo-mltln-main'},
      },      
    ],
  },
  {
    title: '시스템설정',
    icon: 'SettingsIcon',
    children: [
      {
        title: '계정관리',
        route: { name: 'coching-bo-sys-account-main'},
      },
      {
        title: '코드관리',
        route: { name: 'coching-bo-system-code-main'},
      },    
      {
        title: '게시판마스터',
        route: { name: 'coching-bo-system-boardMaster'},
      },
      {
        title: '배너마스터',
        route: { name: 'coching-bo-system-bannerMaster-main'},
      },
      {
        title: '팝업마스터',
        route: { name: 'coching-bo-system-popupMaster-main'},
      },
      {
        title: '사용자로그',
        route: { name: 'coching-bo-system-log-main'},
      },
      {
        title: '환경변수 ',
        route: { name: 'coching-bo-system-sysProp-main'},
      },     
    ],
  },
  {
    title: '코칭 실험실',
    icon: 'CpuIcon',
    children: [
      {
        title: 'MY LAB',
        icon: 'FlaskIcon',
        route: { name: 'coching-bo-rnd-mylab'},
      },
      {
        title: 'AI 처방 V1',
        route: { name: 'coching-bo-rnd-lab-list'},
      }, 
      {
        title: '아이콘 리스트',
        route: { name: 'coching-bo-rnd-dev-iconList'},
      },      
    ],
  },
]
