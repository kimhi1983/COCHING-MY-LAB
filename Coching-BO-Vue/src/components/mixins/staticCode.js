import APP_CONFIG from '@/config';

import {getEnumCodes} from '@/api/coching-bo/comm/code';

export default {
  data() {
    return {

    }
  },
  filters: {
    /**
     * 카드,세금계산서,현금,쇼핑몰,배달앱 노출명칭 리턴
     * @param {*} dataType 통계데이터 타입
     */
     eufmtDataTypeDspName(dataType){
      switch(dataType){
        case "CARDSA" :
        case "CARDSC" :
        case "CARDP" :
          return "카드";
        case "MALL" : 
          return "쇼핑몰";
        case "DAPP" : 
          return "배달앱";
        case "EBILL" : 
          return "전자세금";
        case "CASH" : 
          return "현금";
        default:
          return "";
      }
    }
  },
  computed: {
    
  },
  methods: {
    //서버로 부터 Enum 목록을 가져온다
    async eumGetEnumCodes(codeMst){
      const _vm = this;

      try{
        const res = await getEnumCodes({codeMst:codeMst});
        const { resultCode, resultFailMessage, resultData } = res;

        let retCodes = resultData.list;

        switch(codeMst){
          case "SCD_BANK":	//SCD 입금은행			
            retCodes = _vm.eumMergeCodes(APP_CONFIG.SCD_BANK_CODES, resultData.list, "code", "orgDivCd");
			      break;
			
		      case "SCD_CARD":	//SCD 대상카드사
            retCodes = _vm.eumMergeCodes(APP_CONFIG.SCD_CARD_CODES, resultData.list, "code", "orgDivCd");
			      break;

          case "SCRAP_BANK":	//자산 은행계좌
            retCodes = _vm.eumMergeCodes(APP_CONFIG.BANK_CODES, resultData.list, "code", "orgDivCd");
            break;
			
          case "SCRAP_LINK_BANK":	//자산 연동 은행계좌
            retCodes = _vm.eumMergeCodes(APP_CONFIG.LINK_BANK_CODES, resultData.list, "code", "orgDivCd");
            break;
            
          case "SCRAP_CARD":	//지출 카드
            retCodes = _vm.eumMergeCodes(APP_CONFIG.COST_CARD_CODES, resultData.list, "code", "orgDivCd");          
            break;
            
          case "SCRAP_DAPP":	//배달앱
            retCodes = _vm.eumMergeCodes(APP_CONFIG.DLAPP_CODES, resultData.list, "code", "orgDivCd");
            break;
            
          case "SCRAP_MALL":	//쇼핑몰
            retCodes = _vm.eumMergeCodes(APP_CONFIG.MALL_CODES, resultData.list, "code", "orgDivCd");
            break;
            
          case "SRV_TYPE":	//서비스구분
            break;
        }
        return retCodes;

      }catch(err){
        console.error(err);

        //ignore
        return [];
      }      
    },

    //2개 코드리스트 합치기
    eumMergeCodes(srcList1, srcList2, keyName1, keyName2){
      const _vm = this;

      const retList = srcList1.map((item1=>{
        const foundItem = srcList2.find(item2=>{
          return item1[keyName1] == item2[keyName2];
        });

        if(!foundItem){
          return item1;
        }

        return {
          ...item1,
          ...foundItem
        };
      }));

      return retList;
    },

    /**
     * 은행코드 정보를 찾아 반환
     * @param {*} code 은행코드
     * @returns 은행코드 Info Object
     */
    findBankCode(code, refBankList){
      const bankList = refBankList || APP_CONFIG.BANK_CODES;

      const findCode = bankList.find(item=>{
        return item.code == code;
      });

      return findCode || APP_CONFIG.DEFAULT_BANK;
    },

    /**
     * 은행코드 정보를 찾아 반환
     * @param {*} code 은행코드
     * @returns 은행코드 Info Object
     */
     findLinkBankCode(code, refBankList){
      const bankList = refBankList || APP_CONFIG.LINK_BANK_CODES;

      const findCode = bankList.find(item=>{
        return item.code == code;
      });

      return findCode || APP_CONFIG.DEFAULT_BANK;
    },

    /**
     * 카드사코드 정보를 찾아 반환(scd용)
     * @param {*} code 카드사코드
     * @returns 카드사코드 Info Object
     */
    findCardCode(code, refCardList){
      const cardList = refCardList || APP_CONFIG.SCD_CARD_CODES;

      const findCode = cardList.find(item=>{
        return item.code == code;
      });

      return findCode || APP_CONFIG.DEFAULT_CARD;
    },

    /**
     * 카드사코드 정보를 찾아 반환(지출용)
     * @param {*} code 카드사코드
     * @returns 카드사코드 Info Object
     */
     findCostCardCode(code, refCardList){
      const cardList = refCardList || APP_CONFIG.COST_CARD_CODES;

      const findCode = cardList.find(item=>{
        return item.code == code;
      });

      return findCode || APP_CONFIG.DEFAULT_COST_CARD;
    },

    /**
     * 쇼핑몰코드 정보를 찾아 반환
     * @param {*} code 쇼핑몰코드
     * @returns 쇼핑몰코드 Info Object
     */
     findMallCode(code){
      const findCode = APP_CONFIG.MALL_CODES.find(item=>{
        return item.code == code;
      });

      return findCode || APP_CONFIG.DEFAULT_MALL;
    },

    /**
     * 배달APP 정보를 찾아 반환
     * @param {*} code 배달APP코드
     * @returns 배달APP코드 Info Object
     */
    findDLAppCode(code){
      const findCode = APP_CONFIG.DLAPP_CODES.find(item=>{
        return item.code == code;
      });

      return findCode || APP_CONFIG.DEFAULT_DLAPP;
    },

    /**
     * 카드,세금계산서,현금,쇼핑몰,배달앱 정렬 순서 리턴
     * @param {*} dataType 통계데이터 타입
     */
    getDataTypeSortOrder(dataType){
      switch(dataType){
        case "CARDSA" :
        case "CARDSC" :
        case "CARDP" :
          return 1;
        case "MALL" : 
          return 4;
        case "DAPP" : 
          return 5;
        case "EBILL" : 
          return 3;
        case "CASH" : 
          return 2;
        default:
          return 999;
      }
    },
  }
};