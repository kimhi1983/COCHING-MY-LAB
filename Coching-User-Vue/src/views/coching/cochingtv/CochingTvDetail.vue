<template>
  <section id="coching-tv-detail">
    <div class="container h-100">
      <div class="content">
        <div class="common-content tv-detail-container"> 

          <!-- 메인 콘텐츠 영역 (왼쪽) -->
          <div class="main-content">
            <!-- 비디오 플레이어 -->
            <div class="video-player-container">
              <iframe 
                :src="embedUrl"
                :title="tvInfo.title" 
                frameborder="0" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                referrerpolicy="strict-origin-when-cross-origin" 
                allowfullscreen>
              </iframe>
            </div>
            
            <!-- 비디오 정보 -->
            <div class="video-info">
              <div class="video-title-section">
                <div class="title-meta-row">
                  <h1 class="video-title">{{tvInfo.title}}</h1>
                  <div class="video-meta">
                    <span class="views">{{(tvInfo.views || '0') | eFmtNum }}회</span>
                    <span class="separator">|</span>
                    <span class="date">{{tvInfo.ytDttm | eFmtDate('.')}}</span>
                  </div>
                </div>
                <button class="share-btn" @click="openShareModal">
                  <i class="share-icon"></i>
                  공유하기
                </button>
              </div>
              
              <div class="video-description">
                <p>{{tvInfo.content || '설명이 없습니다.'}}</p>
              </div>
            </div>
          </div>
          
          <!-- 추천 영상 영역 (오른쪽) -->
          <div class="recommended-videos">
            <h3 class="recommended-title">추천영상</h3>
            <div class="video-list">
                         <div 
                 v-for="video in recommendedVideos" 
                 :key="video.tvSeq"
                 class="video-card"
               >
                 <div class="video-thumbnail"
                  @click="onRecommendedVideoClick(video.tvSeq)"
                 >
                  <img :src="thumbnailUrl(video.ytUrl)" :alt="tvInfo.title"/>
                   <!-- <iframe 
                     :src="getEmbedUrl(video.ytUrl)"
                     :title="video.title" 
                     frameborder="0" 
                     allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                     referrerpolicy="strict-origin-when-cross-origin" 
                     allowfullscreen>
                   </iframe> -->
                 </div>
                
                <div class="video-info-card"
                  @click="onRecommendedVideoClick(video.tvSeq)"
                >
                  <h4 class="video-title-card">{{video.title}}</h4>
                  <div class="video-meta-card">
                    <span class="views-card">조회수 {{(video.views || '0') | eFmtNum }}회</span>
                    <span class="separator-card">•</span>
                    <span class="date-card">{{video.ytDttm | eFmtDate('.')}}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 공유 모달 -->
    <div ref="shareModal" class="modal share-modal" @click="closeShareModal">
      <div class="layer">
        <div class="layer-content">
          <div class="modal-inner modal-md" @click.stop>
            <div class="modal-content">
              <div class="modal-body p-t-0">
                <input 
                  type="text" 
                  :value="tvInfo.ytUrl" 
                  readonly 
                  class="url-input"
                  ref="urlInput"
                />
                <button class="copy-btn" @click="copyToClipboard">복사</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import ernsUtils from '@/components/mixins/ernsUtils';
import { getCochigtv, getRecommendedList, getYoutubeInfo } from '@/api/coching/comm/cochingtv';
import axios from "axios";

const DEF_TV = {
	tvSeq: 0,
	ytUrl: '',
	title: '',
	hashtag: '',
	views: '',
	ytDttm: '',
	useYn: 'Y',
	delYn: 'N',
	content: '',
	category: ''
};

