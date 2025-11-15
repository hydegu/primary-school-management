package com.example.primaryschoolmanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Web Jackson 配置
 * 确保 HTTP 响应使用干净的 JSON（不包含 @class 类型信息）
 */
@Configuration
public class WebJacksonConfig {

    /**
     * Web 响应专用的 ObjectMapper
     * 标记为 @Primary，确保 Spring MVC 使用这个 ObjectMapper
     * 不包含类型信息，返回干净的 JSON 给前端
     */
    @Bean
    @Primary
    public ObjectMapper webObjectMapper() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .build();
        // 禁用时间戳格式，使用 ISO-8601 格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 不使用 activateDefaultTyping，确保返回干净的 JSON
        return mapper;
    }
}

