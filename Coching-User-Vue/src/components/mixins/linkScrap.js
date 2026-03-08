import APP_CONFIG from '@/config';

const VUE_APP_BASE_ROUTER_PATH = process.env.VUE_APP_BASE_ROUTER_PATH;

import {  
  scrapLinkStatus,
  scrapLinkedAccList,
} from '@/api/coching/member/scrapCmInfo';

export default {
  data() {
    return {
      joinMode : false,

      //인증정보 현황
      sLinkStatus : {
        isLoad : false,
        statusMap : {},

        //해당 카테고리 정보 있는지
        hasCardSales : false,   //카드매출
        hasSales : false,       //매출
        hasCost : false,        //지출
        hasCalc : false,        //정산
        hasBankAc : false,      //계좌

        resData : {},        
      },
    }
  },
  filters: {
    //인증상태 표시
    authStatDesc(val){
      const _vm = this;

      const checkVal = (val || '').trim();
      if("" == checkVal){
        return null;
      }

      /**
       * U0001	회원가입	:			회원가입 요청 후 회원가입 2차 인증 요청 차례
       * U0002	아이디 찾기 :			회원가입 2차 인증 요청 후 아이디 찾기 인증 요청 차례
       * U0002A	아이디 찾기 인증번호 입력 :	아이디 찾기 요청 후 아이디 찾기 인증 요청 차례
       * U0003	비밀번호 변경 :			아이디 찾기 인증 후 비밀번호 변경 요청 차례
       * U0003A	비밀번호 인증번호 요청 : 비밀번호 변경 요청 후 비밀번호 2차 인증 요청 차례
       * U0010	이메일 중복	:			이메일 중복 체크 후 중복 발생
       */
      switch(checkVal){
        case "U0001" : return "가맹점 신규가입 인증";
        case "U0002" : return "아이디 찾기 인증";
        case "U0002A" : return "아이디 찾기 2차 인증";
        case "U0003" : return "비밀번호 찾기 인증";
        case "U0003A" : return "비밀번호 찾기 2차인증";
        case "U0010" : return "이메일 중복확인";
      }

      return null;
    },

    //인증번호 시간
    certificationSec(val){
      let ret = "0:00";
      let min = Math.floor(val / 60);
      let sec = val % 60;
      ret = `${min}:${sec < 10 ? '0' : ''}${sec}`;
      return ret;
    }
  },
  computed: {
    //연동 카테고리  
    // join:회원가입 (step 화면을 표시)
    // all:모두 연동, scd:한번에 카드가맹점, sales:매출, cost:지출, calc:정산, bank:은행
    scrapLinkCate(){
			return this.$store.state.coching.scrapLinkCate;
		},

    //오직 카드매출 연결인지
    linkOnlyScd(){
      const slCate = this.scrapLinkCate;
			return slCate == 'scd';			
    },
    
    //오직 매출연결인지
		linkOnlySales(){
			const slCate = this.scrapLinkCate;
			return slCate == 'scd' || slCate == 'sales' || slCate == 'calc';			
		},

    //오직 지출 연결인지
		linkOnlyCost(){
			const slCate = this.scrapLinkCate;
			return slCate == 'cost';
		},

    //매출 연결 필요한지
		isNeedLinkSales(){
			const slCate = this.scrapLinkCate;
			return slCate == 'join' || slCate == 'all' || slCate == 'scd' || slCate == 'calc';			
		},

    //지출 연결 필요한지
		isNeedLinkCost(){
			const slCate = this.scrapLinkCate;
			return slCate == 'join' || slCate == 'all' || slCate == 'cost';			
		},

    //오직 카드지출인지
    linkOnlyCostCard(){
			const slCate = this.scrapLinkCate;
			return slCate == 'card';
		},

  },
  methods: {
    // 연동정보 로딩
    async loadLinkStatusInfo(){
      const _vm = this;

      const sLinkStatus = _vm.sLinkStatus;
      { //스크래핑 정보 로딩
        const slsRes = await scrapLinkStatus({bsnSeq:_vm.activeBizLicInfo.bsnSeq});       
        sLinkStatus.isLoad = true;
        sLinkStatus.resData = slsRes.resultData;
        sLinkStatus.statusMap = {};
        for(const item of sLinkStatus.resData.list){
          sLinkStatus.statusMap[item.orgCd] = item;
        }

        //해당 카테고리 정보 있는지 설정

        //카드매출
        sLinkStatus.hasCardSales = sLinkStatus.statusMap['scd'].accLinkCnt > 0;   

        //매출
        sLinkStatus.hasSales = 
             sLinkStatus.statusMap['scd'].accLinkCnt > 0 //카드 매출
          || sLinkStatus.statusMap['hometax'].accLinkCnt > 0
          || sLinkStatus.statusMap['mall'].accLinkCnt > 0
          || sLinkStatus.statusMap['dlvy'].accLinkCnt > 0 ;     
          
        //지출
        sLinkStatus.hasCost =    
             sLinkStatus.statusMap['hometax'].accLinkCnt > 0
          || sLinkStatus.statusMap['card'].accLinkCnt > 0;  //카드 지출

        //정산
        sLinkStatus.hasCalc = 
             sLinkStatus.statusMap['scd'].accLinkCnt > 0
          || sLinkStatus.statusMap['mall'].accLinkCnt > 0
          || sLinkStatus.statusMap['dlvy'].accLinkCnt > 0 ;     

        sLinkStatus.hasBankAc = sLinkStatus.statusMap['bank'].accLinkCnt > 0;      //계좌
      }
    },

    // 연동 계정정보 로딩
    async loadLinkedAccList(orgCd, entKey, showError){
      const _vm = this;

      // 데이터 로드
      const params = {
        bsnSeq : _vm.activeBizLicInfo.bsnSeq,
      };
      if(orgCd){
        params["orgCd"] = orgCd;
      }
      if(entKey){
        params["entKey"] = entKey;
      }

      const res = await scrapLinkedAccList(params);

      const { resultCode, resultFailMessage, resultData } = res;
      if(resultCode != "0000"){
        if(showError){
          await _vm.$refs["alertModal"].open({title: "오류",
            content : resultFailMessage || '알수 없는 오류가 발생했습니다.'
          });
        }
        return [];
      } 

      return resultData.list;
    },

    //인증정보수 리턴
    getAccLinkCnt(section){
      const _vm = this;
      const sLinkStatus = _vm.sLinkStatus;

      if(!sLinkStatus.isLoad){
        return 0;
      }

      const secInfo = sLinkStatus.statusMap[section];
      if(!secInfo){
        return 0;
      }

      return sLinkStatus.statusMap[section].accLinkCnt;
    },

    // 미연동 
    onClickMoveLinkAccount(pCate){
      const _vm = this;

      let routerName = 'coching-settings-link-info';
      const query = {
        bsnSeq : _vm.activeBizLicInfo.bsnSeq
      };
      let cate = pCate || 'all';
      switch(cate){
        case "scd" : 
          routerName = 'coching-accountLink-cardSales-intro'; break;          

        case "calc" :         
          routerName = 'coching-accountLink-cardSales-intro'; break;
          
        case "sales" : 
          routerName = 'coching-accountLink-cardSales-intro'; break;
        
        case "cost" : 
          routerName = 'coching-accountLink-cardSpend-intro'; break;

        case "card" :
          routerName = 'coching-accountLink-cardSpend-intro'; break;

        case "bank" : 
          routerName = 'coching-accountLink-addInfoDesc'; break;

        case "hometax" : 
          routerName = 'coching-accountLink-addInfoDesc'; break;
        
        case "mall" : 
          routerName = 'coching-accountLink-addInfoDesc'; break;

        case "dlvy" : 
          routerName = 'coching-accountLink-addInfoDesc'; break;

        default:
          routerName = 'coching-settings-link-info';
          if(!(cate == 'all' || cate == 'join')){
            cate = 'all';
          }
          break;
      }
      _vm.$store.dispatch('coching/setScrapLinkCate', cate);      

      console.debug(`routerName:${routerName}, param:${JSON.stringify(query)}`);
      _vm.ermPushPage({
        name:routerName, query:{
          ...query
        }
      });			
      return;
    },

    //Scd: 가입 결과 처리
    getScrapJoinData(pOrgDivCd, joinResData){
      try{
        if(joinResData.data instanceof Array){
          const joinDataList = joinResData.data;

          const retJoinData = joinDataList.find(item=>{
            return pOrgDivCd == item.cardCd;
          });

          return retJoinData ? retJoinData : null;

        }else if(joinResData.data instanceof Object){
          
          const retJoinData = joinResData.data;
          if(pOrgDivCd == retJoinData.cardCd){
            return retJoinData;
          }
        }
    
      }catch(e){
        console.error(e);
      }
    
      return null;
    },

    /**
     * Scd: 가입결과 처리
     * @param {*} pOrgDivCd 타깃 카드사 코드
     * @param {*} resData 가입결과 response
     * @returns 
     */
    async processScdJoinRes(pOrgDivCd, resData){
      const _vm = this;

      const joinData = _vm.getScrapJoinData(pOrgDivCd, resData);
      if(joinData == null){
        _vm.$refs["alertModal"].open({title: "오류",
          content : '응답오류가 발생하였습니다.'
        });          
        return;
      }

      if(joinData.joinYn == "Y"){
        //회원가입완료, 진행상태 표시
        _vm.ermReplacePage({name:"coching-accountLink-cardSales-joinStatus"});
        return;
      }

      switch(joinData.returnCode){
        case "U0002"  : //아이디 찾기
          await _vm.$refs["alertModal"].open({title: "이미 가입되어 있습니다.\n아이디 찾기로 이동합니다.",
            content : ""
          });  
          _vm.ermReplacePage({
            name:"coching-accountLink-cardSales-joinFindIdAuth",
            query :{
              orgDivCd : pOrgDivCd
            }
          });
          return;

        case "U0003"  : //비밀번호 찾기
          _vm.ermReplacePage({
            name:"coching-accountLink-cardSales-joinFindPwAuth",
            query :{
              orgDivCd : pOrgDivCd
            }
          });
          return;

        // case "U0010"  : //이메일 중복
        //   _vm.ermReplacePage({
        //     name:"coching-accountLink-cardSales-joinSetEmail",
        //     query :{
        //       orgDivCd : pOrgDivCd
        //     }
        //   });          
        //   return;

        case "U0002A"  : //아이디 찾기 인증번호 입력 후
        case "U0010"  : //비밀번호 인증번호 요청 후
        default :
          break;
      }
    },

    //Scd: 기본오류처리
    async defaultScdErrorHandler(err){
      const _vm = this;
      let errorMsg = '알수 없는 오류가 발생했습니다.';
      if(!err.response){
        console.error(err);
        _vm.$refs["alertModal"].open({title: errorMsg,
          content : ''
        });
        return true;
      }
      
      const { resultCode, resultFailMessage, resultMsg } = err.response.data;
      errorMsg = resultFailMessage || resultMsg || '통신이 원할하지 않습니다.\n잠시후 다시 시도해 주십시오.';
  
      if(resultCode == "19401") {
        _vm.loading(false);
        await _vm.$refs["alertModal"].open({title: errorMsg,
          content : ''
        });
  
        _vm.ermReplacePage({name:"coching-accountLink-cardSales-basicInfo"});  
        return true;
      }
  
      if(resultCode != "0000"){
        _vm.$refs["alertModal"].open({title: errorMsg,
          content : ''
        });          
        return true;
      }
  
      return false;
    }
  }
};