<template>
  <b-form-select
    ref="select"
    v-model="selectedValue"
    :options="options"
    @change="onValueChanged"
    @keydown.enter="onEnter"
    @keydown.esc="onEsc"
  />
</template>

<script>
export default {
  data() {
    return {
      selectedValue: null,
      options: []
    };
  },
  mounted() {
    
    if (!this.params) {
      return;
    }
    
    // 부모에서 넘긴 options로 업데이트
    if (this.params.options && Array.isArray(this.params.options)) {
      this.options = this.params.options;
    }
    
    const currentValue = this.params.value || (this.params.node && this.params.node.data && this.params.column ? this.params.node.data[this.params.column.colId] : null);
    
    // b-form-select는 value만 사용하므로 직접 설정
    this.selectedValue = currentValue;
    
    this.$nextTick(() => {
      if (this.$refs.select) {
        this.$refs.select.focus();
      }
    });
  },
  methods: {
    getValue() {
      return this.selectedValue;
    },
    onValueChanged(value) {
      // 값이 변경되면 편집 모드 종료
      if (this.params && this.params.api) {
        this.params.api.stopEditing();
      }
    },
    onEnter() {
      if (this.params && this.params.api) {
        this.params.api.stopEditing();
      }
    },
    onEsc() {
      // 원래 값으로 복원
      if (this.params) {
        this.selectedValue = this.params.value;
      }
      if (this.params && this.params.api) {
        this.params.api.stopEditing();
      }
    }
  }
};
</script>

<style scoped>
/* b-form-select 스타일 커스터마이징 */
:deep(.form-control) {
  border: none;
  padding: 4px;
  background: transparent;
  font-size: 14px;
}

:deep(.form-control:focus) {
  box-shadow: none;
  border-color: #007bff;
}
</style>
