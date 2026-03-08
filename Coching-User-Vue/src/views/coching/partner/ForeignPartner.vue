<template>
  <!--section-->
  <section id="coching-partner-foreign">
    <div class="container h-100">
      <!--content-->
      <div class="content">
        <!--common-content-->
        <div class="common-content">
          <div class="title-wrap">
            <div class="h1 text-center m-none">
                해외 원료사
            </div>
          </div>
          <!--total-wrap-->
          <div class="total-wrap">
            <div class="total-num">총<span>{{partnerData.pi.totalItem}}</span></div>
            <!--right-->
            <div class="right">
              <!--checkbox-->
              <div class="check-total-wrap">
                <div class="checkbox">
                  <input id="ptn-recruit-01" type="checkbox" 
                    v-model="partnerData.sc.recruiting"
                    @change="onSearch()"
                  />
                  <label for="ptn-recruit-01" class="checkbox-label">대리점모집</label>
                </div>
              
                <div class="checkbox">
                  <input id="ptn-recruit-02" type="checkbox" 
                    v-model="partnerData.sc.completed"
                    @change="onSearch()"
                  />
                  <label for="ptn-recruit-02" class="checkbox-label">완료</label>
                </div>
              </div>
              <!--search-->
              <div class="m-none">
                <CochingSelect
                  v-model="partnerData.sc.searchOption"
                  :options="selectOptions"
                  placeholder="선택하세요"
                  track-by="value"
                  label="text"
                />
              </div>
              <div class="input-set m-none">
                <div class="input-ic-set">
                  <input type="text" placeholder="검색" v-model="partnerData.sc.searchKeyword" @keyup.enter="onSearch"/>
                  <button type="button" class="input-ic ic-md ic-search-md" @click="onSearch"></button>
                </div>
              </div>
            </div>
          </div>

          <!-- 파트너 리스트 -->
          <div class="partner-list">
            <div 
              v-for="item of partnerData.list"
              :key="item.partnerSeq"
              class="partner-item"
              >
              <div class="content-wrap">
                <!-- 유튜브 영상 또는 기본 이미지 -->
                <div class="img-wrap">
                  <iframe 
                    v-if="getEmbedUrl(item.ytUrl)"
                    :src="getEmbedUrl(item.ytUrl)"
                    :title="item.ptnName" 
                    frameborder="0" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    referrerpolicy="strict-origin-when-cross-origin" 
                    allowfullscreen>
                  </iframe>
                  <!-- <div v-else style="color: #999; text-align: center; font-size: 1.25rem; font-weight: 700;">
                    No video.
                  </div> -->
                  <div v-else class="no-video"></div>
                </div>
                <!-- 회사 정보 -->
                <div class="info-wrap"
                  @click="onClickPartner(item)"
                >
                  <div class="company-header">
                    <div class="company-name">
                      {{item.ptnName | eufmtEllipsis(50)}}
                      <span v-if="item.nationName">({{item.nationName}})</span>
                    </div>
                    <div class="status-wrap">
                      <div v-if="item.recruitYn == 'N'" class="state-badge green">대리점 모집</div>
                      <div v-else class="state-badge gray">모집 완료</div>
                    </div>
                  </div>
                  <div class="company-desc">{{item.ptnIntro}}</div>
                  <div class="company-website" v-if="item.pageUrl">
                    <a 
                      :href="item.pageUrl" 
                      @click.stop
                      class="website-link"
                    >
                      {{item.pageUrl}}
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div v-show="partnerData.list.length <= 0"
            class="result-none empty-content">
            등록된 원료사가 없습니다.
          </div> 

          <!--pagenation-->
          <Pagenation
            v-model="partnerData.pi.curPage"
            :totalRows="partnerData.pi.totalItem"
            :perPage="partnerData.pi.perPage"
            @input="onChangePage"
          ></Pagenation>
        </div>
      </div>

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import Pagenation from '@/components/Pagenation.vue';
import CochingSelect from '@/components/CochingSelect.vue';
import CochingTvCard from '@/components/CochingTvCard.vue';
import { getPtnList } from '@/api/coching/comm/partner';

const DEF_PARTNER = {
	sc : {
    nation: '002',
    searchOption: '',
    searchKeyword: '',
    recruitYn: '',
    recruiting: '',
    completed: '',
	},
	pi:{
		curPage : 1,
    totalItem : 0,
    perPage : 10
	},
};

