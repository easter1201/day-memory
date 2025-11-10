package com.daymemory.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

@Configuration
public class XssConfig {

    /**
     * XSS 방지를 위한 JSON 역직렬화 설정
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new XssStringDeserializer());
        objectMapper.registerModule(module);

        converter.setObjectMapper(objectMapper);
        return converter;
    }

    /**
     * String 타입의 입력값에 대해 HTML 이스케이프 처리
     */
    public static class XssStringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            String value = jsonParser.getValueAsString();
            if (value == null) {
                return null;
            }
            // HTML 특수 문자를 이스케이프 처리
            return HtmlUtils.htmlEscape(value);
        }
    }
}
