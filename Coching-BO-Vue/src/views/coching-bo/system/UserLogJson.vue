<template>
  <b-container fluid>
    <b-form-group 
      :label="json1Label"
      label-for="userLog-log1">
      <b-form-textarea id="userLog-log1"
        v-model="localData.logJsonStr"
        rows="3" max-rows="30" readonly
      ></b-form-textarea>
    </b-form-group>
    <b-form-group v-show="localData.logJson2"
      :label="json2Label"
      label-for="userLog-log2">
      <b-form-textarea id="userLog-log2"
        v-model="localData.logJson2Str"
        rows="3" max-rows="30" readonly  
      ></b-form-textarea>
    </b-form-group>
    <!-- localData:{{ localData }} -->
  </b-container>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';

const DEF_JSON_OBJ = {
  logJsonStr: '',
  logJson2Str: '',
}
export default {
  name: 'coching-BackOffice-System-UserLog',
  mixins: [ernsUtils],
  components : {
  },
  props: {
    data: {
      type: Object,
      default: function() {
        return {...DEF_JSON_OBJ}
      }
    },
    json1Label: {
      type: String,
      default: 'JSON1'
    },
    json2Label: {
      type: String,
      default: 'JSON2'
    }
  },
  computed : {
  
  },
  data(){
    return {
      localData: {}
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
    setValue() {
      const _vm = this; 
      _vm.localData = {..._vm.data};
      
      if(_vm.localData.logJson) {
        _vm.localData.logJsonStr =  JSON.stringify(JSON.parse(_vm.localData.logJson), null, 2);

      }
      if(_vm.localData.logJson2) {
        _vm.localData.logJson2Str = JSON.stringify(JSON.parse(_vm.localData.logJson2), null, 2);
      }
    }
  }  
}
</script>

<style lang="scss" scoped>

</style>