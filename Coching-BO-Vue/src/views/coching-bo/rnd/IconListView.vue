<template>
  <div id="icon-list-view" class="container-fluid">
    <b-card no-body>
      <b-card-body>
        <b-row class="mb-2">
          <b-col md="6" lg="5">
            <b-form-group label="아이콘 검색" label-for="icon-search-text">
              <b-input-group size="sm">
                <b-form-input id="icon-search-text" v-model="searchText" placeholder="아이콘 이름 검색 (예: search, TrashIcon)" @keyup.enter="onSearch" />
                <b-input-group-append>
                  <b-button 
                    size="sm" 
                    variant="outline-secondary" 
                    @click="onClear"
                    v-b-tooltip.hover
                    title="검색어 초기화"
                  >
                    <feather-icon icon="XIcon" size="16" />
                  </b-button>
                  <b-button 
                    size="sm" 
                    variant="outline-primary" 
                    @click="onSearch"
                    v-b-tooltip.hover
                    title="검색"
                  >
                    <feather-icon icon="SearchIcon" size="16" />
                  </b-button>                  
                </b-input-group-append>
              </b-input-group>
            </b-form-group>
          </b-col>
          <b-col md="3" lg="2" class="ml-auto">
            <b-form-group label="아이콘 크기" label-for="icon-size-input">
              <b-form-input id="icon-size-input" v-model.number="iconSize" type="number" min="10" max="128" size="sm" />
            </b-form-group>
          </b-col>
        </b-row>

        <div class="d-flex justify-content-between align-items-center mb-1">
          <small class="text-secondary">총 {{ filteredIcons.length }}개</small>
          <small class="text-muted">* 아이콘을 클릭하면 이름이 복사됩니다. / 코드 예시: <code>&lt;feather-icon icon="[NameOfIcon]" :size="[iconSizeStr]" /&gt;</code></small>
        </div>

        <div class="feather-icon-grid">
          <div 
            v-for="name in filteredIcons" 
            :key="name" 
            class="feather-icon-item" 
            @click="copyName(name)"
            v-b-tooltip.hover
            :title="`클릭하여 ${name} 복사`"
          >
            <component :is="getIconComponent(name)" :size="iconSizeStr" />
            <div class="feather-icon-name">{{ name }}</div>
          </div>
        </div>
      </b-card-body>
    </b-card>
  </div>
  
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";
import cochingUtils from "@/components/mixins/cochingUtils";
import * as FeatherIcons from 'vue-feather-icons';

export default {
  name: 'IconListView',
  mixins: [ernsUtils, cochingUtils],
  data(){
    const _vm = this;
    const allIconNames = Object.keys(FeatherIcons)
      .filter(k => k.endsWith('Icon'))
      .sort((a,b)=> a.localeCompare(b));
    return {
      allIconNames,
      searchText: '',
      iconSize: 24,
    };
  },
  computed: {
    filteredIcons(){
      const text = (this.searchText || '').trim();
      if(!text) return this.allIconNames;
      const lower = text.toLowerCase();
      return this.allIconNames.filter(n => n.toLowerCase().includes(lower));
    },
    iconSizeStr(){
      return String(this.iconSize || 24);
    }
  },
  methods:{
    onSearch(){ /* 단순히 computed 가 반응 */ },
    onClear(){ this.searchText = ''; },
    getIconComponent(name){
      return FeatherIcons[name] || 'div';
    },
    async copyName(name){
      try{
        await navigator.clipboard.writeText(name);
        this.$toast && this.$toast.success(`${name} 복사됨`);
      }catch(err){
        console.error(err);
        this.$toast && this.$toast.error('클립보드 복사 실패');
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.feather-icon-grid{
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  grid-gap: 12px;
}
.feather-icon-item{
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 8px;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color .15s ease, box-shadow .15s ease;
}
.feather-icon-item:hover{
  background: #f8f9fa;
  box-shadow: 0 1px 2px rgba(0,0,0,0.06);
}
.feather-icon-name{
  margin-top: 6px;
  font-size: 12px;
  color: #495057;
  word-break: break-all;
  text-align: center;
}
</style>