export default {
  name: 'coching-tv-detail',
  mixins: [ernsUtils],
  components: {},
  props: {
    tvSeq: {
      type: [String, Number],
      default: "0"
    }
  },
  data() {
    return {
      tvInfo: { ...DEF_TV },
      recommendedVideos: [],
      showShareModal: false
    }
  },
  computed: {
    embedUrl() {
      const url = this.tvInfo.ytUrl;
      const match = url.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      if (match && match[1]) {
        return `https://www.youtube.com/embed/${match[1]}`;
      }
      return null;
    }
  },
  watch: {
    tvSeq(newVal, oldVal) {
      const _vm = this;
      _vm.fetchData();
    },
    '$i18n.locale' : async function(){
      const _vm = this;
      await _vm.loadCodes();
      _vm.$nextTick(() => {});
    },
  },
  async mounted(){
    const _vm = this;
    _vm.docReady();
    await _vm.fetchData();
  },
  beforeDestroy() {
    const _vm = this;
  },
  methods: {
    async fetchData(){
      const _vm = this;

      _vm.loading(true);
      try {
        await _vm.loadDetail();
        await _vm.getRecommendedVideos();
        await _vm.setYoutubeInfos();
      } catch (err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },   
    
    /* 코칭TV 상세 정보 가져오기 */
    async loadDetail() {
      const _vm = this;

      _vm.loading(true);
      try {
        const params = _vm.getCochigtvSearchParam();
        const tvRes = await getCochigtv(params);
        tvRes.resultData.views = '-';
        tvRes.resultData.ytDttm = '-';

        //데이터 바인딩
        _vm.tvInfo = {
          ...tvRes.resultData, 
        };

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    //검색조건
    getCochigtvSearchParam() {
      const _vm = this;

      const retParam = {
        tvSeq : _vm.tvSeq,
      };

      return retParam;
    },

    /* 추천 영상 가져오기 */
    async getRecommendedVideos(){
      const _vm = this;

      _vm.loading(true);
      try {
        const params = _vm.getRecommendedListSearchParam();
        const recommendedListRes = await getRecommendedList(params);
        
        //데이터 바인딩
        for (var video of recommendedListRes.resultData.list) {
          video.views = '-';
          video.ytDttm = '-';
        }

        _vm.recommendedVideos = recommendedListRes.resultData.list || [];

      } catch(err) {
        _vm.defaultApiErrorHandler(err); //기본에러처리
      } finally {
        _vm.loading(false);
      }
    },

    /* 추천 영상 검색조건 */
    getRecommendedListSearchParam() {
      const _vm = this;

      const retParam = {
        excludeSeq : _vm.tvInfo.tvSeq, //자기 자신 제외
        category : _vm.tvInfo.category, //카테고리 지정
        // rowSize : 5,
        useYn : 'Y',
        isRandom : 'Y',

      };

      return retParam;
    },

    /* 유튜브 정보 가져오기 */
    async setYoutubeInfos(){
      const _vm = this;
      
      // 상세 영상 먼저
      await _vm.setYoutubeInfo(_vm.tvInfo);

      // 추천 영상 병렬 처리
      const list = Array.isArray(_vm.recommendedVideos)
        ? _vm.recommendedVideos
        : Object.values(_vm.recommendedVideos || {});

      if (list.length === 0) return;

      try {
        await Promise.all(list.map(v => _vm.setYoutubeInfo(v)));
      } catch (err) {
        console.warn('추천 영상 YouTube 정보 가져오기 오류:', err);
      }
    },

    //유튜브 정보를 가져오고 셋팅
    async setYoutubeInfo(tvInfo){
      const params = {
        ytUrl : tvInfo.ytUrl,
      };
      const youtubeInfoRes = await getYoutubeInfo(params);
      const youtubeInfo = youtubeInfoRes.resultData;

      if(youtubeInfo) {
        tvInfo.views = youtubeInfo.viewCount;
        tvInfo.ytDttm = youtubeInfo.publishedAt;
      }
    },

    async loadCodes(){
      const _vm = this;
    },

    /* YouTube URL을 embed URL로 변환 */
    getEmbedUrl(ytUrl) {
      if (!ytUrl) return null;
      const match = ytUrl.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      if (match && match[1]) {
        return `https://www.youtube.com/embed/${match[1]}`;
      }
      return null;
    },

    thumbnailUrl(ytUrl) {
      const url = ytUrl;
      const match = url.match(/(?:youtu\.be\/|v=)([\w-]+)/);
      if (match && match[1]) {
        return `https://img.youtube.com/vi/${match[1]}/hqdefault.jpg`;
      }
      return null;
    },

    docReady(){
      const _vm = this;
    },

    onRecommendedVideoClick(tvSeq) {
      const _vm = this;
      
      const routeParam = {
        tvSeq: tvSeq,
      };

      _vm.ermPushPage({
        name: 'coching-tv-detail', 
        query: routeParam,
      });
    },

    /* 공유 모달 열기 */
    openShareModal() {
      const _vm = this;
      $(_vm.$refs["shareModal"]).fadeIn(300);
    },

    /* 공유 모달 닫기 */
    closeShareModal() {
      const _vm = this;
      $(_vm.$refs["shareModal"]).fadeOut(300);
    },

    /* 클립보드에 URL 복사 */
    async copyToClipboard() {
      const _vm = this;
      
      try {
        if (!_vm.tvInfo.ytUrl) {
          await _vm.alertError('복사할 URL이 없습니다.');
          return;
        }

        if (navigator.clipboard && window.isSecureContext) {
          await navigator.clipboard.writeText(_vm.tvInfo.ytUrl);
        }
        
        await _vm.alertSuccess('URL 복사 완료');
        _vm.closeShareModal();
      } catch (err) {
        console.error('클립보드 복사 실패:', err);
        await _vm.alertError('URL 복사 실패');
      }
    },

  }  
}
</script>

<style lang="scss" scoped>
.tv-detail-container {
  display: flex;
  gap: 24px;
}

/* 메인 콘텐츠 영역 (왼쪽) */
.main-content {
  flex: 3;
}

.video-player-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9; //16:9 비율
  margin-bottom: 20px;

  iframe {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border: none;
    border-radius: 0.5rem;
  }
}

.video-info {
}

.video-title-section {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
  
  .title-meta-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 16px;
    border-bottom: 1px solid var(--color--bk);
    padding-bottom: 1rem;
    margin-bottom: 1rem;
    
    .video-title {
      font-size: 1.5rem;
      font-weight: 700;
    }
    
    .video-meta {
      display: flex;
      align-items: center;
      gap: 8px;
      color: var(--color--gray-999);
      font-size: 0.875rem;
      
      .views {
      }
      
      .separator {
      }
      
      .date {
      }
    }
  }
  
  .share-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: var(--color--wh);
    border:  1px solid var(--color--gray-666);
    border-radius: 18px;
    color: var(--color--gray-666);
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.2s;
    align-self: flex-end;
    
    &:hover {
      background: #e5e5e5;
    }
    
    .share-icon {
      width: 16px;
      height: 16px;
      background-image: url(~@/assets/images/ic-share-gray.png);
      background-size: contain;
    }
  }
}

