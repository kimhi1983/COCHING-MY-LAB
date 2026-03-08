<template>
  <b-row>
    <b-col cols="12" md="6" class="mb-2">
      <b-form-group>
        <b-input-group>
          <b-form-input
            type="text"
            placeholder="매장명"
            v-model="searchText"
            @input="inputSearchText"
          />
          <b-input-group-append>
            <b-button 
              v-ripple.400="'rgba(113, 102, 240, 0.15)'"
              variant="primary" 
              @click="onClickSearch">검색</b-button>
          </b-input-group-append>
        </b-input-group>
      </b-form-group>
    </b-col>
    <b-col cols="12" md="2" class="check-all">
      <b-form-checkbox
        v-model="selectAll"
        @input="onClickCheckAll">
        전체선택
      </b-form-checkbox>
    </b-col>    
    <b-col cols="12" class="mb-4">
      <b-row>
        <b-col cols="3" v-for="item of list.dispList"
          :key="item.frSeq" class="fr-list" @click="onClickFr(item)">
          <!-- <b-button variant="primary">{{item.frName}}</b-button> -->
          <div class="fr-item" :class="{'fr-selected': item.selected}">
            {{item.frName}}
          </div>
          <!-- <b-form-checkbox
            :value="item.seq" 
            @input="onClickFr(item)">
            {{item.frName}}
          </b-form-checkbox> -->
        </b-col>
      </b-row>
    </b-col>
    <hr>
    <b-col cols="12" v-if="selectList.dispList.length > 0">
      <label>총 <span class="text-red">{{selectList.dispList.length | eFmtCurrency}}</span> 개의 매장이 선택되었습니다.</label>
      <div class="fr-selected-names"> 
        <li v-for="(item, idx) of selectList.dispList"
          :key="idx" class="fr-selected-name">{{item.frName}}
        </li>
      </div>  
    </b-col>
  </b-row>
</template>

<script>
import {getCodes} from '@/api/coching-bo/comm/code';
import {listFr} from '@/api/coching-bo/fr/franchise';

import { required } from "@validations";
import ernsUtils from '@/components/mixins/ernsUtils';
import Ripple from 'vue-ripple-directive'
import vSelect from 'vue-select';

const baseUrl = process.env.VUE_APP_API_BASE_URL;

export default {
  name: 'coching-BackOffice-franchise-select',
  mixins: [ernsUtils],  
  components : {
    vSelect, 
  },
  props: {
    frList: {
      type: Array,
      default: function(){
        return []
      }
    },
    selectedList: {
      type: Array,
      default: function(){
        return []
      }
    },
  },
  directives: {
    Ripple
  },
  computed : {
  
  },
  watch: {
    // 라우트가 변경되면 메소드를 다시 호출됩니다.
    '$route': 'fetchData',
  },
  data(){
    return {
      list: {
        rawList: [],
        dispList: []
      },
      selectList: {
        rawList: [],
        dispList: []
       },

      searchText: "",

      selectAll: false,

      selectedCnt: 0
    }
  },
  mounted(){
    const _vm = this;    
    _vm.selectList.rawList = [..._vm.selectedList];
    _vm.list.rawList = [..._vm.frList];
    _vm.loadFrList();
  },
  beforeDestroy(){
    const _vm = this;
	},
  methods: {
    async loadFrList() {
      const _vm = this;

      const list = _vm.list.rawList;
      for(const item of list) {
        for(const selected of _vm.selectList.rawList) {
          if(item.frSeq == selected.frSeq) {
            item.selected = true;
          }          
        }
      }
      
      _vm.list.dispList = list;
      if(list.length == _vm.selectList.rawList.length) {
        _vm.selectAll = true;
        _vm.selectList.dispList.push({frName: "전체 선택"});
        return;
      } 
      _vm.selectList.dispList = _vm.selectList.rawList;
    },

    onClickFr(item) {
      const _vm = this;

      if(!item.selected) {
        item.selected = true;
        _vm.selectList.rawList.push(item);
      } else {
        item.selected = false;
        _vm.selectList.rawList = _vm.selectList.rawList.filter(elem=>{
          return elem.frSeq != item.frSeq; 
        });
      }
      _vm.selectList.dispList = _vm.selectList.rawList;
    },

    onClickSearch() {
      const _vm = this;
      if(!_vm.searchText) {
        _vm.list.dispList = _vm.list.rawList;
        return;
      }
      const searchVal = _vm.searchText.trim().toUpperCase();
      
      _vm.list.dispList = _vm.list.rawList.filter(item=>{
        const frName = item.frName.toUpperCase();
        return frName.includes(searchVal);
      });
    },
    inputSearchText() {
      const _vm = this;
      _vm.onClickSearch();
    },

    onClickCheckAll(val) {
      const _vm = this;
      if(val) {
        _vm.selectList.rawList = _vm.list.dispList.map(item=>{
          item.selected = true;
          return item;
        });
        _vm.selectList.dispList = [];
        _vm.selectList.dispList.push({frName: "전체 선택"});
      } else {
        _vm.selectList.rawList = _vm.list.dispList.map(item=>{
          item.selected = false;
          return {};
        });
        _vm.selectList.rawList = [];
        _vm.selectList.dispList = _vm.selectList.rawList;
      }
    },
    getSelectList() {
      const _vm = this;
      const selectObj = {};
      
      selectObj.selectAll = _vm.selectAll;
      selectObj.list = _vm.selectList.rawList;
      return selectObj;
    }
  }
}
</script>

<style lang="scss" scoped>
.fr-container {
  display:flex; 
  flex-wrap:wrap;
  justify-content: space-between;
}
.fr-item {
  // width: 24%;
  margin-bottom: 5px;
  padding: 6px 0 6px 0;
  border-radius: 3px;
  border: 1px solid rgb(208, 204, 253);
  background-color: rgba(183, 176, 255, 0.1);
  text-align: center;
  cursor: pointer;
}
.fr-item:hover, .fr-item:active  {
  background-color: rgba(183, 176, 255, 0.4);
}
.fr-selected  {
  background-color: rgba(183, 176, 255, 0.4);
}
.fr-selected-names {
  display: flex;
  flex-wrap:wrap;
  align-items: center;
  .fr-selected-name {
    list-style: none;
    font-size: 14px;
    color: #798691;
    background: #bed6ee;
    padding: 6px 6px 8px;
    border-radius: 4px;
    margin: 2px;
  }
}
.check-all {
  padding-top: 8px;
  .custom-checkbox::after {
    padding-top: 8px;
  }
}
.text-red {
  color: red;
}
</style>