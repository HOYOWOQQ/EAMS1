package com.eams.utils;

import java.util.*;
import java.util.stream.Collectors;

import com.eams.Entity.score.QuestionUpsertReq;


public final class OptionNormalizer {
    private OptionNormalizer() {}

    /** 將 options 轉成 { "A": "...", "B": "...", ... } 的 LinkedHashMap（保序） */
    public static LinkedHashMap<String, String> toKeyMap(List<QuestionUpsertReq.OptionItem> items) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (items == null) return map;
        String[] letters = {"A","B","C","D","E","F","G","H"};
        int i = 0;
        for (QuestionUpsertReq.OptionItem it : items) {
            String key = (it.getKey() == null || it.getKey().isBlank()) ? letters[Math.min(i, letters.length-1)] : it.getKey().toUpperCase();
            map.put(key, it.getText() == null ? "" : it.getText());
            i++;
        }
        return map;
    }

    /** 從 AnswerPayload 推一個單值 key（例如 "A"），暫只處理單選 */
    public static String resolveAnswerKey(QuestionUpsertReq.AnswerPayload ans) {
        if (ans == null) return null;
        if (ans.getKey() != null && !ans.getKey().isBlank()) return ans.getKey().toUpperCase();
        if (ans.getIndex() != null) {
            String[] letters = {"A","B","C","D","E","F","G","H"};
            int idx = Math.max(0, Math.min(ans.getIndex(), letters.length-1));
            return letters[idx];
        }
        if (ans.getKeys() != null && !ans.getKeys().isEmpty()) {
            return ans.getKeys().get(0).toUpperCase(); // 先支援單選；多選可自行延伸
        }
        return null;
    }
}
