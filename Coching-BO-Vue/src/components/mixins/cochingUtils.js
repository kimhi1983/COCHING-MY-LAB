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
        return "[No Data]";
      }

      const retNames = arrays.map(item=>{
        return item[nameKey];
      });

      return retNames.join(', ');
    },

    //검색어 검색
    goSearchKeyword(searchWord){
      const _vm = this;

      _vm.ermPushPage({
        name:'coching-search-main', 
        query : {
          keyword : searchWord
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
          _highlight: item.highlight,
          ...item.source,
        };
      });
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
