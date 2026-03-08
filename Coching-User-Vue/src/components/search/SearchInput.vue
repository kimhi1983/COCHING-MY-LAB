<template>
  <input
    ref="input"
    :value="model"
    @input="handleInput"
    @focus="$emit('focus', $event)"
    @change="$emit('change', $event)"
    @keyup.esc="$emit('keyup.esc', $event)"
    @keyup.enter="$emit('keyup.enter', $event)"
    :maxlength="maxLength"
  />
</template>

<script>
export default {
  name: 'Coching-Search-Input',
  props: {
    value: {
      type: String,
      default: ''
    },
    maxlength: {
      type: [Number, String],
      default: Infinity
    }
  },
  data() {
    return {
      model: this.value
    };
  },
  watch: {
    value(newVal) {
      this.model = newVal;
    },
    model(newVal) {
      if (newVal && newVal.length > this.maxLength) {
        this.model = newVal.substring(0, this.maxLength);
      }
      this.$emit('input', this.model); // v-model
    }
  },
  computed: {
    maxLength() {
      return Number(this.maxlength) || Infinity;
    }
  },
  methods: {
    handleInput(e) {
      this.model = e.target.value;
    },
    focus() {
      this.$refs.input && this.$refs.input.focus();
    },
    blur() {
      this.$refs.input && this.$refs.input.blur();
    }
  }
};
</script>