export default {
  name: 'coching-partner-foreign',
  mixins: [ernsUtils],
  components: {
    Pagenation,
    CochingSelect,
    CochingTvCard
  },
  computed: {},
  watch: {
    '$route': 'fetchData' // 라우트가 변경되면 메소드를 다시 호출
  },
  props: {},
  data() {
    return {
      selectOptions: [
        { value: 'ptnName', text: '원료사명' },
        { value: 'nationName', text: '국가' },
        { value: 'ptnIntro', text: '소개' }
      ],
      searchKeyword: '',
      partnerData: {
        list: [],
        pi: {...DEF_PARTNER.pi},
        sc: {...DEF_PARTNER.sc},
      }
    }
  },
  async mounted() {
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();
  },
  beforeDestroy() {
    const _vm = this;
  },
  methods: {
    getEmbedUrl(ytUrl) {
      if (!ytUrl) return null;
      const match = ytUrl.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      return match ? `https://www.youtube.com/embed/${match[1]}` : null;
    },
    // 데이터 가져오는 부분
    async fetchData() {
      const _vm = this;

      _vm.loading(true);
      try {

        await _vm.loadList(1);
        
      } catch (err) {
        _vm.defaultApiErrorHandler(err); // 기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    /* 해외 원료사 목록 가져오기 */
    async loadList(pageNo) {
			const _vm = this;
      const dataMap = _vm.partnerData, pInfo = _vm.partnerData.pi, searchOp = _vm.partnerData.sc;
      pInfo.curPage = pageNo || 1;

			_vm.loading(true);
      try {
        const params = _vm.getListSearchParam();
        const res = await getPtnList(params);
        const resData = res.resultData;
        
        const pageInfo = resData.pageInfo;
        dataMap.pi = {
          ...dataMap.pi,
          curPage : pageInfo.currentPage,
          totalItem : pageInfo.totalItem,
          perPage : pageInfo.pageItemSize
        }

        //데이터 바인딩
        dataMap.list = resData.list;
      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    /* 검색 조건 가져오기 */
		getListSearchParam() {
      const _vm = this;
      const pInfo = _vm.partnerData.pi, searchOp = _vm.partnerData.sc;

      searchOp.recruitYn = _vm.getRecruitYnValue();

			const retParam = {
        ...searchOp,
        
        page : pInfo.curPage,
        rowSize : pInfo.perPage
      };
			
			return retParam;
    },

    // 대리점 모집 여부
    getRecruitYnValue() {
      const { recruiting, completed } = this.partnerData.sc;
      
      return recruiting && completed ? '' :
        recruiting ? 'N' :
        completed ? 'Y' : '';
    },

    // 검색
		onSearch() {
      const _vm = this;
      _vm.loadList(1);
    },

    // 파트너 클릭
    onClickPartner(item) {
      const _vm = this;
      
      // 파트너 상세 화면으로 이동 (라우터 이름은 실제 상세 화면에 맞게 수정 필요)
      _vm.ermPushPage({
        name: 'coching-partner-foreign-detail', 
        query: {
          ptnSeq: item.ptnSeq
        }
      });
    },

    // 페이지 변경
    onChangePage(pageNo) {
      const _vm = this;
      _vm.loadList(pageNo);
    },
    
    docReady() {
      const _vm = this;
      // TODO: DOM 조작이나 이벤트 리스너 설정
    }
  }
}
</script>

<style lang="scss" scoped>
#coching-partner-foreign {
  .partner-list {
    .partner-item {
      border-bottom: 1px solid var(--color--border-eee);
      
      &:hover {
        background-color: var(--color--bg-hover);
      }
      
      .content-wrap {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1.5rem 1rem;
        
        .img-wrap {
          width: 20rem;
          aspect-ratio: 16 / 9;
          overflow: hidden;
          border-radius: 0.5rem;

          iframe {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
          }

          .no-video {
            width: 100%;
            height: 100%;
            background-color: var(--color--input-disabled);
            background-image: url('~@/assets/images/ic-no-video-lg.png');
            background-size: 60px 60px;
            background-repeat: no-repeat;
            background-position: center;
          }
        }
        
        .info-wrap {
          flex: 1;
          cursor: pointer;
          
          .company-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0.5rem;
            gap: 1rem;
          }
          
          .company-name {
            font-size: 1.25rem;
            font-weight: 700;
            color: var(--color--bk);
            word-break: break-all;
            // flex: 1;
            // min-width: 0;
          }
          
          .company-desc {
            font-size: 1rem;
            color: var(--color--gray-666);
            line-height: 1.5;
            margin-bottom: 0.5rem;
            word-break: break-all;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .company-website {
            font-size: 0.75rem;
            
            .website-link {
              color: var(--color--gray-999);
              text-decoration: underline !important;
              
              &:hover {
                color: var(--color--primary);
              }
            }
          }
        }
        
        .status-wrap {
          flex-shrink: 0;
          
          .btn {
            min-width: 6rem;
          }
        }
      }
    }
  }

  /* 모바일 사이즈 */
  @media (max-width: 767px) { 
    .partner-list .partner-item .content-wrap {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.75rem;
      
      .img-wrap {
        width: 100%;
      }
      
      .info-wrap {
        width: 100%;
      }
    }
  }
}
</style>
