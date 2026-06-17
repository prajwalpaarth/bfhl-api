package com.bajaj.bfhl.service;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.bajaj.bfhl.dto.BfhlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BfhlServiceImpl implements BfhlService {

    private static final Logger log = LoggerFactory.getLogger(BfhlServiceImpl.class);
    private static final Set<Character> VOWELS = Set.of('A','E','I','O','U');

    @Override
    public BfhlResponse process(BfhlRequest request, String requestId) {
        long startTime = System.currentTimeMillis();
        log.info("Processing request with id: {}", requestId);

        List<Object> rawData = request.getData();
        int totalReceived = (rawData == null) ? 0 : rawData.size();

        // Step 1: Filter out null / empty / whitespace-only
        List<String> validRaw = new ArrayList<>();
        int invalidCount = 0;
        if (rawData != null) {
            for (Object item : rawData) {
                if (item == null) { invalidCount++; continue; }
                String s = item.toString().trim();
                if (s.isEmpty()) { invalidCount++; continue; }
                validRaw.add(s);
            }
        }

        // Step 2: Detect duplicates BEFORE dedup
        Set<String> seen = new HashSet<>();
        boolean containsDuplicates = false;
        for (String s : validRaw) {
            if (!seen.add(s)) { containsDuplicates = true; break; }
        }

        // Step 3: Deduplicate (preserve order)
        List<String> dedupedData = validRaw.stream()
                .distinct()
                .collect(Collectors.toList());

        int uniqueElementCount = dedupedData.size();

        // Step 4: Categorize
        List<BigDecimal> numericValues = new ArrayList<>();
        List<String> alphabeticStrings = new ArrayList<>();  // whole alpha strings
        List<String> individualAlphabets = new ArrayList<>(); // for alphabet_count / freq
        List<String> specialCharacters = new ArrayList<>();

        for (String element : dedupedData) {
            if (isNumeric(element)) {
                numericValues.add(new BigDecimal(element));
            } else if (isAlphaOnly(element)) {
                alphabeticStrings.add(element.toUpperCase());
                for (char c : element.toUpperCase().toCharArray()) {
                    individualAlphabets.add(String.valueOf(c));
                }
            } else if (isAlphanumeric(element)) {
                // Extract digits and letters separately
                StringBuilder digits = new StringBuilder();
                StringBuilder letters = new StringBuilder();
                for (char c : element.toCharArray()) {
                    if (Character.isDigit(c)) digits.append(c);
                    else if (Character.isLetter(c)) letters.append(Character.toUpperCase(c));
                }
                if (digits.length() > 0) numericValues.add(new BigDecimal(digits.toString()));
                if (letters.length() > 0) {
                    for (char c : letters.toString().toCharArray()) {
                        individualAlphabets.add(String.valueOf(c));
                    }
                    alphabeticStrings.add(letters.toString());
                }
            } else if (isSpecialChar(element)) {
                specialCharacters.add(element);
            }
        }

        // Step 5: Numeric analytics
        List<BigDecimal> sortedNums = numericValues.stream()
                .sorted()
                .collect(Collectors.toList());

        List<String> oddNumbers = new ArrayList<>();
        List<String> evenNumbers = new ArrayList<>();
        for (BigDecimal num : numericValues) {
            // Only integer-valued numbers are odd/even
            if (num.stripTrailingZeros().scale() <= 0) {
                long longVal = num.longValueExact();
                if (Math.abs(longVal) % 2 == 0) evenNumbers.add(formatNumber(num));
                else oddNumbers.add(formatNumber(num));
            } else {
                // decimal — treat as even by convention (not specified, so put in even)
                evenNumbers.add(formatNumber(num));
            }
        }

        BigDecimal sum = numericValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        String largest = sortedNums.isEmpty() ? null : formatNumber(sortedNums.get(sortedNums.size()-1));
        String smallest = sortedNums.isEmpty() ? null : formatNumber(sortedNums.get(0));
        List<String> sortedNumberStrings = sortedNums.stream().map(this::formatNumber).collect(Collectors.toList());

        // Step 6: Alphabet analytics
        int vowelCount = 0;
        Map<String, Integer> freqMap = new LinkedHashMap<>();
        for (String a : individualAlphabets) {
            freqMap.merge(a, 1, Integer::sum);
            if (VOWELS.contains(a.charAt(0))) vowelCount++;
        }

        String longestAlpha = alphabeticStrings.stream()
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
        String shortestAlpha = alphabeticStrings.stream()
                .min(Comparator.comparingInt(String::length))
                .orElse(null);

        // Step 7: Build response
        BfhlResponse response = new BfhlResponse();
        response.setIs_success(true);
        response.setRequest_id(requestId);
        response.setOdd_numbers(oddNumbers);
        response.setEven_numbers(evenNumbers);
        response.setAlphabets(alphabeticStrings);
        response.setSpecial_characters(specialCharacters);
        response.setSum(formatSum(sum));
        if (largest != null) response.setLargest_number(largest);
        if (smallest != null) response.setSmallest_number(smallest);
        response.setSorted_numbers(sortedNumberStrings);
        response.setAlphabet_count(individualAlphabets.size());
        response.setNumber_count(numericValues.size());
        response.setSpecial_character_count(specialCharacters.size());
        response.setVowel_count(vowelCount);
        response.setUnique_element_count(uniqueElementCount);
        response.setContains_duplicates(containsDuplicates);
        if (!freqMap.isEmpty()) response.setAlphabet_frequency(freqMap);
        if (longestAlpha != null) response.setLongest_alphabetic_value(longestAlpha);
        if (shortestAlpha != null) response.setShortest_alphabetic_value(shortestAlpha);
        response.setProcessing_time_ms(System.currentTimeMillis() - startTime);
        response.setSummary(new BfhlResponse.Summary(totalReceived, uniqueElementCount, invalidCount + (validRaw.size() - uniqueElementCount)));

        log.info("Request {} processed in {}ms", requestId, response.getProcessing_time_ms());
        return response;
    }

    // --- Helpers ---

    private boolean isNumeric(String s) {
        try { new BigDecimal(s); return true; } catch (NumberFormatException e) { return false; }
    }

    private boolean isAlphaOnly(String s) {
        return s.chars().allMatch(Character::isLetter);
    }

    private boolean isAlphanumeric(String s) {
        boolean hasLetter = s.chars().anyMatch(Character::isLetter);
        boolean hasDigit = s.chars().anyMatch(Character::isDigit);
        boolean allAlphanumeric = s.chars().allMatch(c -> Character.isLetter(c) || Character.isDigit(c));
        return hasLetter && hasDigit && allAlphanumeric;
    }

    private boolean isSpecialChar(String s) {
        return s.chars().noneMatch(Character::isLetterOrDigit);
    }

    private String formatNumber(BigDecimal bd) {
        // Remove trailing zeros but keep decimals when needed
        BigDecimal stripped = bd.stripTrailingZeros();
        if (stripped.scale() <= 0) {
            return stripped.toBigIntegerExact().toString();
        }
        return stripped.toPlainString();
    }

    private String formatSum(BigDecimal sum) {
        if (sum.compareTo(BigDecimal.ZERO) == 0) return "0";
        BigDecimal stripped = sum.stripTrailingZeros();
        if (stripped.scale() <= 0) {
            return stripped.toBigIntegerExact().toString();
        }
        return stripped.toPlainString();
    }
}
