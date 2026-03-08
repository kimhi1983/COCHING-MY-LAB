<template>
  <div class="table-viewer-container" style="max-height: 800px; overflow-y: auto;">
    <div v-if="aiResult">
      <!-- 기본 정보 -->
      <b-card class="mb-3">
        <b-card-title>기본 정보</b-card-title>
        <b-table-simple hover responsive>
          <b-tbody>
            <b-tr>
              <b-td><strong>결과 코드</strong></b-td>
              <b-td>{{ aiResult.resultCode || '-' }}</b-td>
            </b-tr>
            <b-tr>
              <b-td><strong>메시지</strong></b-td>
              <b-td>{{ aiResult.resultFailMessage || '-' }}</b-td>
            </b-tr>
            <b-tr v-if="aiResult.processing_time">
              <b-td><strong>처리 시간</strong></b-td>
              <b-td>{{ aiResult.processing_time }}초</b-td>
            </b-tr>
            <b-tr v-if="aiResult.elasticsearch_connected !== undefined">
              <b-td><strong>Elasticsearch 연결</strong></b-td>
              <b-td>
                <b-badge :variant="aiResult.elasticsearch_connected ? 'success' : 'danger'">
                  {{ aiResult.elasticsearch_connected ? '연결됨' : '연결 안됨' }}
                </b-badge>
              </b-td>
            </b-tr>
            <b-tr v-if="aiResult.ingredients_found !== undefined">
              <b-td><strong>발견된 성분</strong></b-td>
              <b-td>{{ aiResult.ingredients_found }} / {{ aiResult.total_ingredients || 0 }}</b-td>
            </b-tr>
            <b-tr v-if="aiResult.request_id">
              <b-td><strong>요청 ID</strong></b-td>
              <b-td>{{ aiResult.request_id }}</b-td>
            </b-tr>
          </b-tbody>
        </b-table-simple>
      </b-card>

      <!-- 요청 데이터 -->
      <b-card v-if="aiResult.request_data">
        <b-card-title>요청 데이터</b-card-title>
        <b-table-simple hover responsive>
          <b-tbody>
            <b-tr v-if="aiResult.request_data.formulation">
              <b-td><strong>제형</strong></b-td>
              <b-td>{{ aiResult.request_data.formulation }}</b-td>
            </b-tr>
            <b-tr v-if="aiResult.request_data.direction">
              <b-td><strong>방향</strong></b-td>
              <b-td>{{ aiResult.request_data.direction }}</b-td>
            </b-tr>
            <b-tr v-if="aiResult.request_data.model_name">
              <b-td><strong>모델명</strong></b-td>
              <b-td>{{ aiResult.request_data.model_name }}</b-td>
            </b-tr>
            <b-tr v-if="aiResult.request_data.temperature">
              <b-td><strong>온도</strong></b-td>
              <b-td>{{ aiResult.request_data.temperature }}</b-td>
            </b-tr>
          </b-tbody>
        </b-table-simple>

        <!-- 요청 성분 목록 -->
        <div v-if="aiResult.request_data.ingredients && aiResult.request_data.ingredients.length > 0">
          <h6 class="mt-3 mb-2">요청 성분 목록</h6>
          <b-table-simple hover responsive striped>
            <b-thead>
              <b-tr>
                <b-th>ID</b-th>
                <b-th>성분명</b-th>
                <b-th>영문명</b-th>
              </b-tr>
            </b-thead>
            <b-tbody>
              <b-tr v-for="(ingredient, ingIndex) in aiResult.request_data.ingredients" :key="ingIndex">
                <b-td>{{ ingredient.id || '-' }}</b-td>
                <b-td>{{ ingredient.repName || ingredient.rep_name || '-' }}</b-td>
                <b-td>{{ ingredient.repNameEn || ingredient.rep_name_en || '-' }}</b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
        </div>
      </b-card>

      <hr/>

      <!-- 처방 데이터 -->
      <div v-if="aiResult.resultData">
        <!-- 개요 -->
        <b-card class="mb-3" v-if="aiResult.resultData.overview">
          <b-card-title>처방 개요</b-card-title>
          <b-card-text style="white-space: pre-wrap; line-height: 1.6;">{{ aiResult.resultData.overview }}</b-card-text>
        </b-card>

        <!-- 총 비율 -->
        <b-card class="mb-3" v-if="aiResult.resultData.total_percentage">
          <b-card-title>총 비율</b-card-title>
          <b-card-text>
            <h4 class="text-primary">{{ aiResult.resultData.total_percentage }}%</h4>
          </b-card-text>
        </b-card>

        <!-- 단계별 성분 (Phases) -->
        <b-card class="mb-3" v-if="aiResult.resultData.phases && aiResult.resultData.phases.length > 0">
          <b-card-title>단계별 성분 ({{ aiResult.resultData.phases.length }}단계)</b-card-title>
          <div v-for="(phase, phaseIndex) in aiResult.resultData.phases" :key="phaseIndex" class="mb-3">
            <h6 class="text-primary">{{ phase.phase_name || `단계 ${phaseIndex + 1}` }}</h6>
            <b-table-simple hover responsive striped>
              <b-thead>
                <b-tr>
                  <b-th>성분명</b-th>
                  <b-th>비율(%)</b-th>
                  <b-th>기능</b-th>
                  <b-th>ES ID</b-th>
                </b-tr>
              </b-thead>
              <b-tbody>
                <template v-if="phase.ingredients && phase.ingredients.length > 0">
                  <b-tr v-for="(ingredient, ingIndex) in phase.ingredients" :key="ingIndex">
                    <b-td>{{ ingredient.name || '-' }}</b-td>
                    <b-td class="text-right">{{ ingredient.percentage || '-' }}</b-td>
                    <b-td>{{ ingredient.function || '-' }}</b-td>
                    <b-td>{{ ingredient.es_id || '-' }}</b-td>
                  </b-tr>
                </template>
                <b-tr v-else>
                  <b-td colspan="4" class="text-center text-muted">성분 없음</b-td>
                </b-tr>
              </b-tbody>
            </b-table-simple>
            <small class="text-muted">온도: {{ phase.temperature || '미지정' }}</small>
          </div>
        </b-card>

        <!-- 상세 제조 과정 -->
        <b-card class="mb-3" v-if="aiResult.resultData.detailed_process && aiResult.resultData.detailed_process.length > 0">
          <b-card-title>상세 제조 과정 ({{ aiResult.resultData.detailed_process.length }}단계)</b-card-title>
          <b-table-simple hover responsive striped>
            <b-thead>
              <b-tr>
                <b-th width="8%">단계</b-th>
                <b-th width="8%">상</b-th>
                <b-th width="40%">설명</b-th>
                <b-th width="10%">온도</b-th>
                <b-th width="10%">시간</b-th>
                <b-th width="24%">참고사항</b-th>
              </b-tr>
            </b-thead>
            <b-tbody>
              <b-tr v-for="(process, processIndex) in aiResult.resultData.detailed_process" :key="processIndex">
                <b-td class="text-center">{{ process.step_number || processIndex + 1 }}</b-td>
                <b-td class="text-center">{{ process.phase || '-' }}</b-td>
                <b-td style="white-space: pre-wrap; line-height: 1.4;">{{ process.description || '-' }}</b-td>
                <b-td class="text-center">{{ process.temperature || '-' }}</b-td>
                <b-td class="text-center">{{ process.time || '-' }}</b-td>
                <b-td style="white-space: pre-wrap; line-height: 1.4;">{{ process.notes || '-' }}</b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
        </b-card>

        <!-- 품질 관리 -->
        <b-card class="mb-3" v-if="aiResult.resultData.quality_control">
          <b-card-title>품질 관리</b-card-title>
          <b-table-simple hover responsive>
            <b-tbody>
              <b-tr v-if="aiResult.resultData.quality_control.ph_range">
                <b-td><strong>pH 범위</strong></b-td>
                <b-td>{{ aiResult.resultData.quality_control.ph_range }}</b-td>
              </b-tr>
              <b-tr v-if="aiResult.resultData.quality_control.appearance">
                <b-td><strong>외관</strong></b-td>
                <b-td style="white-space: pre-wrap; line-height: 1.4;">{{ aiResult.resultData.quality_control.appearance }}</b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
          <div v-if="aiResult.resultData.quality_control.stability_test && aiResult.resultData.quality_control.stability_test.length > 0">
            <h6 class="mt-3 mb-2">안정성 시험</h6>
            <ul class="list-unstyled">
              <li v-for="(test, testIndex) in aiResult.resultData.quality_control.stability_test" :key="testIndex" class="mb-1">
                {{ test }}
              </li>
            </ul>
          </div>
        </b-card>

        <!-- 주의사항 -->
        <b-card class="mb-3" v-if="aiResult.resultData.precautions && aiResult.resultData.precautions.length > 0">
          <b-card-title>주의사항</b-card-title>
          <ul class="list-unstyled">
            <li v-for="(precaution, precautionIndex) in aiResult.resultData.precautions" :key="precautionIndex" class="mb-2">
              {{ precaution }}
            </li>
          </ul>
        </b-card>

        <!-- 보관 조건 및 유통기한 -->
        <b-card class="mb-3" v-if="aiResult.resultData.storage_conditions || aiResult.resultData.shelf_life">
          <b-card-title>보관 조건 및 유통기한</b-card-title>
          <b-table-simple hover responsive>
            <b-tbody>
              <b-tr v-if="aiResult.resultData.storage_conditions">
                <b-td><strong>보관 조건</strong></b-td>
                <b-td style="white-space: pre-wrap; line-height: 1.4;">{{ aiResult.resultData.storage_conditions }}</b-td>
              </b-tr>
              <b-tr v-if="aiResult.resultData.shelf_life">
                <b-td><strong>유통기한</strong></b-td>
                <b-td>{{ aiResult.resultData.shelf_life }}</b-td>
              </b-tr>
            </b-tbody>
          </b-table-simple>
        </b-card>

        <!-- 장단점 -->
        <b-card class="mb-3" v-if="(aiResult.resultData.pros && aiResult.resultData.pros.length > 0) || (aiResult.resultData.cons && aiResult.resultData.cons.length > 0)">
          <b-card-title>장단점 분석</b-card-title>
          <b-row>
            <b-col md="6" v-if="aiResult.resultData.pros && aiResult.resultData.pros.length > 0">
              <h6 class="text-success">장점</h6>
              <ul class="list-unstyled">
                <li v-for="(pro, proIndex) in aiResult.resultData.pros" :key="proIndex" class="mb-1">
                  {{ pro }}
                </li>
              </ul>
            </b-col>
            <b-col md="6" v-if="aiResult.resultData.cons && aiResult.resultData.cons.length > 0">
              <h6 class="text-danger">단점</h6>
              <ul class="list-unstyled">
                <li v-for="(con, conIndex) in aiResult.resultData.cons" :key="conIndex" class="mb-1">
                  {{ con }}
                </li>
              </ul>
            </b-col>
          </b-row>
        </b-card>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiPrscResultTable',
  props: {
    aiResult: {
      type: Object,
      default: null,
    },
  },
}
</script>

<style scoped>
.text-primary { font-weight: 600; }
</style>


