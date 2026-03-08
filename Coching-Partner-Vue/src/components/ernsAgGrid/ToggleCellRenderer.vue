<template>
  <div class="gridswitchbox">
    <label class="switch">
      <input type="checkbox"
        @click="onClick($event)" 
        :checked="checked" 
        :disabled="isDisabled()"/>
      <span class="slider"></span>
    </label>
</div>
</template>

<script>

const DEF_CHECKED = false;

export default {
  props: {
    // params: Object, // ag-Grid에서 전달하는 기본 데이터    
  },
  computed: {
    
  },
  beforeMount() {
    const _vm = this;

    _vm.checked = _vm.params.checked || DEF_CHECKED;
    
  },
  data() {
    return {
      checked : DEF_CHECKED,
    }
  },  
  methods: {
    isDisabled() {
      if (this.params.isDisabled) {
        return this.params.isDisabled(this.params);
      }
      return false;
    },    
    onClick(event) {
      const _vm = this;

      if (!_vm.params.isChange) {
        event.preventDefault(); // 기본 동작 막기
        event.stopPropagation(); // 이벤트 전파 막기
      }

      if (_vm.params.action) {
        _vm.params.action(_vm.params);
      }
    },    
  }
};
</script>

<style lang="scss" scoped>
</style>
