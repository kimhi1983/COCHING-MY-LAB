<template>
  <div class="bottom">
    <!--item-->    
    <div class="item">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">SKIN DEEP DATA</div>
        <div class="date">업데이트 일시 : {{coIngdInfo.ewgChngDttm | eFmtDateTime('.') | eufmtValueDash}}</div>        
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

      <!-- coIngdInfo:{{coIngdInfo}} -->

      <!--btn-->
      <button v-show="coIngdInfo.ewgUrl"
        @click="onClickGoEwg(coIngdInfo.ewgUrl)"
        type="button" 
        class="btn btn-sm btn-gray-outline w-100">바로가기</button>
    </div>

    <!--item-->
    <div class="item">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">국가별 규제</div>
        <div class="date">업데이트 일시 : {{coIngdInfo.nationLimits.updateDate | eufmtValueDash}}</div>
      </div>
      <!--ingre-table-->
      <div class="ingre-table nation-limits">
        <div v-show="coIngdInfo.nationLimits.displayList.length <= 0"
          class="empty-wrap" :class="{'show-btn' : coIngdInfo.nationLimits.externalUrl }"
        >규제사항 확인 요망</div>          
        <table v-show="coIngdInfo.nationLimits.displayList.length > 0">
          <colgroup>
            <col width="90" />
            <col />
          </colgroup>
          <tbody>
            <tr v-for="item in coIngdInfo.nationLimits.displayList" :key="item.limitKo">
              <th class="flex">
                <div class="ic-sm" :class="item.icon"></div>
                {{item.name}}
              </th>
              <td>{{item.nationKoListStr}}</td>              
            </tr>           
          </tbody>
        </table>
      </div>
      <!--btn-->
      <button 
        @click="onClickGoNationLimits()"
        type="button" 
        class="btn btn-sm btn-gray-outline w-100">의약품안전나라 바로가기</button>
      <!--ready-overlay-->
      <!-- <div class="ready-overlay">
        <div class="img">
          <div class="ic-xlg ic-emoji-clock-xlg"></div>
        </div>
        <div class="title">서비스 준비중입니다.</div>
      </div> -->
    </div>
    
    <!--TODO : 수출정보 미구현-->
    <!--item-->
    <div class="item">
      <!--title-wrap-->
      <div class="title-wrap">
        <div class="title">수출 정보</div>
        <div class="date">업데이트 일시 : {{coIngdInfo.nationExpLimits.updateDate | eufmtValueDash}}</div>
      </div>
      <!--ingre-table-->
      <div class="ingre-table">
        <table v-show="coIngdInfo.nationExpLimits.displayList.length > 0">
          <colgroup>
            <col width="90" />
            <col />
          </colgroup>
          <tbody>
            <tr v-for="item in coIngdInfo.nationExpLimits.displayList" :key="item.limitKo">
              <th class="flex">
                <div class="ic-sm" :class="item.icon"></div>
                {{item.name}}
              </th>
              <td>{{item.nationKoListStr}}</td>              
            </tr>
          </tbody>
        </table>
      </div>
      <!--btn
      <button type="button" class="btn btn-sm btn-gray-outline w-100">바로가기</button>
      -->
      <!--ready-overlay
      <div class="ready-overlay">
        <div class="img">
          <div class="ic-xlg ic-emoji-clock-xlg"></div>
        </div>
        <div class="title">서비스 준비중입니다.</div>
      </div>
      -->
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

    onClickGoNationLimits(){
      const _vm = this;
      var url = "https://nedrug.mfds.go.kr/pbp/CCBDG01";
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

.ingre-table.nation-limits{
  min-height: 133px;

  .empty-wrap{
    height: 11rem;
    border-bottom: none;
    &.show-btn{
      height: 7rem;
    }
  }  
}
</style>

<style lang="scss">

</style>