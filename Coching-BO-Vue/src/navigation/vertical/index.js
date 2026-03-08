/*

Array of object

Top level object can be:
1. Header
2. Group (Group can have navItems as children)
3. navItem

* Supported Options

/--- Header ---/

header

/--- nav Grp ---/

title
icon (if it's on top level)
tag
tagVariant
children

/--- nav Item ---/

icon (if it's on top level)
title
route: [route_obj/route_name] (I have to resolve name somehow from the route obj)
tag
tagVariant

*/

/* coching-backOffice */
import ernsCochingBackOfficeViews from './coching-bo';
import ernsCochingBackOfficeRouter from '@/router/routes/coching-bo';

//권한 있는 메뉴만 필터링 후 리턴
const filterBoMenu = (hasGroups)=>{
  const authRouter = ernsCochingBackOfficeRouter.filter(router=>{
    if(!router.meta){return false;}
    if(!router.meta.userGroups || router.meta.userGroups.length <= 0){return false;}

    return true;
  });

  const routerMap = {};
  for(const ar of authRouter){
    routerMap[ar.name] = ar;
  }

  const retMenus = [];
  for(const menuGroup of ernsCochingBackOfficeViews){
    if(!menuGroup.hasOwnProperty('children')){
      retMenus.push(menuGroup);
      continue;
    }

    //2Depth
    const children = [];
    for(const subMenu of menuGroup.children){
      const authRouterInfo = routerMap[subMenu.route.name];
      if(!authRouterInfo){
        children.push(subMenu);
        continue;
      }

      for(const ckGroup of hasGroups){
        if(authRouterInfo.meta.userGroups.includes(ckGroup)){
          children.push(subMenu);
          break;
        }
      }      
    }

    if(children.length > 0){
      const addMenuGroup = {
        ...menuGroup
        ,children : children
      }
      retMenus.push(addMenuGroup);
    }
  }

  return retMenus;
};

const getNavMenuItems = (userData)=>{
  const role = userData.role;

  if(role == "ROLE_GUEST"){
    return [
      //Nothing
    ]
  }

  if(role == 'ROLE_ADMIN'){
    const boMenus = filterBoMenu(userData.boUserGroups);
    return [
      ...boMenus,
    ]
  }

  return [];
} 

export {
  getNavMenuItems
};