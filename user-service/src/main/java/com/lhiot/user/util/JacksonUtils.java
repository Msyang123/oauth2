package com.lhiot.user.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Objects;

public class JacksonUtils {
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules();

    public static String toJson(Object object) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        return mapper.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> type) throws IOException, IOException {
        if (Objects.isNull(json)||"".equals(json)) {
            return null;
        }
        return mapper.readValue(json, type);
    }
}
