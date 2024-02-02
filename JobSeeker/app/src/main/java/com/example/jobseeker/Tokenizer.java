package com.example.jobseeker;

import java.util.*;

/**
 * The Tokenizer class is designed to split an input string into tokens based on a comma delimiter.
 * <p>
 * This class is particularly useful for tokenizing comma-separated values.
 * </p>
 * @author Jiaqi Zhuang
 */
public class Tokenizer {

    private String input;

    public Tokenizer(String input) {
        this.input = input;
    }

    /**
     * Tokenizes the input string based on a comma delimiter.
     * Spaces following commas are also considered in the split.
     *
     * @return a list of tokens obtained by splitting the input string
     *         based on the comma delimiter.
     */
    public List<String> tokenize() {
        // Split the input string based on a comma followed by zero or more spaces
        return Arrays.asList(input.split(",\\s*"));
    }
}
