package com.cwjcsu.ybjj.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Locale;

@Configuration
public class Beans {

    /**
     * 通过 xml 配置这个 bean 指定日期格式比较麻烦
     */
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
