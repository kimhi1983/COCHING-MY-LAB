<template>
  <!--filter-wrap-->
  <div class="filter-wrap filter-common m-none">
    <div class="title-wrap">
      <div class="title">제품 필터</div>
      <button @click="onClickResetFilter"
        type="button" class="deselect"></button>
    </div>

    <div class="filter-check scroller">    
      <div v-for="(grpItem, grpIdx) of CODES.grpList" :key="grpIdx"
        class="item filter-acd"
        :class="{'block': grpItem.isOpen}"        
      >
        <div class="title" @click="onToggleGroupItem(grpItem)"
        >{{grpItem.name}}
          <!-- 선택된 개수 표시-->
          <span v-if="CODES[`CATE${grpItem.etc3}`].filter(item=>item.checked).length > 0">
            {{CODES[`CATE${grpItem.etc3}`].filter(item=>item.checked).length}}
          </span>
        </div>

        <div class="check-wrap">
          <div v-for="(item, itemIdx) of CODES[`CATE${grpItem.etc3}`]" 
            :key="itemIdx"
          >
            <div class="checkbox">
              <input :id="`prod-cate-checkbox-${item.etc3}`" 
                type="checkbox" 
                v-model="item.checked"
                @change="onChnagerHander"
              />
              <label :for="`prod-cate-checkbox-${item.etc3}`" class="checkbox-label">{{item.name}}</label>
            </div>
          </div>          
        </div>
      </div>

      <!-- cate1 : {{ cateList.filter(cd=>cd.etc1==1) }} -->

    </div>
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { PROD_CATES } from '@/constants/testCode';
import _ from 'lodash';

export default {
  name: 'coching-prod-filter',
  mixins: [ernsUtils],
  components: {
    
  },
  props: {
    
  },
  computed: {
      
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
    
  },
  data() {
    return {
      CODES : {
        prodCateList : [...PROD_CATES],

        grpList: [],
        list: [],
        CATE001 : [],
      },
      localData: {
        cateList : []
      }
    }
  },
  async mounted(){
    const _vm = this;

    await _vm.loadCodes();
    await _vm.fetchData();    
    _vm.docReady();

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    //fetchData
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try{
        
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    //필터초기화
    onClickResetFilter(){
      const _vm = this;

      _vm.localData.cateList.forEach(item =>{
        item.checked = false;
      });

      _vm.onChnagerHander();  
    },

    setOption(pItem, value){
      const _vm = this;

      const find = _vm.localData.cateList.find(item =>{
        return item.etc3 == pItem.etc3
      });
      if(find){
        find.checked = value;
      }
    },

    //토글
    onToggleGroupItem(grpItem){
      const _vm = this;
      
      if(grpItem.isOpen){
        grpItem.isOpen = false;
      }else{
        grpItem.isOpen = true;

        //다른 그룹은 닫는다.
        _vm.CODES.grpList.forEach(gItem => {
          if(gItem.etc3 != grpItem.etc3){
            gItem.isOpen = false;
          }
        }); 
      }
    },

    //설정값 변화 핸들러
    onChnagerHander(){
      const _vm = this;

      const values = _vm.getValue();
      _vm.$emit('update', values);
    },

    //설정된 값을 리턴
    getValue(){
      const _vm = this;

      return JSON.parse(JSON.stringify(_vm.localData));
    },

    async loadCodes(){
      const _vm = this;

      let filterList = _vm.CODES.prodCateList
        .filter(cd => cd.name != '전체')
        .filter(cd => cd.code != 2);

      filterList = _.sortBy(filterList, item => item.sortOrder);

      _vm.localData.cateList = _vm.CODES.list = [...filterList.map(item=>{
        return {
          ...item,
          checked : false,
          isOpen : false
        }
      })];
      _vm.CODES.grpList = _vm.CODES.list.filter(cd=>cd.etc1==1);
      _vm.CODES.grpList.forEach(code => {
        _vm.CODES[`CATE${code.etc3}`] = _vm.CODES.list.filter(cd=>{
          return cd.etc3.indexOf(code.etc3)==0 && cd.etc3 != code.etc3;
        });
      });
      //_vm.CODES.grpList[0].isOpen = true;
    },

    docReady(){
      const _vm = this;   

    },
  }  
}
</script>

<style lang="scss" scoped>
</style>

<style lang="scss">

</style>