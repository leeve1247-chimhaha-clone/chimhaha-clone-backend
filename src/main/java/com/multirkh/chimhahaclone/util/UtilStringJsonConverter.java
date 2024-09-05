package com.multirkh.chimhahaclone.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilStringJsonConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode jsonNodeOf(String jsonString) {
        JsonNode jsonNode = null;
        try {
            // ** JSON 형태의 String 문자열을 JsonNode 로 변환 **
            jsonNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            System.out.println("e = " + e);
        }
        return jsonNode;
    }

}