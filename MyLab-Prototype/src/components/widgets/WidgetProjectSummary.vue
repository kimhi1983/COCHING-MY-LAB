<template>
  <div class="project-widget">
    <div v-if="projectList.length" class="project-list">
      <div class="proj-item" v-for="p in projectList" :key="p.id">
        <div class="proj-top">
          <span class="proj-name">{{ p.name }}</span>
          <span class="proj-count">{{ p.formulaCount }}건</span>
        </div>
        <div class="proj-bar-wrap">
          <div class="proj-bar" :style="{ width: p.progress + '%' }"></div>
        </div>
        <div class="proj-stats">
          <span>완료 {{ p.doneCount }}</span>
          <span>진행 {{ p.reviewCount }}</span>
          <span>초안 {{ p.draftCount }}</span>
        </div>
      </div>
    </div>
    <div v-else class="empty">프로젝트가 없습니다</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useProjectStore } from '../../stores/projectStore.js'

const { allProjects } = useProjectStore()
const projectList = computed(() => allProjects().slice(0, 5))
</script>

<style scoped>
.project-list { overflow-y: auto; max-height: 100%; }
.proj-item { padding: 10px 12px; border-bottom: 1px solid var(--border); }
.proj-item:last-child { border-bottom: none; }
.proj-top { display: flex; justify-content: space-between; margin-bottom: 6px; }
.proj-name { font-size: 12px; font-weight: 600; color: var(--text); }
.proj-count { font-size: 10px; font-family: monospace; color: var(--text-dim); }
.proj-bar-wrap { height: 3px; background: var(--border); border-radius: 99px; overflow: hidden; margin-bottom: 4px; }
.proj-bar { height: 100%; background: var(--green); border-radius: 99px; transition: width 0.3s; }
.proj-stats { display: flex; gap: 10px; font-size: 9px; color: var(--text-dim); }
.empty { text-align: center; color: var(--text-dim); font-size: 12px; padding: 24px; }
</style>
