import { canNavigate } from '@/libs/acl/routeProtection';
import popup from './popup';
import banner from './banner';
import board from './board';
import sys from './sys';
import system from './system';
import terms from './terms';
import ver from './ver';
import main from './main';
import partner from './partner';
import member from './member';
import raw from './raw';
import mltln from './mltln';
import search from './search';
import cochingtv from './cochingtv';
import rnd from './rnd';


const rawData = [
  ...popup,
  ...banner,  
  ...board,
  ...sys,
  ...system,
  ...terms,
  ...ver,
  ...main,

  ...partner,
  ...member,
  ...raw,
  ...mltln,
  ...search,
  ...cochingtv,
  ...rnd
]

const data = rawData.map(item=>{
  return {
    ...item
    , meta: {
      ...item.meta,
      resource: 'ADMIN_MENU',
      action: 'read',
    },
  }
})

export default data
