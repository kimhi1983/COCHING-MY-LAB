<template>
  <span class="status-chip" :style="chipStyle" @click="$emit('click')">
    {{ statusLabel }}
  </span>
</template>

<script setup>
import { computed } from 'vue'
import { statusStyles } from '../../tokens.js'

const props = defineProps({
  status: { type: String, default: 'draft' },
})
defineEmits(['click'])

const style = computed(() => statusStyles[props.status] || statusStyles.draft)
const statusLabel = computed(() => style.value.label)
const chipStyle = computed(() => ({
  color: style.value.color,
  background: style.value.bg,
  border: `1px solid ${style.value.border}`,
}))
</script>

<style scoped>
.status-chip {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 600;
  font-family: monospace;
  letter-spacing: 0.5px;
  cursor: default;
}
</style>
