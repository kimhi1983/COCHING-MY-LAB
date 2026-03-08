import appConfig from '@/config';
import moment from 'moment';

import { REF_CODES } from '@/constants/favorite';

import { getMyFavoriteList, toggleMyFavorite, removeMyFavorite } from '@/api/coching/member/favorite';
import { EventBus } from '@/components/eventBus'; 
import { isUserLoggedIn} from '@/auth/utils';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;


export default {
  data() {
    return {
      isLoggedIn:  (isUserLoggedIn() || null) ? true : false,
    }
  },
  computed: {

  },
  filters: {
    
  },  
  methods: {  
    async setToggleMyFavorite(item){
      const _vm = this;

      if(!isUserLoggedIn()){
        await _vm.onClickConfirmLogin();
        return;
      }
      
      _vm.loading(true);
      try{
        const params = {
          ...item
        };
        
        const res = await toggleMyFavorite(params);        
        EventBus.$emit('set-my-favorite', {
          ...params,
        });      
        return res;

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }

      return null;
    },

    async getMyFavorites(refCode, refSeqs){
      const _vm = this;

      if(!isUserLoggedIn()){
        return null;
      }

      _vm.loading(true);
      try{
        const params = {
          refCode: refCode,
          refSeqs: refSeqs, 
        };
        
        const res = await getMyFavoriteList(params);        
        EventBus.$emit('set-my-favorite', {
          ...params,
        });      
        return res;

      }catch(err){
        console.error(err);
        _vm.defaultApiErrorHandler(err); //기본에러처리
      }finally{
        _vm.loading(false);
      }

      return null;
    }
  }
}