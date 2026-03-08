<template>
  <b-form-textarea
    ref="textarea"
    v-model="value"
    rows="4"
    class="textarea-edit-cell"
    @keydown.stop
  />
</template>

<script>
import { BFormTextarea } from 'bootstrap-vue';

export default {
  props: {
    //params: Object, // ag-Grid에서 전달하는 기본 데이터
  },
  beforeMount() {
    const _vm = this;
    _vm.value = _vm.params && _vm.params.value ? _vm.params.value : '';
  },
  components: { BFormTextarea },
  data() {
    return {
      value: ''
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.textarea.focus();
    });
  },
  methods: {
    getValue() {
      return this.value;
    }
  }
};
</script>

<style scoped>
.textarea-edit-cell {
  width: 100%;
  height: 100%;
  min-height: 80px;
}
</style> 