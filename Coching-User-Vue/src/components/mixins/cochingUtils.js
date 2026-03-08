function splitNumbers(input) {
  // 정규식을 이용하여 문자열에서 숫자 추출
  const matches = input.match(/\d+/g);

  if (!matches || matches.length == 1) {
    const [num1] = matches.map(Number); // 숫자로 변환  
    return { num1, num2 : num1 };
  }

  if (!matches || matches.length < 2) {
      throw new Error("입력에 숫자가 두 개 이상 포함되어 있지 않습니다.");
  }

  // 첫 번째 숫자와 두 번째 숫자를 변수로 분리
  const [num1, num2] = matches.map(Number); // 숫자로 변환
  return { num1, num2 };
}

import moment from 'moment';

export default {
  data() {
    return {

    }
  },
  filters: {
    // 조회수 포맷팅 필터
    fmtViews(value) {
      if (typeof value !== 'number') return value; // 숫자가 아닐 경우 그대로 반환
      if (value < 1000) return value.toString(); // 1000 미만은 그대로 반환
      return `${(value / 1000).toFixed(1)}k`; // 1000 이상은 '1.1k' 형식으로 반환
    }
  },
  methods: {
    /** EWG 텍스트를 파싱한다. */
    getEwgInfo(inputEwgVal){
      const retVal = {
        scoreText : "-",
        score : 99,
        min : 99,
        max : 99
      }

      if(inputEwgVal == null || inputEwgVal == undefined || inputEwgVal == 'null-null'){
        return retVal;
      }

      try{
        const pRet = splitNumbers(inputEwgVal);        

        retVal.score = Math.max(pRet.num1, pRet.num2);
        retVal.max = Math.max(pRet.num1, pRet.num2);
        retVal.min = Math.min(pRet.num1, pRet.num2);

        if(retVal.min == retVal.max){
          retVal.scoreText = `${retVal.min}`;
        }else{
          retVal.scoreText = `${retVal.min}-${retVal.max}`;
        }        
      }catch(e){
        console.error(e);
        
      }
      return retVal;
    },

    /** EWG 색상클래스 리턴 */
    getEwgClass(pEwgScore){
      const ewg = pEwgScore || 10;

      if(ewg == 99){
        return 'gray';
      }
      
      if(ewg <= 2){
        return 'green';
      }

      if(ewg <= 6){
        return 'yellow';
      }

      return 'red';
    },

    // 배열에서 null과 undefined를 필터링하고 중복을 제거
    cleanArrayWithReduce(arr) {
      if (!Array.isArray(arr)) {
        throw new TypeError("Input must be an array");
      }
    
      return arr.reduce((acc, item) => {
       
        if (item !== null && item !== undefined && !acc.includes(item)) {
          acc.push(item);
        }
        return acc;
      }, []);
    },

    //배열을 ','로 표시
    displayArrayVal(arrVal){
      if(!arrVal){
        return "-";
      }
      return this.cleanArrayWithReduce(arrVal).join(", ");
    },

    //성분명 html
    displayIngdNamesHtml(ingdElemName, index, arrays){
      const _vm = this;

      let retHtml = (ingdElemName || '').split(", ")[0];

      //TODO 
      //<span class="highlight">글리세린</span>
      if(index < (arrays.length-1)){
        retHtml = retHtml+",";
      }

      return retHtml || '-';
    },

    //효능,기능,제품,성상,원물
    displayCateNames(arrays, nameKey){
      const _vm = this;

      if(arrays.length <=0){
        return "-";
      }

      const retNames = arrays.map(item=>{
        return item[nameKey];
      });

      return retNames.join(', ');
    },

    //검색어 검색
    goSearchKeyword(searchWord, pHintField, pTargetRouteName){
      const _vm = this;

      const hintField = pHintField || '0000';
      const curRouteName = _vm.$route.name;
      let targetRouteName = 'coching-search-main';
      if(pTargetRouteName){
        targetRouteName = pTargetRouteName;
      }else{
        if(curRouteName == 'coching-search-raws'){
          targetRouteName = 'coching-search-raws';
        }else if(curRouteName == 'coching-search-ingredients'){
          targetRouteName = 'coching-search-ingredients';
        }
      }

      _vm.ermPushPage({
        name: targetRouteName, 
        query : {
          keyword : searchWord
          , hf : hintField
        }
      });
    },

    //검색어 검색
    goSearchKeywordV2(searchWord, pSearchOption, pTargetRouteName){
      const _vm = this;
      const searchOption = {
        hintField : '0000',
        //exactMatchOnly : false,
        emo : false,
        ...pSearchOption
      };

      const curRouteName = _vm.$route.name;
      let targetRouteName = 'coching-search-main';
      if(pTargetRouteName){
        targetRouteName = pTargetRouteName;
      }else{
        if(curRouteName == 'coching-search-raws'){
          targetRouteName = 'coching-search-raws';
        }else if(curRouteName == 'coching-search-ingredients'){
          targetRouteName = 'coching-search-ingredients';
        }
      }

      _vm.ermPushPage({
        name: targetRouteName, 
        query : {
          keyword : searchWord
          , hf : searchOption.hintField
          , emo : searchOption.emo
          , rs : searchOption.rs
        }
      });
    },

    //API 버전에 따른 결과 컨버팅
    convertSearchResultByVersion(version, resList){
      if(version == 1){
        return resList;
      }

      return resList.map((item, index) => {
        return {
          _score : item.score,
          _highlights: item.highlights,
          ...item.source,
        };
      });
    },

    //성분 국가별 제한 데이터 컨버팅
    convertIngredientNationLimitData_old(limitResultData, mfdsSearchParam){
      const {list} = limitResultData;

      let nationLimitsData = {
        hasData : false,
        rawList:[],
        displayList : [],
        externalUrl : "",
        updateDate : "-"
      };

      if(mfdsSearchParam){        
        const limitParams = {
          searchYn : 'true'
          , searchCountry : '전체'
          // , searchCondition : 'ingrEngName'
          // , searchValue : inicName
          , btnSearch : ''
          , sort : ''
          , sortOrder : 'false'
          , page : 1
          , limit : 10
          ,...mfdsSearchParam
        }
        const limitUrl = `https://nedrug.mfds.go.kr/pbp/CCBDG01/getList?${new URLSearchParams(limitParams).toString()}`;            
        nationLimitsData.rawList = list;
        nationLimitsData.externalUrl = limitUrl;
        nationLimitsData.hasData = true;
        if(list.length > 0){
          nationLimitsData.updateDate = list[0].updateDate;
        }

        const DEFAULT_LIMIT_TYPES = [
          {name:"가능", value:"ic-check-sm"},
          {name:"한도", value:"ic-warning-sm"},
          {name:"금지", value:"ic-close-sm"},
        ];

        const limitMap = {};
        list.forEach(item=>{
          const {nationKo, limitKo} = item;
          if(!limitMap[limitKo]){
            limitMap[limitKo] = {
              name : limitKo,
              icon : DEFAULT_LIMIT_TYPES.find(item=>item.name == limitKo)?.value || "ic-check-sm",
              nationKoList : [nationKo],
            } ;
          }else{
            limitMap[limitKo].nationKoList.push(nationKo);
          }
        });
        
        const dList = Object.entries(limitMap).map(([limitKo, limitData])=>{ 
          return {
            ...limitData,
            nationKoListStr : limitData.nationKoList.join(", ")
          }
        });

        nationLimitsData.displayList = dList;
      }

      return nationLimitsData;
    },

    //성분 국가별 수출 제한 데이터 컨버팅
    convertIngredientNationLimitData(limitResultData, mfdsSearchParam, codeList){
      const data = limitResultData;

      let nationLimitsData = {
        hasData: false,
        data: {},
        displayList: [],
        externalUrl: "",
        updateDate: "-"
      };

      if (!data) {
        return nationLimitsData;
      }

      if(mfdsSearchParam){ 
        const limitParams = {
          searchYn : 'true'
          , searchCountry : '전체'
          // , searchCondition : 'ingrEngName'
          // , searchValue : inicName
          , btnSearch : ''
          , sort : ''
          , sortOrder : 'false'
          , page : 1
          , limit : 10
          ,...mfdsSearchParam
        }
        const limitUrl = `https://nedrug.mfds.go.kr/pbp/CCBDG01/getList?${new URLSearchParams(limitParams).toString()}`;            
        nationLimitsData.data = data;
        nationLimitsData.externalUrl = limitUrl;
        nationLimitsData.hasData = true;
        nationLimitsData.updateDate = data.rgtDttm || "-";

        // 제한 코드 아이콘 매핑
        const iconMap = {
          "001": "ic-check-sm",    // 제한없음
          "002": "ic-close-sm",    // 금지/제한
          "003": "ic-warning-sm"   // 확인필요
        };
        
        const LIMIT_CODE_MAP = new Map();

        Object.entries(iconMap).forEach(([code, icon]) => {
          const codeInfo = codeList.find(item => item.code === code);
          
          if (codeInfo) {
            LIMIT_CODE_MAP.set(code, {
              name: codeInfo.codeName,
              icon: icon
            });
          }
        });

        // 국가별 데이터 매핑
        const NATION_MAP = {
          "eu": "EU",
          "usa": "미국", 
          "china": "중국",
          "japan": "일본",
          "korea": "한국"
        };

        const limitMap = new Map();

        LIMIT_CODE_MAP.forEach((limitInfo, code) => {
          const nationList = [];
          
          Object.entries(NATION_MAP).forEach(([codeKey, nationName]) => {
            if (data[codeKey] === code) {
              nationList.push(nationName);
            }
          });
          
          if (nationList.length > 0) {
            limitMap.set(limitInfo.name, {
              name: limitInfo.name,
              icon: limitInfo.icon,
              nationKoList: nationList
            });
          }
        });

        const dList = Array.from(limitMap.values()).map(limitData => {
          return {
            ...limitData,
            nationKoListStr: limitData.nationKoList.join(", ")
          };
        });

        nationLimitsData.displayList = dList;
      }

      return nationLimitsData;
    },

    convertIngredientNationExpLimitData(limitResultData, mfdsSearchParam, codeList){
      const data = limitResultData;

      let nationLimitsData = {
        hasData: false,
        data: {},
        displayList: [],
        updateDate: "-"
      };

      if(!data){
        return nationLimitsData;
      }

      if(mfdsSearchParam){
        nationLimitsData.data = data;
        nationLimitsData.hasData = true;
        nationLimitsData.updateDate = data.rgtDttm || "-";

        const iconMap = {
          "001": "ic-check-sm",    // 규제없음
          "002": "ic-close-sm",    // 수출불가
        };
  
        const LIMIT_CODE_MAP = new Map();
        Object.entries(iconMap).forEach(([code, icon]) => {
          const codeInfo = codeList.find(item => item.code === code);
          
          if (codeInfo) {
            LIMIT_CODE_MAP.set(code, {
              name: codeInfo.codeName,
              icon: icon
            });
          }
        });
  
        const NATION_MAP = {
          "eu": "EU",
          "usa": "미국", 
          "china": "중국",
          "japan": "일본",
          "korea": "한국"
        };
  
        const limitMap = new Map();
  
        LIMIT_CODE_MAP.forEach((limitInfo, code) => {
          const nationList = [];
          
          Object.entries(NATION_MAP).forEach(([codeKey, nationName]) => {
            if (data[codeKey] === code) {
              nationList.push(nationName);
            }
          });
          
          if (nationList.length > 0) {
            limitMap.set(limitInfo.name, {
              name: limitInfo.name,
              icon: limitInfo.icon,
              nationKoList: nationList
            });
          }
        });
  
        const dList = Array.from(limitMap.values()).map(limitData => {
          return {
            ...limitData,
            nationKoListStr: limitData.nationKoList.join(", ")
          };
        });
  
        nationLimitsData.displayList = dList;
      }

      return nationLimitsData;
    },

    //프록시 경로
    getProxyFile(orgUrl) {
      //const BASE_URL = process.env.VUE_APP_API_BASE_URL;
      const BASE_URL = 'https://coching.co.kr'; //운영계 고정
      const encodedUrl = encodeURIComponent(orgUrl);
      const url = `${BASE_URL}/common/img/pc?url=${encodedUrl}`;
      //console.debug(url);
      return url;
    }
  }
}
