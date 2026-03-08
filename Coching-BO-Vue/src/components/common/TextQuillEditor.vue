<template>
  <div>
    <quillEditor
      ref="quillEditor"
      v-model="localValue"
      :options="editorOptions"
      :style="`height: ${height}`"
    />
  </div>
</template>

<script>
import { quillEditor } from 'vue-quill-editor'

export default {
  name: 'TextQuillEditor',
  components: { quillEditor },
  props: {
    value: {
      type: String,
      default: ''
    },
    imageUploadHandler: {
      type: Function,
      required: true
    },
    placeholder: {
      type: String,
      default: '내용을 입력하세요...'
    },
    height: {
      type: String,
      default: '400px'
    }
  },
  data() {
    return {
      localValue: this.value,
      editorOptions: {
        placeholder: this.placeholder,
        theme: 'snow',
        modules: {
          toolbar: {
            container: [
              ['bold', 'italic', 'underline', 'strike'],
              [{ header: [1, 2, 3, 4, 5, 6, false] }],
              [{ list: 'ordered' }, { list: 'bullet' }],
              [{ indent: '-1' }, { indent: '+1' }],
              [{ color: [] }, { background: [] }],
              [{ font: [] }],
              [{ align: [] }],
              ['link', 'image', 'video'],
              ['clean']
            ],
            handlers: {
              image: async () => {
                const input = document.createElement('input');
                input.setAttribute('type', 'file');
                input.setAttribute('accept', 'image/png, image/jpeg');
                input.click();
                input.onchange = async () => {
                  const file = input.files[0];
                  if (!file) return;
                  const MAX_SIZE_MB = 10 * 1024 * 1024;
                  if (file.size > MAX_SIZE_MB) {
                    alert('파일 크기는 10MB 이하만 업로드할 수 있습니다.');
                    return;
                  }
                  if (!['image/png', 'image/jpeg'].includes(file.type)) {
                    alert('PNG 또는 JPEG 형식의 이미지만 업로드 가능합니다.');
                    return;
                  }
                  try {
                    const imageUrl = await this.imageUploadHandler(file);
                    if (imageUrl) {
                      const quill = this.$refs.quillEditor.quill;
                      const range = quill.getSelection();
                      quill.insertEmbed(range.index, 'image', imageUrl);
                    }
                  } catch (error) {
                    alert('이미지 업로드 중 오류가 발생했습니다.');
                  }
                };
              }
            }
          }
        }
      }
    };
  },
  mounted() {
    this.$nextTick(() => {
      const toolbar = this.$el.querySelector('.ql-toolbar');
      const container = this.$el.querySelector('.ql-container');
      if (toolbar && container) {
        const toolbarHeight = toolbar.offsetHeight;
        container.style.height = `calc(100% - ${toolbarHeight}px)`;
        container.style.maxHeight = `calc(${this.height} - ${toolbarHeight}px)`;
        container.style.overflowY = 'auto';
      }
    });
  },
  watch: {
    value(val) {
      this.localValue = val;
    },
    localValue(val) {
      this.$emit('input', val);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "~quill/dist/quill.core.css";
@import "~quill/dist/quill.snow.css";
@import "~quill/dist/quill.bubble.css";
</style> 