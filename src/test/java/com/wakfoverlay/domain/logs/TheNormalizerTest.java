package com.wakfoverlay.domain.logs;

import org.junit.jupiter.api.Test;

import static com.wakfoverlay.domain.logs.TheNormalizer.normalize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TheNormalizerTest {

    @Test
    void testNormalizeNull() {
        assertNull(normalize(null), "Null input should return null");
    }

    @Test
    void testNormalizeEmpty() {
        assertEquals("", normalize(""), "Empty string should remain empty");
    }

    @Test
    void testNormalizeWhitespace() {
        assertEquals("", normalize("   "), "Whitespace only should return empty string");
        assertEquals("test", normalize("  test  "), "Leading and trailing whitespace should be removed");
    }

    @Test
    void testNormalizeLowerCase() {
        assertEquals("test", normalize("TEST"), "Text should be converted to lowercase");
        assertEquals("mixed case text", normalize("MiXeD cAsE tExT"), "Mixed case should be converted to lowercase");
    }

    @Test
    void testNormalizeAccents() {
        assertEquals("e", normalize("é"), "Accented characters should be normalized");
        assertEquals("a", normalize("à"), "Accented characters should be normalized");
        assertEquals("c", normalize("ç"), "Cedilla should be normalized");
        assertEquals("o", normalize("ö"), "Umlauts should be normalized");
    }

    @Test
    void testNormalizeFrenchText() {
        assertEquals("les elements francais", normalize("Les Éléments Français"),
                "French text with accents should be properly normalized");
    }

    @Test
    void testNormalizeSpecialCharacters() {
        // Special characters should remain unchanged
        assertEquals("test@example.com", normalize("TEST@example.com"),
                "Special characters should be preserved");
        assertEquals("test 123", normalize("TEST_123"),
                "Underscores and numbers should be preserved");
    }

    @Test
    void testNormalizeComplex() {
        assertEquals("voila un texte avec des accents et des caracteres speciaux!",
                normalize("  Voilà un TEXTE avec des ACCENTS et des caractères SPÉCIAUX!  "),
                "Complex text should be properly normalized");
    }

    @Test
    void testNormalizeNonLatinCharacters() {
        // Test with non-Latin characters (which should remain unchanged except for case)
        assertEquals("привет мир", normalize("Привет Мир"),
                "Cyrillic characters should be lowercased but not stripped");
        assertEquals("你好世界", normalize("你好世界"),
                "Chinese characters should remain unchanged");
    }

    @Test
    void testNormalizeMultipleSpaces() {
        assertEquals("multiple  spaces    test", TheNormalizer.normalize("multiple  spaces    test"),
                "Multiple spaces should be preserved");
    }

}
