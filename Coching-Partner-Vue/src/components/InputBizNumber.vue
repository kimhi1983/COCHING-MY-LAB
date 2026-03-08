<template>
  <input 
    type="text"
    :value="formattedValue"
    @blur="formatNumber"
    @focus="unformatNumber"
    @input="updateValue($event.target.value)"
    :placeholder="$attrs.placeholder"
    :disabled="$attrs.disabled"
    :readonly="$attrs.readonly"      
    :class="$attrs.class">      
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
export default {
  name: "Erns-Input-BizNumber",
  mixins: [ernsUtils],
  components : {},
  props: ['value'],
  computed: {
    formattedValue() {
      // 숫자가 아니라면 원래 값을 반환
      if (isNaN(this.value)) return this.value;
      // 숫자를 포맷팅하여 표시
      return this.formatNumberDisplay(this.value);
    }
  },
  data() {
    return {
      
    }
  },  
  mounted(){
    const _vm = this;
    
  },
  
  methods : {
    // 숫자만 추출 (포맷팅 제거)
    getNumberVal(paramValue){ 
      const _vm = this;
      let value = "" + (paramValue || '');
      if(!value)
        return "";      

      const numberValue = ""+value.replace(/[^\d]/g, '');
      return numberValue;
    },
    formatNumberDisplay(value) {
      const _vm = this;
      // 숫자를 포맷팅하여 표시

      if(isNaN(value)){
        return "";
      }

      const fmtBizNumber = _vm.$options.filters.eufmtBizNumDash(""+value);
      if(fmtBizNumber == "-"){
        return "";
      }

      return fmtBizNumber;
    },

    updateValue(value) {
      const _vm = this;

      // 숫자만 추출 (포맷팅 제거)
      const numberValue = _vm.getNumberVal(value);
      // 부모 컴포넌트에 업데이트
      this.$emit('input', numberValue);
    },
    
    formatNumber() {
      const _vm = this;

      const retVal = _vm.getNumberVal(_vm.value);
      //console.debug(retVal);
      this.$emit('input', ""+retVal);
    },    
    unformatNumber() {
      const _vm = this;

      const retVal = _vm.value;
      //console.debug(retVal);
      this.$emit('input', ""+_vm.value);
    }   
  }
}
</script>

<style lang="scss" scoped>

</style>

<style lang="scss">
  
</style>