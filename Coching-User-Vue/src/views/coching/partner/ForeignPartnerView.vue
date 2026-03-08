<template>
  <!--section-->
  <section id="coching-partner-foreign-view">
    <div class="container h-100">
      <!--content-->
      <div class="content">
        <!--common-content-->
        <div class="common-content">
          <!--title-->
          <div class="title-wrap">
            <div class="h1 text-center m-none">
              해외 원료사
            </div>
          </div>

          <!--main content-->
          <div class="main-content">
            <!--left column - main content-->
            <div class="left-column">
              <!--video player -->
              <div class="video-section" v-if="partnerData.ytUrl">
                <div class="video-wrapper">
                  <iframe 
                    :src="getEmbedUrl(partnerData.ytUrl)"
                    :title="partnerData.ptnName" 
                    frameborder="0" 
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    referrerpolicy="strict-origin-when-cross-origin" 
                    allowfullscreen>
                  </iframe>
                </div>
              </div>

              <!--company info-->
              <div class="company-info-section">
                <h2 class="company-name">
                  {{ partnerData.ptnName }} 
                  <span v-if="partnerData.nationName">({{partnerData.nationName}})</span>
                </h2>
                <div class="status-wrap">
                  <div class="company-website">
                    <a 
                      v-if="partnerData.pageUrl"
                      :href="partnerData.pageUrl" 
                      class="website-link"
                    >
                      {{partnerData.pageUrl}}
                    </a>
                  </div>
                  <button :class="['btn', 'btn-sm', partnerData.recruitYn === 'N' ? 'btn-primary' : 'btn-disabled']"
                    :disabled="partnerData.recruitYn === 'Y'"
                    @click="onClickApplication">
                    {{ partnerData.recruitYn === 'N' ? '대리점 신청' : '모집 완료' }}
                  </button>
                </div>
              </div>

              <!--detailed description-->
              <div class="description-section" v-show="partnerData.ptnIntro">
                <div class="description-content" v-html="partnerData.ptnIntro"></div>
              </div>
            </div>

            <!--right column - sidebar-->
            <div class="right-column">
              <!--related products section-->
              <div class="related-products-section">
                <h3 class="section-title">원료사 원료</h3>
                <div class="card-wrap" v-show="rawData.list.length > 0">
                  <RawCard v-for="(item, idx) of rawData.list"
                    :key="idx"
                    :rawInfo="item"
                    @onClickCard="onClickRawCard(item)"
                    @onClickHashTag="(hashtag)=>{goSearchKeywordV2(hashtag, {hintField : '1001', emo : true})}"
                  />
                </div>
                <div v-show="rawData.list.length <= 0"
                  class="result-none empty-content">
                  등록된 원료가 없습니다.
                </div> 
              </div>
            </div>
          </div>
        </div>
      </div>

      <!--대리점 신청-->
      <PartnerRecruitModal ref="partnerRecruitModal"></PartnerRecruitModal>
      <!--원료정보-->
      <RawInfoModal ref="rawInfoModal"
        @onClickRequest="onClickRequest"
      />

      <!--오류-->
      <AlertModal ref="alertModal"></AlertModal>
      <ConfirmModal ref="confirmModal"></ConfirmModal>
    </div>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import RawCard from '@/components/RawCard.vue';
import RawInfoModal from '@/components/modal/RawInfoModal.vue';
import PartnerRecruitModal from '@/components/modal/PartnerRecruitModal.vue';
import { getPtn } from '@/api/coching/comm/partner';
import { getSearchRawList } from '@/api/coching/search/search';


const DEF_PARTNER = {
  ptnSeq: 0,
  ptnName: '',
  nationName: '',
  pageUrl: '',
  ytUrl: '',
  recruitYn: '',
  ptnIntro: '',
  rgtrSeq: 0,

  membSeq: null,
  membId: '',
  membType: '',
  membName: '',
  nickname: '',
  phone: '',
  email: '',
};

