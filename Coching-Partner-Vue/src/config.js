
const setDarkModeInHtml = function(isDarkMode){
  if(isDarkMode){
    document.documentElement.classList.add('dark-mode');
  }else{
    document.documentElement.classList.remove('dark-mode');
  }
};

const storageDarkMode = 'erns.coching.pc.darkMode';

const getDarkModeInLocalStorage = function(){
	const isDarkMode = JSON.parse(localStorage.getItem(storageDarkMode) || "false");
	return isDarkMode;
};

export default {    
  storagePushTokenKey : 'erns.coching.pc.pushInfoData',
  storagePartnerInfoKey : 'erns.coching.pc.partnerInfoData',
  storageIsVisitedInfoKeyName: 'erns.coching.pc.firstVisitData',
  storageScdJoinInfo: 'erns.coching.pc.scdJoinInfo',

  storageLocale: 'erns.coching.pc.locale',
  storageUserBaseUrl: 'erns.coching.pc.userBaseUrlData',

  storageDarkMode : storageDarkMode,

  DEFAULT_NAVINFO :{
    curPath : "",
    meta : {
      pageTitle : "",
      header : {
        show:true,
        isMain:false,
        emptyTitle:false
      },
    }
  },

  setDarkModeInHtml,
  getDarkModeInLocalStorage,
};

