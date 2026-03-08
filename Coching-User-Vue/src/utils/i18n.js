import Vue from 'vue';
import VueI18n from 'vue-i18n';
import axios from 'axios';

import koData from '@/i18n/ko.json';
import enData from '@/i18n/en.json';
import appConfig from '@/config';

Vue.use(VueI18n);

const apiBase = process.env.VUE_APP_API_BASE_URL;

const loadLocaleMessages = async (locale) => {
  //그냥 화면에 있는 json 리턴
  //return loadDefaulLocale(locale);

  //서버에서 요청
  try {
    const reqUrl = `${apiBase}/api/mltln/i18n/${locale}.api`;
    const response = await axios.get(reqUrl);

    //console.debug(response);
    return response.data;

  } catch (error) {
    console.error(`Failed to load ${locale} translations`, error);

    return loadDefaulLocale(locale);
  }
};

//기본언어팩 로드
const loadDefaulLocale = function(locale){
  switch(locale){
    case 'en':
      return enData;
    case 'ko':
    default:
      return koData;
  }
};

const loadDefaultLocale = function(){
  const localeInfo = localStorage.getItem(appConfig.storageLocale);
  let retLocale = localeInfo ? localeInfo : 'ko';

  switch(retLocale){
    case 'en':      
    case 'ko':
      break;
    default:
      retLocale = 'ko';
  }

  return retLocale;
}

const defaultLocale = ""+loadDefaultLocale();
const i18n = new VueI18n({
  locale: defaultLocale, // 기본 언어 설정
  messages: {} // 초기 메시지는 비워둡니다.
});

// 초기 언어를 로딩하거나 다른 초기화 작업을 수행
(async () => {
  i18n.setLocaleMessage(defaultLocale, await loadLocaleMessages(defaultLocale));
  // 다른 언어를 사전 로드하거나 필요에 따라 나중에 로드할 수도 있습니다.
})();

export {
  i18n,
  defaultLocale,
  loadLocaleMessages
};