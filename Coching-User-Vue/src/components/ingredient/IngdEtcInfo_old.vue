<template>
  <div class="bottom">
    <!--item-->
    <div class="item">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">SKIN DEEP DATA</div>
        <button v-show="coIngdInfo.ewgUrl"
          @click="onClickGoEwg(coIngdInfo.ewgUrl)"
          type="button" class="btn-go">바로가기</button>
      </div>
      <!--ingre-table-->
      <div class="ingre-table">
        <table>
          <colgroup>
            <col width="100" />
            <col />
            <col width="32" />
          </colgroup>
          <tbody>
            <tr>
              <th>등급</th>
              <td>
                <span v-if="coIngdInfo.ewgScoreMin && coIngdInfo.ewgScore
                  && coIngdInfo.ewgScoreMin != coIngdInfo.ewgScore"
                >Depends on usage</span>
                <span v-else>-</span>
              </td>
              <td>                
                <div v-if="coIngdInfo.ewgScoreMin && coIngdInfo.ewgScore"
                  class="ewg" :class="getEwgClass(getEwgInfo(`${coIngdInfo.ewgScoreMin}-${coIngdInfo.ewgScore}`).max)">
                  {{getEwgInfo(`${coIngdInfo.ewgScoreMin}-${coIngdInfo.ewgScore}`).scoreText}}
                </div>                
              </td>
            </tr>
            <tr>
              <th>데이터유효성</th>
              <td colspan="2">{{coIngdInfo.ewgDataLabel | eufmtValueDash}}</td>
            </tr>
            <tr>
              <th>Last update</th>
              <td colspan="2">{{coIngdInfo.ewgChngDttm | eFmtDateTime('.') | eufmtValueDash}}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!--item-->
    <div class="item">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">수출 정보(미구현)</div>
      </div>
      <!--ingre-table-->
      <div class="ingre-table">
        <table>
          <colgroup>
            <col width="180" />
            <col />
          </colgroup>
          <tbody>
            <tr>
              <td>한국, EU</td>
              <th class="flex">
                <div class="ic-sm ic-check-sm"></div>
                가능
              </th>
            </tr>
            <tr>
              <td>아세안, 일본, 캐나다</td>
              <th class="flex">
                <div class="ic-sm ic-close-sm"></div>
                불가능
              </th>
            </tr>
            <tr>
              <td>대만, 미국</td>
              <th class="flex">
                <div class="ic-sm ic-warning-sm"></div>
                제한
              </th>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!--item-->
    <div class="item">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">국가별 규제(미구현)</div>
        <button type="button" class="btn-go">바로가기</button>
      </div>
      <!--ingre-table-->
      <div class="ingre-table">
        <table>
          <colgroup>
            <col width="180" />
            <col />
          </colgroup>
          <tbody>
            <tr>
              <td>한국, EU</td>
              <th class="flex">
                <div class="ic-sm ic-check-sm"></div>
                없음
              </th>
            </tr>
            <tr>
              <td>아세안, 일본, 캐나다</td>
              <th class="flex">
                <div class="ic-sm ic-close-sm"></div>
                금지
              </th>
            </tr>
            <tr>
              <td>대만, 미국</td>
              <th class="flex">
                <div class="ic-sm ic-warning-sm"></div>
                한도
              </th>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { DEF_INGD_DETAIL } from '@/constants/data';
import moment from 'moment';

export default {
  name: 'coching-ingredient-etc-info',
  mixins: [ernsUtils],
  components: {
    
  },
  props: {
    ingdInfo : {
      type : Object,
      default: () => ({}),
      require : true
    },
    coIngdInfo:{
      type : Object,
      default: () => ({
        ...DEF_INGD_DETAIL
      }),
      require : true
    },
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
      
    }
  },
  async mounted(){
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();    

  },
	beforeDestroy() {
    const _vm = this;
	},
  methods: {
    onClickGoEwg(url){
      const _vm = this;

      _vm.eumLocationUrl(url);
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