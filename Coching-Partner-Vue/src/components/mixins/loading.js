
const LOADING_START_DELAY = 10; //10ms

import Promise from "bluebird";

export default {
  data() {
    return {
      
    }
  },
  computed : {
    isLoading(){
      return this.$store.state.coching.loading.isLoading;
    },
    loadingData(){
      return this.$store.state.coching.loading;
    }
  },
  methods: {
    async loading(show) {
      const _vm = this;
      //_vm.loadingSpinner(show);
      _vm.loadingCoching(show);
    },

    async loadingCoching(show) {
      let loading = this.$store.state.coching.loading;
      loading.isLoading = show;
      
      if(loading.isLoading){
        //Do nothing
      }else{
        //Do nothing
      }
      
      this.$store.dispatch('coching/setLoading', loading);
      await Promise.delay(LOADING_START_DELAY);
    },
    
    async loadingSpinner(show) {
      let loading = this.$store.state.coching.loading;
      loading.isLoading = show;
      loading.mode = 'spinner';
      if(loading.isLoading){
        loading.spinner.loader = this.$loading.show({
          // Optional parameters
          container: null,
          canCancel: false
        });
      }else{
        const loader = loading.spinner.loader;
        if(loader){
          loader.hide();
        }        
      }
      
      this.$store.dispatch('coching/setLoading', loading);
      await Promise.delay(LOADING_START_DELAY);
    },

    async loadingProgress(show, value, max) {
      let loading = this.$store.state.coching.loading;
      loading.isLoading = show;
      loading.mode = 'progress';
      loading.progress = {
        ...loading.progress,
        value : value || 0,
        max : max || 100
      };
      this.$store.dispatch('coching/setLoading', loading);      
      await Promise.delay(LOADING_START_DELAY);
    }
  }
}
