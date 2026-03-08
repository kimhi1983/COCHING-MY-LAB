<template>
  <!--filter-wrap-->
  <div class="filter-wrap filter-common m-none">
    <div class="title-wrap">
      <div class="title">원료 필터</div>
      <button @click="onClickResetFilter"
        type="button" class="deselect"></button>
    </div>
      <div class="filter-check scroller">
      <!--checkbox id값 변경하세요-->
      <div class="item">
        <div class="check-wrap">
          <div>
            <div class="checkbox">
              <input id="singleRaw-01" type="checkbox" 
                v-model="localData.singleRaw"
                @change="onChangeHander"
              />
              <label for="singleRaw-01" class="checkbox-label">단일원료</label>
              <!-- singleRaw:{{ singleRaw }} -->
            </div>
          </div>
          <div>
            <div class="checkbox">
              <input id="multipleRaw-02" type="checkbox" 
                v-model="localData.multipleRaw"
                @change="onChangeHander"
              />
              <label for="multipleRaw-02" class="checkbox-label">복합원료</label>
              <!-- multipleRaw:{{ multipleRaw }} -->
            </div>
          </div>
        </div>
      </div>

      <!--첫번째는 펼쳐주세요 block-->
      <div v-for="(grpItem, grpIdx) of CODES.cmTypeData.grpList" :key="grpIdx"
        class="item filter-acd"
        :class="{'block': grpItem.isOpen}"  
      >
        <div class="title"
          @click="onToggleGroupItem(grpItem)"
        >{{grpItem.codeName}}
          <!-- 선택된 개수 표시-->
          <span v-if="CODES.cmTypeData[`TYPE${grpItem.etc1}`].filter(item=>item.checked).length > 0">
            {{CODES.cmTypeData[`TYPE${grpItem.etc1}`].filter(item=>item.checked).length}}
          </span>
        </div>

        <div class="check-wrap">

          <div v-for="(item, itemIdx) of CODES.cmTypeData[`TYPE${grpItem.etc1}`]"
            :key="itemIdx"
          >
            <div class="checkbox">
              <input :id="`raw-checkbox-${grpItem.etc1}-${itemIdx}`"
                type="checkbox"
                v-model="item.checked"
                @change="onChangeHander"
              />
              <label :for="`raw-checkbox-${grpItem.etc1}-${itemIdx}`" class="checkbox-label">{{item.codeNmKo}}</label>
            </div>
          </div>
          
        </div>
      </div>
      
    </div>

    <!-- localData.cmTypeData:{{ localData.cmTypeData }} -->
  </div>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getCodes, getRawCmTypeList } from '@/api/coching/comm/code';

const DEFAULT_FILTER_DATA = {
  singleRaw : true,
  multipleRaw : false,
  optionList : []
};

export default {
  name: 'coching-raw-filter',
  mixins: [ernsUtils],
  components: {
    
  },
  props: {
    singleRaw :{
      type : Boolean,
      default : DEFAULT_FILTER_DATA.singleRaw
    },
    multipleRaw :{
      type : Boolean,
      default : DEFAULT_FILTER_DATA.multipleRaw
    }
  },
  computed: {
      
  },
  watch: {
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },    
    singleRaw(newSingleRaw){
      const _vm = this;
      _vm.localData.singleRaw = newSingleRaw;
    },
    multipleRaw(newMultipleRaw){
      const _vm = this;
      _vm.localData.multipleRaw = newMultipleRaw;
    }
  },
  data() {
    return {
      CODES : {
        cmTypeData:{
          grpList: [],
          
          list: [],
          TYPE001: [],
          TYPE002: [],
          TYPE003: [],
          TYPE004: [],
          TYPE005: [],
        }
      },
      localData: {
        ...DEFAULT_FILTER_DATA
      },      
    }
  },
  async mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.loadCodes();
    _vm.fetchData();    

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
        _vm.localData.singleRaw = _vm.singleRaw;
        _vm.localData.multipleRaw = _vm.multipleRaw;

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },    

    //필터초기화
    onClickResetFilter(){
      const _vm = this;

      _vm.localData.singleRaw = DEFAULT_FILTER_DATA.singleRaw;
      _vm.localData.multipleRaw = DEFAULT_FILTER_DATA.multipleRaw;
      _vm.localData.optionList.forEach(item =>{
        item.checked = false;
      });

      _vm.onChangeHander();  
    },

    reloadMultipleRawType(){
      const _vm = this;
      _vm.localData.singleRaw = _vm.singleRaw;
      _vm.localData.multipleRaw = _vm.multipleRaw;
    },

    setSingleRaw(value){
      const _vm = this;
      _vm.localData.singleRaw = value;
    },

    setMultipleRaw(value){
      const _vm = this;
      _vm.localData.multipleRaw = value;
    },

    setOption(pItem, value){
      const _vm = this;

      const find = _vm.localData.optionList.find(item =>{
        return item.grpCode == pItem.grpCode && item.code == pItem.code
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
        _vm.CODES.cmTypeData.grpList.forEach(gItem => {
          if(gItem.etc1 != grpItem.etc1){
            gItem.isOpen = false;
          }
        });        
      }
    },

    //설정값 변화 핸들러
    onChangeHander(){
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

      const codeRes = await getCodes({grpCode:"CH001", etc5:_vm.$i18n.locale});
      const { resultCode, resultFailMessage, resultData } = codeRes;

      _vm.CODES.cmTypeData.grpList = [...resultData.list.map(item=>{
        return {
          ...item,
          isOpen : false
        }
      })];
      //_vm.CODES.cmTypeData.grpList[0].isOpen = true;

      await _vm.loadCmType();
    },

    async loadCmType(){
      const _vm = this;

      const res = await getRawCmTypeList({});
      const { resultCode, resultFailMessage, resultData } = res;

      const list = resultData.list;
      _vm.localData.optionList =  _vm.CODES.cmTypeData.list = list.map(item=>{
        return {
          ...item,
          checked : false,
          isOpen : false
        }
      });

      _vm.CODES.cmTypeData.grpList.forEach(code => {
        _vm.CODES.cmTypeData[`TYPE${code.etc1}`] = _vm.CODES.cmTypeData.list.filter(item => item.grpCode === code.etc1);
      });
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