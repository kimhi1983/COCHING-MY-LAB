<template>
	<b-card no-body class="pip">
		<b-aspect aspect="1:1" class="pip-wrap">
			<b-card-img
				:blank-src="blankImg"              
				:src="localData.dataUrl || blankImg" 
				:title="localData.file.name" 
				:alt="localData.file.name"
				@click.prevent="onClickeImage"
			/>		
    </b-aspect>
		<b-card-body v-if="localData.file.name">
			<b-card-sub-title>
				{{localData.file.name}}
				<b-icon icon="x-circle" variant="danger" 
					@click.prevent="onClickRemove"
					scale="1.5" class="ml-1 delButton"></b-icon> 				
			</b-card-sub-title>			
		</b-card-body> 		
		<input type="file" hidden :accept="accept" ref="rawFileInput"
        :multiple="{'multiple': multiple}"
				@change="onChangeImages"/>
	</b-card>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';

const DEFAULT_IMAGE = require('@/assets/images/erns/no-image.png');
const DEFAULT_LOCAL_DATA = {
	file : {
		name : null
	},
	dataUrl : DEFAULT_IMAGE
}

export default {
  name: "Erns-Image-File-Input",
  mixins: [ernsUtils],	
  components : {
    
  },
  props:{
    value : {
      type : [File, Object],
      default : null,
    },
    multiple: {
      type: Boolean,
      default : true
    },       
    accept:{
      type: String,
      default : "image/x-png,image/gif,image/jpeg"
    },
		maxHeight:{
			type: Number,
			default : 100
		},
		maxWidth:{
			type: Number,
			default : 100
		}		    
  }, 
	computed:{
		blankImg(){
			return DEFAULT_IMAGE;
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
				...DEFAULT_LOCAL_DATA
      }
    }
  },
  
  mounted(){
    const _vm = this;
    
  },
  
  methods : {
		onClickeImage(event){
			const _vm = this;
			_vm.$refs.rawFileInput.click();
		},

    onChangeImages(event) {
      const _vm = this;

      const files = event.target.files;

      if(files && files[0]) {
        _vm.onSetValue(files[0]);
        //console.debug(_vm.localData);
      }else{
        _vm.onSetValue(null);
      }

      if(files && files.length > 1){
        const others = [];
        for(let idx=1; idx < files.length ; idx++){
          others.push(files[idx]);
        }        
        _vm.$emit('multipleFiles', others);        
      }
    },    
    onClickRemove(){
      const _vm = this;

			_vm.$emit('onRemove', _vm.localData.file);        

      _vm.onSetValue(null);
      _vm.localData.dataUrl = null;

      //console.debug(_vm.$refs.rawFileInput);
      _vm.$refs.rawFileInput.value = null;			
    },
    onSetValue(pFile){
      const _vm = this;

			if(!pFile){
				_vm.localData.file = {
					name : null
				}; 
			}

			if(pFile instanceof File){
				const reader = new FileReader();
				const file = pFile;
				reader.onload = (e) => {
					_vm.localData.dataUrl = e.target.result;
				}
				reader.readAsDataURL(file);
				_vm.localData.file = pFile; 
				
				_vm.$emit('input', _vm.localData.file);
			}else if(pFile instanceof Object){
				if(pFile.fileId){
					const url = _vm.eumFileImagePath(pFile.fileId);        
					_vm.localData.dataUrl = url;
					_vm.localData.file = {
						..._vm.localData.file,
						name : pFile.fileName,
						...pFile
					};
				}
			}
    },
    getFile(){
      const _vm = this;

      return _vm.localData.file;
    }
  }
}
</script>

<style lang="scss" scoped>
	.pip {
		background: #fff;
	}
  .pip-wrap{
		overflow: hidden;
    display: block;
    position: relative;
    width: 100%;
	}
	.pip img {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    transition: .35s;
    -o-transition: .35s;
    -moz-transition: .35s;
    -webkit-transition: .35s;
    height: 100%;
	}
	.delButton{
		cursor: pointer;
	}
</style>

<style lang="scss">
  
</style>