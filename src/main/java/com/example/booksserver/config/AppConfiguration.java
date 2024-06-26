package com.example.booksserver.config;

import lombok.Getter;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class AppConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost")
                .allowCredentials(true);
    }

    @Getter
    @Value("${server.address}:${server.port}")
    private String fullServerAddress;

    @Getter
    @Value("${mapping.image-path}")
    private String imageMapping;

    @Value("${rest-template.size.pool-size}")
    private int requestPoolMaxSize;

    @Value("${rest-template.timeout.connect}")
    private int connectTimeout;
    @Value("${rest-template.timeout.socket}")
    private int socketTimeout;
    @Value("${rest-template.timeout.pool}")
    private int connectionRequestTimeout;

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(requestPoolMaxSize);
        poolingHttpClientConnectionManager.setDefaultConnectionConfig(
                ConnectionConfig
                        .custom()
                        .setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
                        .setSocketTimeout(Timeout.ofMilliseconds(socketTimeout))
                        .build()
        );

        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
                        .build()
                )
                .build();

        return new HttpComponentsClientHttpRequestFactory(client);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpComponentsClientHttpRequestFactory());
    }
}