.video-description {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 8px;
  
  p {
    margin: 0;
    line-height: 1.6;
    font-size: 0.875rem;
    white-space: pre-line;
    overflow-wrap: anywhere;
  }
}

/* 추천 영상 영역 (오른쪽) */
.recommended-videos {
  flex: 1;
}

.recommended-title {
  font-size: 1.25rem;
  font-weight: 700;
  padding-bottom: 1rem;
  padding-left: 8px;
}

.video-list {
  height: 700px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.video-card {
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s;
  
  &:hover {
    background: #f2f2f2;
  }
}

.video-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9; //16:9 비율
  border-radius: 8px;
  background: #f2f2f2;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  iframe {
    width: 100%;
    height: 100%;
    border: none;
    border-radius: 0.5rem;
  }
}

.video-info-card {
  margin-top: 0.25rem;
}

.video-title-card {
  font-size: 0.875rem;
  font-weight: 600;
}

.video-meta-card {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: var(--color--gray-666);
  
  .views-card {
    color: var(--color--gray-666);
  }
  
  .separator-card {
    color: var(--color--gray-666); 
  }
  
  .date-card {
    color: var(--color--gray-666);
  }
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .tv-detail-container {
    padding: 0 1rem;
    flex-direction: column; /* 세로 방향으로 변경 */
  }
  
  .main-content {
    width: 100%;
  }
  
  .recommended-videos {
    width: 100%;
    padding-top: 2rem;
  }

  .video-list {
    height: auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2px;
  }

  .video-card {
    width: 100%;
    min-width: 0;
  }

}

/* 공유 모달 */
.share-modal {
  z-index: 1000 !important; //alert 모달보다 아래 위치

  .modal-body {
    display: flex;
  }
  .copy-btn {
    padding: 12px 20px;
    background: var(--color--blue);
    color: white;
    border-radius: 0 0.5rem 0.5rem 0;
    font-size: 0.875rem;
    font-weight: 500;
  }
  
  .copy-btn:hover {
    background: var(--color--blue);
    filter: brightness(0.8);
  }
}

</style> 