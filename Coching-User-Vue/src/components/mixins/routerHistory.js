
export default {
  data() {
    return {
      
    }
  },
  computed : {
    logRouteChange(){
      return this.$store.state.coching.logRouteChange || {
        from : null,
        to : null
      };
    },

    currentLogRoute() {
      const _vm = this;
      const lrc = _vm.logRouteChange ;
      return lrc.to || {};
    },
  },
  methods: {
    eumGetRouteHistoryParam() {
      const _vm = this;
      const routerName = _vm.$route.name;
      const map = this.$store.state.coching.routerHistoryMap;
      if(!map || !map.hasOwnProperty(routerName)){
        return null;
      }
      
      return map[routerName];
    },
    eumSetRouteHistoryParam(data) {
      const _vm = this;
      const routerName = _vm.$route.name;
      if(!routerName || !data){
        return;
      }

      data.routerName = routerName;
      this.$store.dispatch('coching/setRouterHistoryMap', {key:routerName ,value:data});
    },     
  }
}
