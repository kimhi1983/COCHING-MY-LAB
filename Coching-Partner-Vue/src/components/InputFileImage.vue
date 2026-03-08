<template>
  <div>
    <label :for="id"></label>
    <div class="file-img-wrap">
      <div>
        <input type="file" class="files" 
          :name="inputName" :id="id" :accept="accept" ref="rawFileInput"
          :multiple="{'multiple': multiple}"
          @change="onChangeImages"/>
      </div>
      <div v-if="localData.file" class="pip">
        <div class="pip-wrap">
          <img class="imageThumb" 
            :src="localData.dataUrl" :title="localData.file.name" :alt="localData.file.name"/>
          <div class="remove" @click.prevent="onClickRemove"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
export default {
  name: "Erns-Image-File-Input",
  mixins: [ernsUtils],
  components : {
    
  },
  props:{
    capture : {
      type: Boolean,
      default : true
    },
    value : {
      type : File,
      default : null,
    },
    multiple: {
      type: Boolean,
      default : true
    },
    id:{
      type: String,
      default : "file_"+new Date().getTime()
    },
    inputId : {
      type: String,
      default : "file_"+new Date().getTime()
    },
    inputName: {
      type: String,
      default : "file_"+new Date().getTime()
    },
    accept:{
      type: String,
      default : "image/x-png,image/gif,image/jpeg"
    }
    
  },  
  watch : {
    value(val){
      const _vm = this;
      _vm.onSetValue(val);
    },
    localValue() {

    },
  },  
  data() {
    return {
      localData :{
        file : null,
        dataUrl : null
      }
    }
  },
  
  mounted(){
    const _vm = this;
    
  },
  
  methods : {
    async onChangeImages(event) {
      const _vm = this;

      const files = event.target.files;

      if(!files || files.length <= 0){
        _vm.onSetValue(null);
        return;
      }

      //사진회전처리      
      const cFiles = [];
      for(let rFile of files){
        const imgData = await _vm.eumLoadImage(rFile);
        cFiles.push(imgData.newFile);
      }

      if(cFiles && cFiles[0]) {
        _vm.onSetValue(cFiles[0]);
        //console.debug(_vm.localData);
      }

      if(cFiles && cFiles.length > 1){
        const others = [];
        for(let idx=1; idx < cFiles.length ; idx++){
          others.push(cFiles[idx]);
        }        
        _vm.$emit('multipleFiles', others);        
      }
    },    
    onClickRemove(){
      const _vm = this;
      _vm.onSetValue(null);
      _vm.localData.dataUrl = null;

      //console.debug(_vm.$refs.rawFileInput);
      _vm.$refs.rawFileInput.value = null;
    },
    onSetValue(pFile){
      const _vm = this;
      _vm.localData.file = pFile; 

      if(pFile){
        const reader = new FileReader();
        const file = pFile;
        reader.onload = (e) => {
          _vm.localData.dataUrl = e.target.result;
        }
        reader.readAsDataURL(file);
      }

      _vm.$emit('input', _vm.localData.file);
    },
    getFile(){
      const _vm = this;

      return _vm.localData.file;
    }
  }
}
</script>

<style lang="scss" scoped>
  
</style>

<style lang="scss">
  
</style>