package com.example.booksserver.config;

import lombok.Getter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@EnableWebMvc
@Configuration
public class AppConfig implements WebMvcConfigurer {
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

    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(40);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(4);

        CloseableHttpClient client = HttpClientBuilder.create().setConnectionManager(poolingHttpClientConnectionManager).build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);

        clientHttpRequestFactory.setConnectionRequestTimeout(500);
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(this::httpComponentsClientHttpRequestFactory)
                .setConnectTimeout(Duration.ofMillis(200))
                .build();
    }
}
