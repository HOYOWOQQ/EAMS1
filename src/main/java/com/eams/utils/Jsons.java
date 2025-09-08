package com.eams.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Jsons {
    private static final ObjectMapper M = new ObjectMapper();
    private Jsons() {}

    public static String toJson(Object o) {
        try { return (o == null ? null : M.writeValueAsString(o)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    public static <T> T fromJson(String s, Class<T> clz) {
        try { return (s == null ? null : M.readValue(s, clz)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
    public static <T> T fromJson(String s, TypeReference<T> type) {
        try { return (s == null ? null : M.readValue(s, type)); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
