package com.rolematch.engine.nlp;

import java.util.Set;

/**
 * Standard English stop words dictionary for Natural Language Processing in ATS tokenization.
 */
public final class StopWordDictionary {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "arent", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
            "but", "by", "can", "cannot", "could", "did", "do", "does", "doing", "dont", "down", "during",
            "each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "her", "here",
            "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "it",
            "its", "itself", "lets", "me", "more", "most", "my", "myself", "no", "nor", "not", "of",
            "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out",
            "over", "own", "same", "she", "should", "so", "some", "such", "than", "that", "the",
            "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those",
            "through", "to", "too", "under", "until", "up", "very", "was", "we", "were", "what",
            "when", "where", "which", "while", "who", "whom", "why", "with", "would", "you", "your",
            "yours", "yourself", "yourselves"
    );

    private StopWordDictionary() {
    }

    public static boolean isStopWord(String word) {
        if (word == null) return false;
        return STOP_WORDS.contains(word.toLowerCase());
    }

    public static Set<String> getStopWords() {
        return STOP_WORDS;
    }
}
