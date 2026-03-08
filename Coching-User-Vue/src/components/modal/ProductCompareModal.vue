<template>
  <ModalFull ref="modal_full"
    :isActive="localData.isActive" 
    :classOption="localData.classOption"
    @onClickDim="onClickClose()"
    @close="onClickClose()">

    <!-- Header Content -->
    <template v-slot:header>
      <div class="title">선택한 제품 비교하기</div>
    </template>

    <!-- Body Content -->
    <template v-slot:body>
      <!--filter-common-->
      <div class="modal-pr-wrap">
        <div class="item">
          <!--pr-info-->
          <ProductCompareProdInfo
            :prodInfo="localData.prodInfo1"/>

          <!--ewg table-->
          <div class="ewg-table">
            <div class="total-num">전성분
              <span>{{ localData.ingredients1.length }}</span>
            </div>
            <table>
              <colgroup>
                <col width="80" />
              </colgroup>
              <thead>
                <tr>
                  <th>EWG</th>
                  <th>성분명</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(ingd, idx) of localData.ingredients1" :key="idx"
                  @click="onClickIngdList(ingd, localData.prodInfo1)"   
                    :class="{'link': ingd != null}"
                  >
                  <td v-if="ingd == null">
                    <div class="ewg">-</div>
                  </td>
                  <td v-else>
                    <div class="ewg" :class="getEwgClass(getEwgInfo(ingd.ewg).max)">
                      {{getEwgInfo(ingd.ewg).scoreText}}</div>
                  </td>
                  <td v-if="ingd == null">-</td>
                  <td v-else>{{ ingd.korean || '-' }}</td>
                </tr>                
              </tbody>
            </table>
          </div>
        </div>
        <div class="item">
          <!--pr-info-->
          <ProductCompareProdInfo
            :prodInfo="localData.prodInfo2"/>

          <!--ewg table-->
          <div class="ewg-table">
            <div class="total-num">전성분
              <span>{{ localData.ingredients2.length }}</span>
            </div>
            <table>
              <colgroup>
                <col width="80" />
              </colgroup>
              <thead>
                <tr>
                  <th>EWG</th>
                  <th>성분명</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(ingd, idx) of localData.ingredients2" :key="idx"
                  @click="onClickIngdList(ingd, localData.prodInfo2)" 
                  :class="{'link': ingd != null}"
                  >
                  <td v-if="ingd == null">
                    <div class="ewg">-</div>
                  </td>
                  <td v-else>
                    <div class="ewg" :class="getEwgClass(getEwgInfo(ingd.ewg).max)">
                      {{getEwgInfo(ingd.ewg).scoreText}}</div>
                  </td>
                  <td v-if="ingd == null">-</td>
                  <td v-else>{{ ingd.korean || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>

  </ModalFull>
</template>
<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import ModalFull from '@/components/modal/ModalFull.vue';
import ProductCompareProdInfo from './ProductCompareProdInfo.vue';
import { DEF_PRODINFO } from '@/constants/data';
import _ from 'lodash';

export default {
  name: 'coching-product-compare-modal',
  mixins: [ernsUtils],
  components: {
    ModalFull,
    ProductCompareProdInfo
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
      status: '',
      CODES: {
        
      },

      localData: {
        isActive : false,
        classOption: "modal-lg for-vs-detail",
        result : undefined,

        prodInfo1 : {...DEF_PRODINFO},
        prodInfo2 : {...DEF_PRODINFO},

        ingredients1 : [],
        ingredients2 : []
      },         
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
    open(options){
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = true;
      ld.prodInfo1 = options.prodInfo1;
      ld.prodInfo2 = options.prodInfo2;

      ld.ingredients1 = [];
      ld.ingredients2 = [];
      _vm.compareIngredients();
      
      _vm.$refs['modal_full'].bodyScrollToTop(500);

      return new Promise( (resolve, reject) => {
        ld.result = resolve;
      })
    },

    //전성분 상세보기 클릭
    onClickIngdList(pickItem, prodInfo){
      const _vm = this;

      if(!pickItem){
        return;
      }
      
      _vm.ermPushPage({
        name:'coching-ingredients', 
        query : {
          productId : prodInfo.product.id,
          ingdId : pickItem.id
        }
      });
    },

    //성분비교
    compareIngredients(){
      const _vm = this;
      const ld = _vm.localData;

      const rawIngredients1 = ld.prodInfo1.ingredients || [];
      const rawIngredients2 = ld.prodInfo2.ingredients || [];

      const groupBoth = rawIngredients1.filter(ingd1=>{        
        const f2 = rawIngredients2.find(ingd2=>{
          return ingd1.id == ingd2.id;
        })

        return f2 && f2.id == ingd1.id;
      });
      const groupOnly1 = rawIngredients1.filter(ingd1=>{        
        const f2 = rawIngredients2.find(ingd2=>{
          return ingd1.id == ingd2.id;
        })

        return !f2;
      });
      const groupOnly2 = rawIngredients2.filter(ingd2=>{        
        const f2 = rawIngredients1.find(ingd1=>{
          return ingd2.id == ingd1.id;
        })

        return !f2;
      });

      ld.ingredients1 = [...groupBoth, 
        ...groupOnly1,
        ...[...Array(groupOnly2.length)].map(() => null)
      ];
      ld.ingredients2 = [...groupBoth, 
        ...[...Array(groupOnly1.length)].map(() => null), 
        ...groupOnly2
      ];
    },

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

    async loadCodes(){
      const _vm = this;
     
    },

    onClickClose() {
      const _vm = this;

      const ld = _vm.localData;
      ld.isActive = false;
      ld.result(false);
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