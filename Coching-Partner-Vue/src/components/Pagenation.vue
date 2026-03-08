<template>
  <div class="pagenation">
    <a class="ic double-prev" href="#" @click.prevent="setPage(1)"></a>
    <a class="ic prev" href="#" @click.prevent="setPage(localData.prevPage)"></a>
    <a v-for="(pageNo, idx) of currentPageGroup"
      :key="idx"
      :class="{'active': pageNo == localData.curPage}"
      @click.prevent="setPage(pageNo, pageNo == localData.curPage)"
      >{{pageNo}}</a>
    <a class="ic next" href="#" @click.prevent="setPage(localData.nextPage)"></a>
    <a class="ic double-next" href="#" @click.prevent="setPage(localData.totalPage)"></a>
    <!-- localData:{{localData}}<br/>
    perPage:{{perPage}}<br/>
    haveNext:{{haveNext}}<br/>
    groupSize:{{groupSize}}<br/>
    currentPageGroup:{{currentPageGroup}}<br/>     -->
  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
export default {
  mixins: [ernsUtils],
  components : {
    
  },
  props:{
    value : {
      type : Number,
      default : 1
    },
    totalRows : {
      type : Number,
      default : 0
    },
    perPage : {
      type : Number,
      default : 10
    },
    groupSize : {
      type : Number,
      default : 5
    }    
  },
  
  watch : {
    value(val){
      const _vm = this;
      _vm.setPropValue();
    },
    totalRows(val){
      const _vm = this;
      _vm.setPropValue();
    },
    perPage(val){
      const _vm = this;
      _vm.setPropValue();
    },
  },
  
  data() {
    return {
      localData :{
        curPage : 1,
        totalRows : 0,

        prevPage : 1,
        nextPage : 1,

        totalPage : 1,        
      },

      havePrev : false,
      haveNext : false,
      currentPageGroup : [],      
    }
  },
  
  mounted(){
    const _vm = this;
    
    _vm.setPropValue();
  },
  
  methods : {
    setPropValue(){
      const _vm = this;

      _vm.localData.curPage = _vm.value >= 0 ? _vm.value : 1;
      _vm.localData.totalRows = _vm.totalRows >= 0 ? _vm.totalRows : 0;

      _vm.calculate();
    },

    setPage(pageNo, isEqualsWithCurrentPage){
      if(isEqualsWithCurrentPage){
        return;        
      }

      const _vm = this;
      const lData = _vm.localData;
      const oldVal = lData.curPage;

      lData.curPage = pageNo;
      _vm.calculate();

      if(oldVal != pageNo){
        _vm.$emit('input', lData.curPage);
      }
    },

    calculate(){
      const _vm = this;
      const lData = _vm.localData;

      const mod = lData.totalRows == 0 ? 0 : lData.totalRows % _vm.perPage;
      const quotient = lData.totalRows == 0 ? 0 : (lData.totalRows - mod) / _vm.perPage;

      //console.debug(mod);
      //console.debug(quotient);

      lData.totalPage = lData.totalRows == 0 ? 1 : quotient + (mod == 0 ? 0 : 1);

      if(lData.totalPage < lData.curPage){
        lData.curPage = lData.totalPage;
      }

      lData.prevPage = lData.curPage <= 1 ? 1 : lData.curPage -1;
      lData.nextPage = lData.curPage >= lData.totalPage ? lData.totalPage : lData.curPage +1;

      const h = Math.floor(_vm.groupSize / 2);
      let sIdx = lData.curPage - (_vm.groupSize-1);
      let eIdx = lData.curPage + (_vm.groupSize-1);
      
      _vm.currentPageGroup = [];
      for(let p=sIdx; p <= eIdx ; p++){
        _vm.currentPageGroup.push(p);
      }

      _vm.currentPageGroup = _vm.currentPageGroup.filter(item=>{
        return item >=1 && item <=lData.totalPage;
      });

      while(_vm.currentPageGroup.length > _vm.groupSize){
        let pIdx = 0;
        for(let i=0 ; i<_vm.currentPageGroup.length; i++){
          if(_vm.currentPageGroup[i] == lData.curPage){
            pIdx = i;
            break;
          }
        }

        const pv = pIdx / _vm.currentPageGroup.length;
        //console.debug(`pv:${pv}`);
        if(pv < 0.5){
          _vm.currentPageGroup.pop();          
        }else{
          _vm.currentPageGroup.splice(0, 1);    
        }
      }
      
      _vm.havePrev = _vm.currentPageGroup[0] != lData.curPage;
      _vm.haveNext = _vm.currentPageGroup[_vm.currentPageGroup.length -1] != lData.curPage;
    }
  }
}
</script>

<style lang="scss" scoped>
  
</style>

<style lang="scss">
  
</style>