<template>
  <div>
    <b-card>
      <b-row>
        <b-col cols="12">
          <b-form-group>
            <b-input-group>
              <b-form-input
                type="text"
                v-model="vMaster.sc.keyword"
                placeholder="2글자 이상 입력해주세요."
                @keyup.enter="onSearch"
              />
              <b-input-group-append>
                <b-button @click="onSearch"> 검색 </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </b-col>
      </b-row>

      <b-table
        class="position-relative"
        responsive
        :items="rawList"
        :fields="fields"

        v-show="vMaster.isShow"
        @row-clicked="clickRowItem"
        hover
      >
      </b-table>

      <div class="mx-2 mb-2" v-show="vMaster.isShow">
        <b-row>
          <b-col
            cols="12"
            sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-start"
          >
            <span class="text-muted">
              {{ eumPaginationRangeForPageInfo(vMaster.pi) }}
            </span>
          </b-col>

          <b-col
            cols="12"
            sm="6"
            class="d-flex align-items-center justify-content-center justify-content-sm-end"
          >
            <b-pagination
              v-if="vMaster.pi.totalItem > 0"
              v-model="vMaster.pi.curPage"
              :total-rows="vMaster.pi.totalItem"
              :per-page="vMaster.pi.perPage"
              first-number
              last-number
              limit="10"
              class="mb-0 mt-1 mt-sm-0"
              prev-class="prev-item"
              next-class="next-item"
              @change="loadList"
            >
              <template #prev-text>
                <feather-icon icon="ChevronLeftIcon" size="18" />
              </template>
              <template #next-text>
                <feather-icon icon="ChevronRightIcon" size="18" />
              </template>
            </b-pagination>
          </b-col>
        </b-row>
      </div>
    </b-card>
  </div>
</template>

<script>
import Ripple from "vue-ripple-directive";
import { searchAddress } from "@/api/coching-bo/fr/franchise";
import { getXY } from "@/api/coching-bo/fr/franchise";

import ernsUtils from "@/components/mixins/ernsUtils";

export default {
  mixins: [ernsUtils],
  directives: {
    Ripple,
  },
  props: {},

  data() {
    return {
      rawList : [],
      fields : [
        {key : 'zipNo', label : '우편번호', sortable : false, class : 'text-center'},
        {key : 'roadAddr', label : '주소', sortable : false, class : 'text-center'},
      ],
      rowItem : {},

      vMaster : {
        sc: {
          keyword : null,
        },

        pi: {
          curPage: 1,
          totalItem: 0,
          perPage: 5,
        },

        isShow : false,
      },
    };
  },

  mounted() {
    const _vm = this;
    _vm.fetchData();
  },

  methods: {
    async fetchData(){
      const _vm = this;

    },

    async loadList(pageNo){
      const _vm = this;
      const pInfo = _vm.vMaster.pi, searchOp = _vm.vMaster.sc;
      pInfo.curPage = pageNo || 1;

      try {
        _vm.loading(true);

        const params = {
          keyword : searchOp.keyword,
          currentPage : pInfo.curPage,
          countPerPage : pInfo.perPage
        };

        const res = await searchAddress(params);
    
        const restResults = res.resultData.results;
        const resCommon = restResults.common;

        _vm.rawList = [];
        _vm.vMaster.isShow = false;

        if(resCommon.errorCode != 0){ // 도로명주소 API 검색 에러 (validation 등)
          _vm.alertError(resCommon.errorMessage);
        }else{ // 성공
          _vm.rawList = restResults.juso;
          _vm.vMaster.isShow = true;

          pInfo.totalItem = resCommon.totalCount;
        }

      }catch(err) {
        _vm.alertError(err);
      }finally{
        _vm.loading(false);
      }

    },

    onSearch(){
      const _vm = this;
      _vm.loadList(1);
    },

    clickRowItem(item) {
      const _vm = this;
      _vm.rowItem = item;
      _vm.$emit("clickRowItem");
    },

    getRowItem() {
      const _vm = this;
      return _vm.rowItem;
    }
  },
};
</script>