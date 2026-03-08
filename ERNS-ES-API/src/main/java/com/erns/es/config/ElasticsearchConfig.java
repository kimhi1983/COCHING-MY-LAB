package com.erns.es.config;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.protocol:https}")
    private String esProtocol;

    @Value("${spring.elasticsearch.host}")
    private String esHost;

    @Value("${spring.elasticsearch.port}")
    private int esPort;

    @Value("${spring.elasticsearch.username}")
    private String esUsername;

    @Value("${spring.elasticsearch.password}")
    private String esPassword;

    // RestClient 빈 등록 (별도로 관리)
    @Bean
    public RestClient restClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(esUsername, esPassword)
        );

        RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, esPort, esProtocol))
            .setHttpClientConfigCallback(httpClientBuilder -> {
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }}, new SecureRandom());

                    return httpClientBuilder
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier((hostname, session) -> true)
                            .setDefaultCredentialsProvider(credentialsProvider);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create SSL context for Elasticsearch client", e);
                }
            });

        return builder.build();
    }

    // ElasticsearchClient 빈 등록 (RestClient를 주입받음)
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
