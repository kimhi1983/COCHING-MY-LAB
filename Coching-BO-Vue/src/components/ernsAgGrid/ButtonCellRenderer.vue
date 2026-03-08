<template>
  <b-button 
    @click="onClick" 
    class="btn"
    :size="size"
    :variant="variant"
    :disabled="isDisabled()"
    v-b-tooltip.hover
    :title="tooltip"
  >{{label}}</b-button>
</template>

<script>

const DEF_LABLE = "버튼";
const DEF_SIZE = "sm";
const DEF_VARIANT = "outline-primary";
const DEF_CHECKING_EDITROW = true;


export default {
  props: {
    //params: Object, // ag-Grid에서 전달하는 기본 데이터    
  },
  computed: {
    
  },
  beforeMount() {
    const _vm = this;

    _vm.label = _vm.params.label || DEF_LABLE;
    _vm.size = _vm.params.size || DEF_SIZE;
    _vm.variant = _vm.params.variant || DEF_VARIANT;
    _vm.tooltip = _vm.params.tooltip || '';
    if(_vm.params.checkEditRow == undefined || _vm.params.checkEditRow == null){
      _vm.checkEditRow = DEF_CHECKING_EDITROW;
    }else{
      _vm.checkEditRow = _vm.params.checkEditRow;
    }
    
  },
  data() {
    return {
      lable : DEF_LABLE,
      size : DEF_SIZE,
      variant : DEF_VARIANT,
      checkEditRow : DEF_CHECKING_EDITROW,
      tooltip : '',
    }
  },  
  methods: {
    isDisabled() {
      if (this.params.isDisabled) {
        return this.params.isDisabled(this.params);
      }
      return false;
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
</style>
