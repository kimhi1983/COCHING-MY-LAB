<template>
  <div>
    <b-card no-body >
      <b-card-body class="sys-banner-filter"
        title="썸네일">
        <b-row align-content="center">
            <b-col cols="12" md="4"
              class="mb-md-0 mt-3 px-5"
              >
              <b-card
                class="fileInput"
                img-alt="Card image cap"
                img-top
                tag="article"
                @click="$refs.inputFile.click()"
              >
              +
              </b-card>
            </b-col> 
            <b-col cols="12" md="4"
              class="mb-md-0 mt-3 px-5"
              >
              <b-card
                class="fileInput"
                img-alt="Card image cap"
                img-top
                tag="article"
                @click="$refs.inputFile.click()"
              >
              </b-card>
            </b-col> 
            <b-col cols="12" md="4"
              class="mb-md-0 mt-3 px-5"
              >
              <b-card
                class="fileInput"
                img-alt="Card image cap"
                img-top
                tag="article"
                @click="$refs.inputFile.click()"
              >
              </b-card>
            </b-col>  

            <template v-if="previewList.length">
              <b-col cols="12" md="4"
              class="mb-md-0 mt-3 px-5"
              v-for="(item, index) in previewList" :key="index"
              >
                <b-card
                  class="imagePreview"
                  :img-src="item"
                  img-alt="Card image cap"
                  img-top
                  tag="article"
                  @click="$refs.inputFile.click()"
                >
                {{item}}
                </b-card>
                <p class="mb-0">file name: {{ imageList[index].name }}</p>
                <p>size: {{ imageList[index].size/1024 }}KB</p>
              </b-col>
            </template>
        </b-row>
      </b-card-body>
      
      <b-card-body>
        <b-card class="px-3">
          <input 
            type="file"
            ref="inputFile"
            accept="image/*"
            @change="onChangeMultiImages" 
            multiple/>
        </b-card>
        <!--// 버튼 영역 -->
        <div class="text-center mt-4 mb-3">
          <b-button
            v-ripple.400="'rgba(113, 102, 240, 0.15)'"
            variant="primary"
            class="mr-1"
            @click.prevent="onClickSave"
          > 저장
          </b-button>
        </div>
        <!-- // 버튼 영역 -->
      </b-card-body>
    </b-card>  
 
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'

export default {
  name: '',
  mixins: [ernsUtils],  
  components : {
    
  },
  props: {

  },
  directives: {
    Ripple
  },
  watch: {
    
  },
  data(){
    return {
      file: null,
      images: [],
      imageData: "",
      imageList: [],
      previewList: []
    }
  },
  mounted(){
    const _vm = this;    
    
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    onSearch(){
      const _vm = this;      
    },

    onClickSave() {

    },

    onClickCancel() {

    },
    
    onClickImgUpload() {

    },

    onChangeImages(event) {
      const _vm = this;
      console.log(event.target.files)
      if(event.target.files) {
        const reader = new FileReader();

        reader.onload = (e) => {
          _vm.imageData = e.target.result;
        }

        reader.readAsDataURL(event.target.files[0]);
      }
    },

    onChangeMultiImages(event) {
      const _vm = this;
      const input = event.target;
      const count = input.files.length;

      // 파일수 체크 다시 
      if(_vm.previewList.length > 2) {
        _vm.alertError(`이미지는 3장까지 업로드할 수 있습니다.`);
        return false;
      }

      for(let ii = 0; ii < input.files.length; ii++) {
        const reader = new FileReader();
        reader.onload = (e) => {
          _vm.previewList.push(e.target.result);
        }
        _vm.imageList.push(event.target.files[ii]);
        reader.readAsDataURL(event.target.files[ii]);
        
      }
    },

    // reset function
  }  
}
</script>

<style lang="scss" scoped>
.fileInput {
  border: 0;
  border: 5px dotted #7367f0;
  height: 200px;
  line-height: 150px;
  text-align: center;
  font-size: 50px;
  color: #7367f0;
  padding: 0;
}

.imagePreview :hover{
  cursor: pointer;
}
</style>