import useJwt from '@/auth/jwt/useJwt'
import appConfig from '@/config'
/**
 * Return if user is logged in
 * This is completely up to you and how you want to store the token in your frontend application
 * e.g. If you are using cookies to store the application please update this function
 */
// eslint-disable-next-line arrow-body-style
export const isUserLoggedIn = () => {
  return localStorage.getItem(useJwt.jwtConfig.storageUserDataKeyName) 
    && localStorage.getItem(useJwt.jwtConfig.storageTokenKeyName)
}

export const getUserData = () => JSON.parse(localStorage.getItem(useJwt.jwtConfig.storageUserDataKeyName))

// 로그인시 이동할 기본페이지
export const getHomeRouteForLoggedInUser = () => {
  //return { name: 'auth-login' }
  return { name: 'coching-raw-main' }; //home
}


//사용자 권한으로 부터 Ability 설정
export function defineAbilitiesForUserAuth(userAuthList) {
  //권한있는 메뉴
  const hasAuthMenuMap = new Map();
  userAuthList.forEach(item=>{
    hasAuthMenuMap.set(item.menuId, {menuId : item.menuId, menuName: item.menuNameKor});
  });

  const retAbilityObj = [
    //{ action:'manage', subject:'all'}
    { action:'read', subject:'commonMenu'},
  ];

  //메뉴권한 설정
  const hasAuthMenuIds = Array.from(hasAuthMenuMap.values());
  hasAuthMenuIds.forEach(menu=>{
    retAbilityObj.push({
      action: 'read', subject: menu.menuId
    });
    //can('menu', menu.menuId);
  });

  userAuthList.forEach(authItem=>{
    retAbilityObj.push({
      action: 'func', subject: authItem.funcId
    });
    //can('func', authItem.funcId);
  });

  //임시 권한 설정
  retAbilityObj.push({action:'write', subject: 'BD_RAW_001'});
  retAbilityObj.push({action:'write', subject: 'BD_RAW_002'});

  return retAbilityObj;
}