<template>
  <b-card>
    <b-row>
      <b-col cols="12" class="mb-2">
        <div class="text-right">
          <b-button @click="onclickReset" variant="primary">
            초기화
          </b-button>
        </div>
      </b-col>
      
      <b-col cols="12">
        <b-card no-body border-variant="primary" class="p-4">
          <draggable v-model="imageData" tag="div">
            <div
              class="image-contents"
              v-for="(item, idx) of imageData" 
              :key="item.fileId"
            > 
              <b-img class="image-box"
                :src="eumFileImagePathWithSize(item.fileId, '1', 'S')"></b-img>
              <input type="hidden" :value="item.newOrder = (idx+1)">  
            </div>     
          </draggable>
        </b-card>
      </b-col>
    </b-row>
  </b-card>  
</template>

<script>
import { setFileOrder } from '@/api/coching-bo/comm/file';

import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'
import draggable from 'vuedraggable'

export default {
  name: '',
  mixins: [ernsUtils],  
  components : {
    draggable
  },
  props: {
    rawImageData: {
      type: Array,
      default: function() {
        return new Array();
      }
    }
  },
  directives: {
    Ripple
  },
  watch: {
    // rawImageData() {
    //   const _vm = this;
    //   console.log("_vm.rawImageData", _vm.rawImageData)
    //   _vm.imageData = [..._vm.rawImageData];

    // }
  },
  data(){
    return {
      imageData: []
    }
  },
  mounted(){
    const _vm = this;    
    _vm.imageData = [..._vm.rawImageData];
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    onSearch(){
      const _vm = this;      
    },

    getValue() {
      const _vm = this;
      return _vm.imageData;
    },

    onclickReset() {
      const _vm = this;
      _vm.imageData = [..._vm.rawImageData];
    },

    async saveOrder() {
      const _vm = this;

      const params = _vm.imageData.map(item=>{
        return {
          ...item,
          fileOrder: item.newOrder
        }
      });
      
      // 사진이 한 장만 있을 때
      if(params.length <= 1) {
        return;
      } 
      
      const result = await _vm.$swal({
        title: '확인',
        text: "파일 순서를 변경하시겠습니까?",
        showCancelButton: true,
        confirmButtonText: '저장',
        customClass: {
          confirmButton: 'btn btn-danger ml-1',
          cancelButton: 'btn btn-outline-primary',
        },
        buttonsStyling: false,
      });
      
      if(!result.value) {
        return;
      }
      _vm.loading(true);
      try {
        await setFileOrder(params);
        _vm.alertSuccess(`사진 순서가 변경되었습니다.`);
        _vm.$emit("saveOrderData", params);

      } catch (err) {
        _vm.alertError(err);
      } finally {
        _vm.loading(false);
      }
      
    },

  }  
}
</script>

<style lang="scss" scoped>
.image-contents {
  float: left;
  margin-right: 1em;
  margin-bottom: 1em;
}
.image-box {
  width: 115px;
  height: 115px;
}

</style>