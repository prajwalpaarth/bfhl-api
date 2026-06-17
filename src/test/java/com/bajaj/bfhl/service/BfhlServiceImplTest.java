package com.bajaj.bfhl.service;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.bajaj.bfhl.dto.BfhlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BfhlServiceImplTest {

    private BfhlServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BfhlServiceImpl();
    }

    private BfhlRequest buildRequest(Object... items) {
        BfhlRequest req = new BfhlRequest();
        req.setData(Arrays.asList(items));
        return req;
    }

    // ---- Example 1 from spec ----
    @Test
    @DisplayName("Example 1: Basic mix of numbers, alphabets, special chars")
    void testExample1() {
        BfhlRequest req = buildRequest("A", "1", "22", "$", "B", "7");
        BfhlResponse res = service.process(req, "REQ-1001");

        assertTrue(res.isIs_success());
        assertEquals("REQ-1001", res.getRequest_id());
        assertTrue(res.getOdd_numbers().contains("1"));
        assertTrue(res.getOdd_numbers().contains("7"));
        assertTrue(res.getEven_numbers().contains("22"));
        assertTrue(res.getAlphabets().contains("A"));
        assertTrue(res.getAlphabets().contains("B"));
        assertTrue(res.getSpecial_characters().contains("$"));
        assertEquals("30", res.getSum());
        assertEquals("22", res.getLargest_number());
        assertEquals("1", res.getSmallest_number());
        assertEquals(2, res.getAlphabet_count());
        assertEquals(3, res.getNumber_count());
        assertEquals(1, res.getSpecial_character_count());
        assertFalse(res.getContains_duplicates());
        assertNotNull(res.getProcessing_time_ms());
    }

    // ---- Example 2: Alphanumeric strings ----
    @Test
    @DisplayName("Example 2: Alphanumeric string decomposition")
    void testExample2AlphanumericDecomposition() {
        BfhlRequest req = buildRequest("A1B2", "100", "#", "Test123", "Z", "55");
        BfhlResponse res = service.process(req, "REQ-1002");

        assertTrue(res.isIs_success());
        // Alphabets extracted from A1B2 -> A, B; Test123 -> T, E, S, T; Z
        assertTrue(res.getAlphabet_count() > 0);
        assertTrue(res.getNumber_count() > 0);
        assertEquals(1, res.getSpecial_character_count());
        assertFalse(res.getContains_duplicates());
    }

    // ---- Example 3: Duplicates, nulls, empty strings ----
    @Test
    @DisplayName("Example 3: Duplicates detected, nulls and empty strings ignored")
    void testExample3DuplicatesAndNulls() {
        BfhlRequest req = buildRequest("10", "10", "A", "A", "", null, "&", "5");
        BfhlResponse res = service.process(req, "REQ-1003");

        assertTrue(res.isIs_success());
        assertTrue(res.getContains_duplicates());
        assertTrue(res.getOdd_numbers().contains("5"));
        assertTrue(res.getEven_numbers().contains("10"));
        assertEquals(1, res.getAlphabets().size()); // deduped: A only once
        assertTrue(res.getSpecial_characters().contains("&"));
        assertEquals("15", res.getSum());
        assertEquals(4, res.getUnique_element_count()); // 10, A, &, 5
    }

    // ---- Example 4: Negative and decimal numbers ----
    @Test
    @DisplayName("Example 4: Negative and decimal number handling")
    void testExample4NegativeAndDecimal() {
        BfhlRequest req = buildRequest("-10", "25.5", "-100.75", "B", "@", "5", "A9");
        BfhlResponse res = service.process(req, "REQ-1004");

        assertTrue(res.isIs_success());
        assertEquals("25.5", res.getLargest_number());
        assertEquals("-100.75", res.getSmallest_number());
        assertFalse(res.getContains_duplicates());
        assertEquals(1, res.getSpecial_character_count());
        assertNotNull(res.getSum());
    }

    // ---- Vowel count ----
    @Test
    @DisplayName("Vowel count is correct")
    void testVowelCount() {
        BfhlRequest req = buildRequest("AEI", "OUB");
        BfhlResponse res = service.process(req, "REQ-VOWEL");

        // A, E, I from AEI = 3 vowels; O, U from OUB = 2 vowels → total 5
        assertEquals(5, res.getVowel_count());
    }

    // ---- Alphabet frequency ----
    @Test
    @DisplayName("Alphabet frequency map is populated correctly")
    void testAlphabetFrequency() {
        BfhlRequest req = buildRequest("AAB", "B");
        BfhlResponse res = service.process(req, "REQ-FREQ");

        assertNotNull(res.getAlphabet_frequency());
        assertEquals(2, res.getAlphabet_frequency().get("A"));
        assertEquals(2, res.getAlphabet_frequency().get("B"));
    }

    // ---- Longest / shortest alphabetic string ----
    @Test
    @DisplayName("Longest and shortest alphabetic values identified")
    void testLongestAndShortestAlpha() {
        BfhlRequest req = buildRequest("ABC", "DE", "F");
        BfhlResponse res = service.process(req, "REQ-LEN");

        assertEquals("ABC", res.getLongest_alphabetic_value());
        assertEquals("F", res.getShortest_alphabetic_value());
    }

    // ---- Summary object ----
    @Test
    @DisplayName("Summary object is correctly populated")
    void testSummaryObject() {
        BfhlRequest req = buildRequest("1", "A", null, "");
        BfhlResponse res = service.process(req, "REQ-SUMMARY");

        assertNotNull(res.getSummary());
        assertEquals(4, res.getSummary().getTotal_elements_received());
        assertEquals(2, res.getSummary().getValid_elements_processed());
        assertEquals(2, res.getSummary().getInvalid_elements_ignored());
    }

    // ---- Sorted numbers ----
    @Test
    @DisplayName("Sorted numbers returned in ascending order")
    void testSortedNumbers() {
        BfhlRequest req = buildRequest("100", "5", "50", "-10");
        BfhlResponse res = service.process(req, "REQ-SORT");

        List<String> sorted = res.getSorted_numbers();
        assertEquals(List.of("-10", "5", "50", "100"), sorted);
    }

    // ---- Empty data list ----
    @Test
    @DisplayName("Empty data list handled gracefully")
    void testEmptyData() {
        BfhlRequest req = buildRequest();
        BfhlResponse res = service.process(req, "REQ-EMPTY");

        assertTrue(res.isIs_success());
        assertTrue(res.getOdd_numbers().isEmpty());
        assertTrue(res.getEven_numbers().isEmpty());
        assertTrue(res.getAlphabets().isEmpty());
        assertEquals("0", res.getSum());
        assertFalse(res.getContains_duplicates());
    }

    // ---- All nulls ----
    @Test
    @DisplayName("All null inputs result in zero counts")
    void testAllNulls() {
        BfhlRequest req = buildRequest(null, null, null);
        BfhlResponse res = service.process(req, "REQ-NULLS");

        assertTrue(res.isIs_success());
        assertEquals(0, res.getNumber_count());
        assertEquals(0, res.getAlphabet_count());
        assertEquals(3, res.getSummary().getInvalid_elements_ignored());
    }

    // ---- Processing time is always set ----
    @Test
    @DisplayName("Processing time is always present and non-negative")
    void testProcessingTime() {
        BfhlRequest req = buildRequest("X", "1");
        BfhlResponse res = service.process(req, "REQ-TIME");
        assertTrue(res.getProcessing_time_ms() >= 0);
    }

    // ---- Special characters only ----
    @Test
    @DisplayName("Only special characters in input")
    void testOnlySpecialChars() {
        BfhlRequest req = buildRequest("@", "#", "$");
        BfhlResponse res = service.process(req, "REQ-SPECIAL");

        assertEquals(3, res.getSpecial_character_count());
        assertEquals(0, res.getNumber_count());
        assertEquals(0, res.getAlphabet_count());
    }
}