export default {
  name: 'coching-partner-foreign-view',
  mixins: [ernsUtils],
  components: {
    RawCard,
    RawInfoModal,
    PartnerRecruitModal,
  },
  computed: {    
  },
  watch: {
    ptnSeq(newVal, oldVal) {
      const _vm = this;
      _vm.fetchData();
    },
  },
  props: {
    ptnSeq: {
      type: [Number, String],
      required: true
    }
  }, 
  data() {
    return {
      partnerData: {...DEF_PARTNER},
      rawData: {
        list: [],
        pi: {
          curPage: 1,
          totalCount: 0,
          perPage: 20
        }
      },
    }
  },
  async mounted() {
    const _vm = this;

    _vm.docReady();
    _vm.fetchData();
  },
  methods: {
    // 유튜브 URL을 임베드 URL로 변환
    getEmbedUrl(ytUrl) {
      if (!ytUrl) return null;
      const match = ytUrl.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      return match ? `https://www.youtube.com/embed/${match[1]}` : null;
    },

    docReady() {
      const _vm = this;
      // TODO: DOM 조작이나 이벤트 리스너 설정
    },

    // 데이터 가져오는 부분
    async fetchData() {
      const _vm = this;

      _vm.loading(true);
      try {

        await _vm.loadDetail();
        await _vm.loadRawList();
        
      } catch (err) {
        _vm.defaultApiErrorHandler(err); // 기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    /* 원료사 정보 가져오기 */
    async loadDetail() {
			const _vm = this;

			_vm.loading(true);
      try {

        //원료사 정보
        const params = _vm.getSearchParam();
        const res = await getPtn(params);

        _vm.partnerData = {
          ...res.resultData,
          ptnIntro: res.resultData.ptnIntro?.replace(/\n/g, '<br>') || '',
        };

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
		},

    /* 검색 조건 가져오기 */
		getSearchParam() {
      const _vm = this;
      
			const retParam = {
        ptnSeq: _vm.ptnSeq,
      };
			
			return retParam;
    },

    /* 원료 목록 가져오기 */
    async loadRawList() {
      const _vm = this;

      _vm.loading(true);
      try {
        // 원료 데이터 초기화
        _vm.rawData.list.splice(0, _vm.rawData.list.length);
        _vm.rawData.pi.curPage = 1;
        _vm.rawData.pi.totalCount = 0;

        const rawParams = {
          ptnSeq: _vm.ptnSeq,
          // page: 1,
          // rowSize: 20
        };

        const res = await getSearchRawList(rawParams);
        const { list, total, version } = res.resultData;
        
        const dataList = _vm.convertSearchResultByVersion(version, list);
        _vm.rawData.list.push(...dataList);
        _vm.rawData.pi.curPage = 1;
        _vm.rawData.pi.totalCount = total;

      } catch (err) {
        _vm.defaultApiErrorHandler(err);
      } finally {
        _vm.loading(false);
      }
    },


    // 대리점 신청 버튼 클릭
    onClickApplication() {
      const _vm = this;
      _vm.$refs.partnerRecruitModal.open({
        partnerData: _vm.partnerData
      });
    },

     // 원료 카드 클릭 이벤트
     async onClickRawCard(rawInfo) {
       const _vm = this;
       
       await _vm.$refs.rawInfoModal.open({
         rawInfo: rawInfo
       });
     }
  }
}
</script>

<style lang="scss" scoped>
#coching-partner-foreign-view {
  .main-content {
    display: flex;
    gap: 3rem;
  }

  .left-column {
    flex: 2;
    min-width: 0;
  }

  .right-column {
    flex: 1;
    min-width: 0;
  }

  // Video Section
  .video-section {

    .video-wrapper {
      position: relative;
      width: 100%;
      aspect-ratio: 16 / 9;
      border-radius: 0.5rem;
      overflow: hidden;

      iframe {
        width: 100%;
        height: 100%;
        object-fit: cover;
        object-position: center;
        background-color: #f5f5f5;
      }
    }
  }

  // Company Info Section
  .company-info-section {
    margin: 1.5rem 0;

    .company-name {
      font-size: 1.25rem;
      font-weight: 700;
      margin: 0 0 1rem 0;
      padding-bottom: 1rem;
      border-bottom: 1px solid var(--color--border-eee);
    }

    .status-wrap {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .company-website {
        font-size: 0.75rem;
        
          .website-link {
            background-color: rgba(0, 0, 0, 0.03);
            color: var(--color--gray-999);
            text-decoration: underline !important;
            padding: 0.3rem 0.75rem;
            border-radius: 100px;
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            
            &::before {
              content: '';
              width: 0.75rem;
              height: 0.75rem;
              background-image: url('~@/assets/images/ic-link-solid.svg');
              background-size: contain;
              background-repeat: no-repeat;
              background-position: center;
              opacity: 0.7;
            }
            
            &:hover {
              color: var(--color--primary);
              
              &::before {
                opacity: 1;
              }
            }
          }
      }
    }

  }

  // Description Section
  .description-section {
    background-color: rgba(0, 0, 0, 0.03);
    padding: 1.5rem;
    border-radius: 0.5rem;
    
    .description-content {
      line-height: 1.5;
      margin-bottom: 1.5rem;
    }
  }

  // Related Products Section
  .related-products-section {
    .section-title {
      font-size: 1.25rem;
      font-weight: 700;
      margin-bottom: 1rem;
    }
    .card-wrap {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
      max-height: 700px;
      overflow-y: auto;
    }
  }

  @media screen and (max-width: 768px) {
    .main-content {
      flex-direction: column;
    }
  }

}
</style>
