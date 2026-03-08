<template>
  <div>
    <quillEditor
      :options="snowOption"
      contentType="html"
      id="bi-form-content"     
      placeholder="내용을 입력하세요."
      v-model="content"
      rows="10"
      max-rows="20"
      style="height: 400px;"
      ref="quillEditor"
      @input="onInputText"
    />  
      <input type="file" style="display:none" 
        id="quillFileInput" 
        ref="quillFileInput"
        accept="image/*" multiple
        @change="onChangeFiles"
      />
  </div>
</template>
<script>
import {imageUpload} from '@/api/coching-bo/comm/board';

import { required } from '@validations';

import Ripple from 'vue-ripple-directive'
import ernsUtils from '@/components/mixins/ernsUtils';

import vSelect from 'vue-select';
import { quillEditor } from 'vue-quill-editor'

const toolbarOptions = [
  [{ 'size': ['small', false, 'large', 'huge'] }],  
  ['bold', 'italic', 'underline', 'strike'],        
  ['blockquote', 'code-block'],
  [{ 'list': 'ordered'}, { 'list': 'bullet' }],
  // [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
  [{ 'indent': '-1'}, { 'indent': '+1' }],          
  [{ 'direction': 'rtl' }, { 'align': [] }],        
  [{ 'color': [] }, { 'background': [] }],          
  ['link', 'image'],
  ['clean']                                         
];
export function imageHandler()  {
  const _vm = this;
  document.getElementById("quillFileInput").click();
};

export default {
  name: 'coching-BackOffice-Cm-Notice-Form',
  mixins: [ernsUtils],
  components : {
    vSelect, required, quillEditor
  },
  directives: {
    Ripple
  },
  computed : {
   
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
    editorText(){
      const _vm = this;
      const text = _vm.editorText;
      text.replace(/&lt;/g, '<').replace(/&gt;/g, '>');
      _vm.content = text;
    }
  },
  props: {
    editorText: {
      type: String,
      default: ''
    },
    boardType: {
      type: String,
      default: ''
    }
  },
  data(){
    return {     
      content: '',
      snowOption: {
        theme: 'snow',
        modules: {
          toolbar: {
            container: [...toolbarOptions],
            handlers: {
              image: imageHandler,
            },
          },
        },
      },
      editorImages: [],
    }
  },
  mounted(){
    const _vm = this;
   
  },
  beforeDestroy(){
	},
  methods: {
    async onChangeFiles(event) {
      const _vm = this;
      const input = event.target;
      const count = input.files.length;

      // 파일저장 
      const files = event.target.files;
      const formData = new FormData();

      formData.append("boardMstId", _vm.boardType);
      formData.append("b_img_files_"+count, files[0]);
      
      const res = await imageUpload(formData);
      
      if(res.resultData.fileId == null || res.resultData.fileId == "") {
         _vm.alertError("이미지 업로드에 실패했습니다. 다시 시도해 주세요.");
         return;
      }

      const retFileData = res.resultData;
      const fileId = retFileData.fileId;

      _vm.editorImages.push(fileId);

      // 업로드한 이미지 출력
      const quill = _vm.$refs.quillEditor.quill; 
      const index = quill.getSelection().index;
      const src = _vm.eumFileImagePath(fileId, "0");
      if(count) {
        const reader = new FileReader();
        reader.onload = (e) => {
          quill.insertEmbed(index, 'image', src);
          quill.setSelection(index + 1);
          
        }
        reader.readAsDataURL(event.target.files[0]);
      }

    },

    onInputText(text) {
      const _vm = this;
      const data = {};
      data.content = text;
      data.fileIds = [..._vm.editorImages]
      _vm.$emit("input", data);
    }
  }
}
</script>

<style lang="scss" scoped>
  .test-class{
    padding-top:1.80rem;
  }
</style>

<style lang="scss">
@import '@core/scss/vue/libs/quill.scss';

blockquote:before, blockquote:after, q:before, q:after {
  content: '';
}
blockquote:before, blockquote:after, q:before, q:after {
  content: '';
}
</style>
