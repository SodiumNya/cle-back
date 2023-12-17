package com.example.clebackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParseUtils {

    public static Map<String, List<String>> parseList(String JsonString){
        try {
            ObjectMapper objectMapper =new ObjectMapper();
            return objectMapper.readValue(JsonString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> parse(String JsonString){
        try {
            ObjectMapper objectMapper =new ObjectMapper();
            return objectMapper.readValue(JsonString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> convertJsonArrayToList(String jsonString){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> list = objectMapper.readValue(jsonString, ArrayList.class);
            return list;
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);

        }

    }

    public static Map<String, List<String>> convertListToMap(List<Map<String, Object>> list) {
        Map<String, List<String>> resultMap = new HashMap<>();
        for (Map<String, Object> map : list) {
            String specs = (String) map.get("specs");
            String value = (String) map.get("value");

            if (!resultMap.containsKey(specs)) {
                resultMap.put(specs, new ArrayList<>());
            }

            resultMap.get(specs).add(value);
        }
        return resultMap;
    }

    public static String convertMapToJson(Map<String, List<String>> resultMap){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(resultMap);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }

    }
}
