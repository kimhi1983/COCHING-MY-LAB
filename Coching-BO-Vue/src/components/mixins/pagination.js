import format from 'format-number';

const fncRowNumber = (rowIdx, curPage, itemPerPage)=>{
  const cp = curPage || 1;
  const cFormat = format({});
  return `${cFormat((cp-1)*itemPerPage + (rowIdx+1))}`;
};

const fncRowNumberDesc = (rowIdx, curPage, totalItem, itemPerPage)=>{
  const cp = curPage || 1;
  const cFormat = format({});
  return `${cFormat(totalItem - ((cp-1)*itemPerPage + (rowIdx)))}`;
};

export default {
  mixins: [],
  data() {
    return {

    }
  },
  computed: {
    
  },
  filters :{
    eufRowNumberDescForPageInfo(rowIdx, pageInfo){
      return fncRowNumberDesc(rowIdx, pageInfo.curPage, pageInfo.totalItem, pageInfo.perPage);
    },
    eufRowNumberDesc(rowIdx, curPage, totalItem, itemPerPage){
      return fncRowNumberDesc(rowIdx, curPage, totalItem, itemPerPage)
    },

    eufRowNumberForPageInfo(rowIdx, pageInfo){
      return fncRowNumber(rowIdx, pageInfo.curPage, pageInfo.perPage);
    },

    eufRowNumber(rowIdx, curPage, itemPerPage){
      return fncRowNumber(rowIdx, curPage, itemPerPage)
    },
  },
  methods: {
    eumPaginationRangeForPageInfo(pageInfo){
      return this.eumPaginationRange(pageInfo.curPage, pageInfo.perPage, pageInfo.totalItem);
    },

    eumPaginationRange(curPage, itemPerPage, totalItem){
      const cp = curPage || 1;
      const start = Math.max(((cp-1) * itemPerPage) + 1, 0);
      const end = (cp) * itemPerPage;

      const cFormat = format({});
      return `${cFormat(start)} ~ ${cFormat(end)} of ${cFormat(totalItem)}`;
    }
  }
}