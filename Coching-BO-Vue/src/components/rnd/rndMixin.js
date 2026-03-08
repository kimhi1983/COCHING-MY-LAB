import appConfig from '@/config';
import moment from 'moment';

import { DEF_SEARCH_OPT } from '@/constants/rnd';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

import { 
  getEsIngredientList,
  getEsIngredient
} from "@/api/coching-bo/search/esSearch";

export default {
  data() {
    return {

    }
  },
  computed: {

  },
  filters: {
    
  },  
  methods: {  
    async findIngredientList(pIngredientList) {
      const _vm = this;

      let ingredientList = pIngredientList;
      if(ingredientList.length == 0) return ingredientList;

      const esIngredientList = await Promise.all(ingredientList.map(async (item)=>{
        return await _vm.findIngredientFromEs(item);
      }));

      return esIngredientList;
    },

    /** ES에서 성분 조회 */
    async findIngredientFromEs(ingredientItem) {
      const _vm = this;

      let retVal = ingredientItem;

      const esId = ingredientItem.id;
      const esInci = ingredientItem.inci || ingredientItem.rep_name_en;
      const esRepName = ingredientItem.rep_name;

      let isFound = false;

      //1. id로 조회
      if(esId && esId > 0){
        const res = await getEsIngredient({
          idType : 'K',
          id : esId,
        });

        if(res.resultData.list.length > 0){
          retVal = {...retVal, ...res.resultData.list[0].source};
          isFound = true;
        }
      }

      //2. inci로 조회
      if(!isFound && esInci && esInci.trim()){
        const res = await getEsIngredientList({
          text: esInci, exactMatchOnly: true, rowSize : 1
        });
          
        if(res.resultData.list.length > 0){
          retVal = {...retVal, ...res.resultData.list[0].source};
          isFound = true;
        }
      }

      //3. rep_name으로 조회
      if(!isFound && esRepName && esRepName.trim()){
        const res = await getEsIngredientList({
          text: esRepName, exactMatchOnly: true, rowSize : 1
        });
        if(res.resultData.list.length > 0){
          retVal = {...retVal, ...res.resultData.list[0].source};
          isFound = true;
        }
      }

      return retVal;
    }
  }
}