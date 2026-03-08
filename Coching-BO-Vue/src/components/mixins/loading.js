
const LOADING_START_DELAY = 10; //10ms

import Promise from "bluebird";

export default {
  data() {
    return {
      
    }
  },
  computed : {
    isLoading(){
      return this.$store.state.erns.loading.isLoading;
    },
    loadingData(){
      return this.$store.state.erns.loading;
    }
  },
  methods: {
    async loading(show) {
      let loading = this.$store.state.erns.loading;
      loading.isLoading = show;
      this.$store.dispatch('erns/setLoading', loading);
      await Promise.delay(LOADING_START_DELAY);
    },
    
    async loadingSpinner(show) {
      let loading = this.$store.state.erns.loading;
      loading.isLoading = show;
      loading.mode = 'spinner';
      this.$store.dispatch('erns/setLoading', loading);      
      await Promise.delay(LOADING_START_DELAY);
    },

    async loadingProgress(show, value, max) {
      let loading = this.$store.state.erns.loading;
      loading.isLoading = show;
      loading.mode = 'progress';
      loading.progress = {
        ...loading.progress,
        value : value || 0,
        max : max || 100
      };
      this.$store.dispatch('erns/setLoading', loading);      
      await Promise.delay(LOADING_START_DELAY);
    }
  }
}
