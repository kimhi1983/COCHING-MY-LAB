<template>
  <div class="ewg-score-cell">
    <div class="ewg" 
    :class="getEwgClass(getEwgInfo(ewgVal).max)">
    {{getEwgInfo(ewgVal).scoreText}}</div>  
    <span>{{ ewgDataLabel }}</span>
  </div>
</template>

<script>

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

const DEF_EWG_NULL_VAL = "null-null";
const DEF_EWG_UNDEFIND_VAL = "undefined-undefined";

export default {
  props: {
    //params: Object, // ag-Grid에서 전달하는 기본 데이터    
  },
  computed: {
    
  },
  beforeMount() {
    const _vm = this;
    _vm.ewgVal = _vm.params.ewgVal || DEF_EWG_NULL_VAL;   
    _vm.ewgDataLabel = _vm.params.ewgDataLabel || "";
  },
  data() {
    return {
      ewgDataLabel : "data",  
      ewgVal : "1-2",
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

      if(inputEwgVal == null || inputEwgVal == undefined || (inputEwgVal || '').trim() == "" || inputEwgVal == DEF_EWG_NULL_VAL || inputEwgVal == DEF_EWG_UNDEFIND_VAL){  
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
    onClick() {
      const _vm = this;

      if (_vm.params.action) {
        _vm.params.action(_vm.params);
      }
    },    
  }
};
</script>

<style lang="scss" scoped>
.ewg-score-cell{
  display: flex;

  /*ewg*/
  .ewg {
    font-size: 0.8125rem;
    font-weight: 800;
    text-align: center;
    color: var(--color--wh);
    width: 2rem;
    height: 2rem;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    line-height: 1;

    &.green {
      background-color: #00a652;
    }
    &.yellow {
      background-color: #fdb813;
    }
    &.red {
      background-color: #ed1b24;
    }
  }

  span{
    line-height: 2.4;
    margin-left: 5px;
    font-size: smaller;
    color: #777; 
  }

  
}

</style>
