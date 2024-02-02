package com.example.jobseeker;

import java.util.*;

/**
 * The Parser class is designed to parse a list of tokens and map them to
 * specific keywords such as 'keyword' and 'location'.
 * <p>
 * It expects the tokens list to have exactly two elements:
 * 1. A keyword.
 * 2. A location.
 * </p>
 * @author Jiaqi Zhuang
 */
public class Parser {

    private List<String> tokens;

    public Parser(List<String> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the list of tokens and maps them to specific keywords: 'keyword' and 'location'.
     * @return a map containing two key-value pairs mapping 'keyword' and 'location'
     *         to their respective values from the tokens list.
     * @throws IllegalArgumentException if the number of tokens is not exactly two.
     */
    public Map<String, String> parse() {
        Map<String, String> resultMap = new HashMap<>();

        // Ensure there are exactly two tokens
        if (tokens.size() != 2) {
            throw new IllegalArgumentException("Invalid number of tokens. Expected 2 tokens (keyword, location).");
        }

        // Map the first token to 'keyword' and the second to 'location'
        resultMap.put("keyword", tokens.get(0).trim());
        resultMap.put("location", tokens.get(1).trim());

        return resultMap;
    }
}

