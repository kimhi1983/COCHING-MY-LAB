package com.erns.es.search.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.erns.es.search.domain.EsManageDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EsManageService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final ElasticsearchClient client;

    @Autowired
    public EsManageService(RestClient restClient, ElasticsearchClient client) {
        this.objectMapper = new ObjectMapper();

        this.restClient = restClient;
        this.client = client;
    }

    /**
     * 인덱스 정보
     * 
     * @param param
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> getIndices(EsManageDTO param) throws IOException {
        String commandPath = "/_cat/indices";
        if (StringUtils.hasText(param.getCommands())) {
            commandPath += param.getCommands();
        }

        Request request = new Request("GET", commandPath);
        Response response = restClient.performRequest(request);

        try (Scanner scanner = new Scanner(response.getEntity().getContent()).useDelimiter("\\A")) {
            String responseBody = scanner.hasNext() ? scanner.next() : "";
            return objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {
            });
        }
    }

    /**
     * 특정 인덱스 삭제
     */
    public Map<String, Object> deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest.Builder()
                .index(indexName) // 삭제할 인덱스 지정
                .build();

        DeleteIndexResponse response = client.indices().delete(request);

        // 응답을 Map으로 변환하여 반환
        Map<String, Object> result = new HashMap<>();
        result.put("acknowledged", response.acknowledged());
        return result;
    }

    /**
     * 새로운 매핑을 적용하여 인덱스 생성
     */
    public Map<String, Object> createIndexWithMapping(String indexName, String mappingJson) throws IOException {
        Request request = new Request("PUT", "/" + indexName);
        request.setJsonEntity(mappingJson); // JSON 형식의 매핑 데이터 추가

        Response response = restClient.performRequest(request);

        try (Scanner scanner = new Scanner(response.getEntity().getContent()).useDelimiter("\\A")) {
            String responseBody = scanner.hasNext() ? scanner.next() : "";
            return objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
            });
        }
    }

    /**
     * ES Bulk 요청
     * @param jsonData
     * @return
     * @throws IOException
     */
    public Response bulkEsApi(String jsonData) throws IOException {
        // Bulk 요청 생성
        Request request = new Request("POST", "/_bulk");
        request.setJsonEntity(jsonData);

        // Elasticsearch 요청 실행
        Response response = restClient.performRequest(request);

        return response;
    }
}
