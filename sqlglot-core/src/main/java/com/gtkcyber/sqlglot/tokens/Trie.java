/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gtkcyber.sqlglot.tokens;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A trie (prefix tree) for efficient keyword lookup during tokenization.
 * Supports O(m) lookup where m is the length of the keyword.
 */
public class Trie {
    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        TokenType value = null;
        boolean isEnd = false;
    }

    private final Node root = new Node();

    /**
     * Builds a trie from a map of keywords to token types.
     */
    public static Trie build(Map<String, TokenType> keywords) {
        Objects.requireNonNull(keywords, "keywords cannot be null");
        Trie trie = new Trie();
        for (Map.Entry<String, TokenType> entry : keywords.entrySet()) {
            trie.insert(entry.getKey(), entry.getValue());
        }
        return trie;
    }

    /**
     * Inserts a keyword into the trie.
     */
    private void insert(String word, TokenType tokenType) {
        Node current = root;
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, ch -> new Node());
        }
        current.isEnd = true;
        current.value = tokenType;
    }

    /**
     * Looks up a keyword (case-insensitive) and returns the TokenType if found, null otherwise.
     */
    public TokenType lookup(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }
        return lookup(word, false);
    }

    /**
     * Looks up a keyword with optional case sensitivity.
     */
    public TokenType lookup(String word, boolean caseSensitive) {
        if (word == null || word.isEmpty()) {
            return null;
        }

        Node current = root;
        String searchWord = caseSensitive ? word : word.toUpperCase();

        for (char c : searchWord.toCharArray()) {
            Node next = current.children.get(c);
            if (next == null) {
                return null;
            }
            current = next;
        }

        return current.isEnd ? current.value : null;
    }

    /**
     * Looks up a keyword by character stream up to a maximum length.
     * Returns the matched TokenType and the actual length matched.
     */
    public LookupResult lookupStream(String input, int offset, int maxLength) {
        if (input == null || offset >= input.length()) {
            return LookupResult.NONE;
        }

        Node current = root;
        int length = 0;
        int maxLen = Math.min(maxLength, input.length() - offset);

        for (int i = 0; i < maxLen; i++) {
            char c = Character.toUpperCase(input.charAt(offset + i));
            Node next = current.children.get(c);
            if (next == null) {
                break;
            }
            current = next;
            length++;
            if (current.isEnd) {
                // Keep going to find the longest match
                // Store this as a potential result
            }
        }

        if (current.isEnd && current.value != null) {
            return new LookupResult(current.value, length);
        }

        return LookupResult.NONE;
    }

    /**
     * Returns true if the trie contains a prefix starting at the given offset.
     */
    public boolean hasPrefix(String input, int offset) {
        if (input == null || offset >= input.length()) {
            return false;
        }

        Node current = root;
        for (int i = offset; i < input.length(); i++) {
            char c = Character.toUpperCase(input.charAt(i));
            Node next = current.children.get(c);
            if (next == null) {
                return false;
            }
            current = next;
            if (current.isEnd) {
                return true;
            }
        }

        return false;
    }

    /**
     * Result of a lookup operation in the trie.
     */
    public static class LookupResult {
        public static final LookupResult NONE = new LookupResult(null, 0);

        public final TokenType type;
        public final int length;

        public LookupResult(TokenType type, int length) {
            this.type = type;
            this.length = length;
        }

        public boolean found() {
            return type != null;
        }
    }
}
