package com.example.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class RestHighLevelClientConfig {
    @Value("${community.elasticsearch.url}")
    private String esUrl;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(esUrl)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
