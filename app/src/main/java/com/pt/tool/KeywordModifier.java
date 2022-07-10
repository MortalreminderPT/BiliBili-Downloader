package com.pt.tool;

public class KeywordModifier {
    public static String checkAndModify(String strs) {
        return strs.replaceAll("[/\\:*\"<>|?]", "-");
    }
}
