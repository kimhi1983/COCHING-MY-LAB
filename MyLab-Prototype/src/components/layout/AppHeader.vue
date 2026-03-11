<template>
  <header class="app-header">
    <div class="header-left">
      <div class="header-date">{{ formattedDate }}</div>
      <h1 class="header-title">{{ currentTitle }}</h1>
    </div>
    <div class="header-actions">
      <router-link to="/formulas/new" class="btn btn-primary">+ 새 처방 생성</router-link>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const formattedDate = computed(() => {
  const d = new Date()
  const days = ['일', '월', '화', '수', '목', '금', '토']
  return `${d.getFullYear()}년 ${d.getMonth() + 1}월 ${d.getDate()}일 ${days[d.getDay()]}요일`
})

const currentTitle = computed(() => route.meta?.title || 'MyLab')
</script>

<style scoped>
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding: 24px 36px 16px;
  border-bottom: 1px solid var(--border);
}
.header-date {
  font-size: 11px;
  color: var(--text-dim);
  letter-spacing: 0.5px;
}
.header-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text);
  margin-top: 2px;
  letter-spacing: -0.3px;
}
.btn {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.15s;
}
.btn-primary {
  background: var(--accent);
  color: #fff;
  box-shadow: 0 2px 8px rgba(184, 147, 90, 0.3);
}
.btn-primary:hover {
  background: #a68350;
}

@media (max-width: 767px) {
  .app-header { padding: 16px; flex-wrap: wrap; gap: 8px; }
  .header-title { font-size: 18px; }
}
</style>
