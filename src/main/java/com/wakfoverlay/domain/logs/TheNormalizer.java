package com.wakfoverlay.domain.logs;

import java.text.Normalizer;

public class TheNormalizer {
    // TODO: Make it not static
    public static String normalize(String text) {
        if (text == null) return null;
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }
}
