<template>
  <nav class="nav-bar">
    <div class="nav-pills">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="nav-pill"
        active-class="active"
        :exact="item.exact"
      >
        <span class="pill-icon">{{ item.icon }}</span>
        <span class="pill-label">{{ item.label }}</span>
      </router-link>
    </div>
    <div class="nav-tools">
      <slot name="tools" />
    </div>
  </nav>
</template>

<script setup>
const navItems = [
  { to: '/', icon: '◈', label: '대시보드', exact: true },
  { to: '/formulas', icon: '⚗', label: '처방 목록', exact: false },
  { to: '/formulas/new', icon: '+', label: '생성', exact: true },
  { to: '/journal', icon: '◉', label: '일지', exact: true },
  { to: '/projects', icon: '◎', label: '프로젝트', exact: true },
  { to: '/ai-guide', icon: '✦', label: 'AI 가이드', exact: true },
]
</script>

<style scoped>
.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 36px;
  border-bottom: 1px solid var(--border);
  background: var(--surface);
  min-height: 44px;
}

.nav-pills {
  display: flex;
  gap: 4px;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 6px 0;
}
.nav-pills::-webkit-scrollbar { display: none; }

.nav-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-sub);
  text-decoration: none;
  white-space: nowrap;
  transition: all 0.15s;
  border: 1px solid transparent;
}

.nav-pill:hover {
  background: var(--bg);
  color: var(--text);
}

.nav-pill.active {
  background: var(--accent-light);
  color: var(--accent);
  font-weight: 600;
  border-color: var(--accent-dim);
}

.pill-icon {
  font-size: 13px;
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

.pill-label {
  font-size: 12px;
}

.nav-tools {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding-left: 12px;
}

@media (max-width: 767px) {
  .nav-bar {
    padding: 0 16px;
    flex-wrap: wrap;
    gap: 4px;
  }
  .nav-pills { flex: 1; min-width: 0; }
  .pill-label { display: none; }
  .nav-pill { padding: 6px 10px; }
}
</style>
