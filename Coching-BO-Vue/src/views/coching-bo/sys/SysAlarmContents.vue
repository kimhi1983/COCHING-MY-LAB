<template>
  <b-card border-variant="primary" class="p-2">
    <b-table-simple
      class="position-relative"
      responsive striped
      show-empty
      empty-text="데이터가 없습니다."
    >
      <b-thead>
        <b-tr>
          <b-th v-for="(item, idx) of data.thData" :key="'th_'+idx" class='text-center'>{{item}}</b-th>
        </b-tr>
      </b-thead> 
      <b-tbody>
        <b-tr v-for="(item, idx) of data.contents" :key="'tr_'+idx">
          <b-td v-for="(value, idx2) in item" :key="'td_'+idx2" v-html="$options.filters.eufmtNewLineToBr(value)"></b-td>
        </b-tr>
      </b-tbody>
    </b-table-simple>
  </b-card>
</template>
<script>
import { required } from '@validations';

import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';
import ToastificationContent from '@core/components/toastification/ToastificationContent.vue'

export default {
  name: 'coching-System-Terms-Contents',
  mixins: [ernsUtils],
  components : {
    ToastificationContent
  },
  directives: {
    Ripple
  },
  props: {
    alramData: {
      type: Object,
      default: function() {
        return {};
      }
    },
  },
  watch: {
    
  },
  data(){
    return {
      data: {
        thData: [],
        contents: []
      }
    }
  },
  mounted(){
    const _vm = this;    
    _vm.setValue();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    setValue(){
      const _vm = this;    
      _vm.data.thData = _vm.alramData.thData;
      _vm.data.contents = _vm.alramData.contents;

      console.log(_vm.data)
    },
 
  }  
}
</script>

<style lang="scss" scoped>

</style>