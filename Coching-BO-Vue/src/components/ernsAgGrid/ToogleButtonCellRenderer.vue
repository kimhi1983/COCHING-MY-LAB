<template>
  <b-form-checkbox
    @change="toggleStatus"
    v-model="value"
    switch

    :value="checkValue"
    :unchecked-value="uncheckValue"

    :size="size"
    :variant="variant"
    :disabled="checkEditRow && !isEditRow()"
  >
  </b-form-checkbox>
</template>

<script>

const DEF_SIZE = "sm";
const DEF_VARIANT = "outline-primary";
const DEF_CHECKING_EDITROW = true;
const DEF_CHECK_VALUE = "Y";
const DEF_UNCHECK_VALUE = "N";


export default {
  props: {
    //params: Object, // ag-Grid에서 전달하는 기본 데이터    
  },
  beforeMount() {
    const _vm = this;

    _vm.field = _vm.params.field;
    _vm.value = _vm.params.value;

    _vm.checkValue = _vm.params.checkValue || DEF_CHECK_VALUE;
    _vm.uncheckValue = _vm.params.uncheckValue || DEF_UNCHECK_VALUE;
    _vm.size = _vm.params.size || DEF_SIZE;
    _vm.variant = _vm.params.variant || DEF_VARIANT;

    if(_vm.params.checkEditRow == undefined || _vm.params.checkEditRow == null){
      _vm.checkEditRow = DEF_CHECKING_EDITROW;
    }else{
      _vm.checkEditRow = _vm.params.checkEditRow;
    }
    
  },
  data() {
    return {
      field : "",
      value : false,
      
      size : DEF_SIZE,
      variant : DEF_VARIANT,
      checkValue : DEF_CHECK_VALUE,
      uncheckValue : DEF_UNCHECK_VALUE,
      checkEditRow : DEF_CHECKING_EDITROW,
    }
  },  
  methods: {
    toggleStatus() {
      const _vm = this;

      _vm.params.node.setDataValue(_vm.field, _vm.value);
    },
    isEditRow(){
      const _vm = this;
      if (!_vm.checkEditRow) {
        return false;        
      }      

      return _vm.params.data["_isEditRow"];
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
