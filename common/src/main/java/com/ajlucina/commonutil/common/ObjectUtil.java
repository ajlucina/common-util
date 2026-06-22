package com.ajlucina.commonutil.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class ObjectUtil {

    private ObjectUtil() {}

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(JsonNode json) {
        try {
            return objectMapper.writeValueAsString(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectNode toObjectNode(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node instanceof ObjectNode objectNode) {
                return objectNode;
            }
            throw new IllegalArgumentException("JSON is not an object node");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper mapper() {
        return objectMapper;
    }
}