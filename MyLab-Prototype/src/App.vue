<template>
  <div class="app-layout">
    <AppSidebar :collapsed="sidebarCollapsed" @toggle="sidebarCollapsed = !sidebarCollapsed" />
    <div class="app-main" :class="{ collapsed: sidebarCollapsed }">
      <AppHeader />
      <main class="app-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppSidebar from './components/layout/AppSidebar.vue'
import AppHeader from './components/layout/AppHeader.vue'

const sidebarCollapsed = ref(false)
</script>

<style>
:root {
  --bg: #f8f7f5;
  --surface: #ffffff;
  --sidebar: #fafaf8;
  --border: #ece9e3;
  --border-mid: #d8d4cc;
  --accent: #b8935a;
  --accent-light: #f0e8d8;
  --accent-dim: #e8dece;
  --text: #1a1814;
  --text-sub: #6b6560;
  --text-dim: #aba59d;
  --green: #3a9068;
  --green-bg: #f0f8f4;
  --red: #c44e4e;
  --red-bg: #fdf2f2;
  --amber: #b07820;
  --amber-bg: #fdf8f0;
  --blue: #3a6fa8;
  --blue-bg: #f0f4fb;
  --purple: #7c5cbf;
  --purple-bg: #f6f2fd;
  --radius: 10px;
  --radius-lg: 16px;
  --shadow: 0 1px 4px rgba(0,0,0,0.04);
}

.app-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg);
}

.app-main {
  flex: 1;
  margin-left: 210px;
  transition: margin-left 0.3s ease;
}

.app-main.collapsed {
  margin-left: 60px;
}

.app-content {
  padding: 32px 36px;
  max-width: 1400px;
}

@media (max-width: 1199px) {
  .app-main { margin-left: 60px; }
  .app-content { padding: 24px 20px; }
}

@media (max-width: 767px) {
  .app-main { margin-left: 0; }
  .app-content { padding: 16px; }
}
</style>
