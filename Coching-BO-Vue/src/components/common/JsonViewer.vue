<template>
  <div
    class="json-viewer-container" >
    <pre>
      <code v-html="formatJsonAdvanced(JSON.parse(jsonString))"></code>
    </pre>
  </div>
</template>

<script>
import ernsUtils from "@/components/mixins/ernsUtils";

export default {
  name: 'coching-BackOffice-JSON-Viewer',
  mixins: [ernsUtils],
  components : {
  },
  directives: {
  },
  computed : {
   
  },
  watch: {    
  },
  props: {
    jsonString:{
      type: String,
      default: "{}"
    },
    collapsed: {
      type: Boolean,
      default: false
    },
    withQuotes: {
      type: Boolean,
      default: false
    }
  },
  data(){
    return {     
      
    }
  },
  mounted(){
    const _vm = this;   
  },
  beforeDestroy(){
	},
  methods: {
    // JSON 포맷팅 메서드 (기본)
    formatJson(jsonData) {
      if (!jsonData) return '';
      
      const jsonString = JSON.stringify(jsonData, null, 2);
      
      // JSON 구문 강조를 위한 HTML 변환
      return jsonString
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, (match) => {
          let cls = 'number';
          if (/^"/.test(match)) {
            if (/:$/.test(match)) {
              cls = 'key';
            } else {
              cls = 'string';
            }
          } else if (/true|false/.test(match)) {
            cls = 'boolean';
          } else if (/null/.test(match)) {
            cls = 'null';
          }
          return '<span class="json-' + cls + '">' + match + '</span>';
        });
    },

    // 고급 JSON 포맷팅 메서드
    formatJsonAdvanced(jsonData) {
      const _vm = this;

      if (!jsonData) return '';
      
      let jsonString;
      if (_vm.withQuotes) {
        jsonString = JSON.stringify(jsonData, null, 2);
      } else {
        jsonString = JSON.stringify(jsonData, null, 2);
      }
      
      // 접기/펼치기 기능을 위한 처리
      if (_vm.collapsed) {
        jsonString = _vm.collapseJson(jsonString);
      }
      
      // JSON 구문 강조를 위한 HTML 변환
      return jsonString
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, (match) => {
          let cls = 'number';
          if (/^"/.test(match)) {
            if (/:$/.test(match)) {
              cls = 'key';
            } else {
              cls = 'string';
            }
          } else if (/true|false/.test(match)) {
            cls = 'boolean';
          } else if (/null/.test(match)) {
            cls = 'null';
          }
          return '<span class="json-' + cls + '">' + match + '</span>';
        });
    },

    // JSON 접기 기능
    collapseJson(jsonString) {
      const lines = jsonString.split('\n');
      const result = [];
      let depth = 0;
      let inString = false;
      let escapeNext = false;
      
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmed = line.trim();
        
        if (trimmed === '{' || trimmed === '[') {
          result.push(line);
          depth++;
        } else if (trimmed === '}' || trimmed === ']') {
          depth--;
          result.push(line);
        } else if (depth > 2 && (trimmed.startsWith('"') || trimmed.startsWith('{') || trimmed.startsWith('['))) {
          // 깊이가 2 이상인 경우 생략 표시
          if (i === 0 || !result[result.length - 1].includes('...')) {
            result.push('  '.repeat(depth - 1) + '...');
          }
        } else {
          result.push(line);
        }
      }
      
      return result.join('\n');
    },

    
  }
}
</script>

<style lang="scss" scoped>
  .json-viewer-container{
    max-height: 800px;
    overflow-y: auto;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    padding: 15px;
    background-color: #f8f9fa;

    pre{
      margin: 0;
      white-space: pre-wrap;
      word-wrap: break-word;
      font-family: 'Courier New', monospace;
      font-size: 13px;
      line-height: 1.4;
    }
  }
</style>