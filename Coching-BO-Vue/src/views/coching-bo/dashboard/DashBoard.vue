<template>
  <section id="dashboard-coching-bo">
    
    대시보드

  </section>
</template>
<script>
import {getCodes} from '@/api/coching-bo/comm/code';

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive';

export default {
  name: 'coching-BackOffice-DashBoard',
  mixins: [ernsUtils],
  components : {
    
  },
  directives: {
    Ripple
  },
  props: {

  },
  filters :{
    
  },
  computed: {
    showRows(){
      return this.$store.state.erns.showRows;
    }  
  },
  data(){
    return {
      dashboardData: {
      }
    }
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData'
  },
  mounted(){
    const _vm = this;
    _vm.fetchData();
    
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    async loadData(){
      const _vm = this;
      
      _vm.loading(true);

      const params = {};
      try {
       

      } catch (err) {
        _vm.alert(err);  
      } finally {
        _vm.loading(false);
      }
    },

    fetchData() {
      const _vm = this;
      _vm.loadData();
    }
  }  
}
</script>

<style lang="scss" scoped>

</style>
<style lang="scss">
.sys-vehicle-filter1{
  .v-select{
    .vs__dropdown-toggle{
      height: 2.142rem;
    }
  }
}
</style>
