<template>
  <input
    ref="input"
    v-model="value"
    :placeholder="params.placeholder || 'Enter text...'"
    @keydown.enter="onEnter"
    @keydown.esc="onEsc"
  />
</template>

<script>
export default {
  data() {
    return {
      value: this.params.value || ""
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.input.focus();
    });
  },
  methods: {
    getValue() {
      return this.value;
    },
    onEnter() {
      this.params.api.stopEditing();
    },
    onEsc() {
      this.value = this.params.value;
      this.params.api.stopEditing();
    }
  }
};
</script>

<style scoped>
input {
  width: 100%;
  border: none;
  outline: none;
  padding: 4px;
}
</style>
