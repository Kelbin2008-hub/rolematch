package com.rolematch.engine.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * High-performance text tokenizer and normalizer tailored for technical resume parsing
 * and job specification analysis.
 */
public final class TextTokenizer {

    // Retains technical tokens such as c++, c#, .net, node.js, spring-boot, etc.
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(?i)[a-z0-9]+(?:[+#.-][a-z0-9]+)*|c\\+\\+|c#|\\.net");

    private TextTokenizer() {
    }

    /**
     * Splits raw text into normalized, stop-word filtered technical tokens.
     *
     * @param text Raw unformatted string
     * @return List of normalized lowercase tokens
     */
    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(text);

        while (matcher.find()) {
            String token = matcher.group().toLowerCase().trim();
            // Remove leading/trailing periods or hyphens that might linger
            token = token.replaceAll("^[-.]+|[-.]+$", "");

            if (token.length() > 1 || "c".equals(token) || "r".equals(token)) {
                if (!StopWordDictionary.isStopWord(token)) {
                    tokens.add(token);
                }
            }
        }

        return tokens;
    }
}
