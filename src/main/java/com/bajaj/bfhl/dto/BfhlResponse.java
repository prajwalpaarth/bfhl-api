package com.bajaj.bfhl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BfhlResponse {

    private boolean is_success;
    private String request_id;

    // Categorized arrays
    private List<String> odd_numbers;
    private List<String> even_numbers;
    private List<String> alphabets;
    private List<String> special_characters;

    // Numeric analytics
    private String sum;
    private String largest_number;
    private String smallest_number;
    private List<String> sorted_numbers;

    // Counts
    private Integer alphabet_count;
    private Integer number_count;
    private Integer special_character_count;
    private Integer vowel_count;
    private Integer unique_element_count;

    // Duplicate info
    private Boolean contains_duplicates;

    // Alphabet analytics
    private Map<String, Integer> alphabet_frequency;
    private String longest_alphabetic_value;
    private String shortest_alphabetic_value;

    // Timing
    private Long processing_time_ms;

    // Summary
    private Summary summary;

    // --- Getters & Setters ---

    public boolean isIs_success() { return is_success; }
    public void setIs_success(boolean is_success) { this.is_success = is_success; }

    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }

    public List<String> getOdd_numbers() { return odd_numbers; }
    public void setOdd_numbers(List<String> odd_numbers) { this.odd_numbers = odd_numbers; }

    public List<String> getEven_numbers() { return even_numbers; }
    public void setEven_numbers(List<String> even_numbers) { this.even_numbers = even_numbers; }

    public List<String> getAlphabets() { return alphabets; }
    public void setAlphabets(List<String> alphabets) { this.alphabets = alphabets; }

    public List<String> getSpecial_characters() { return special_characters; }
    public void setSpecial_characters(List<String> special_characters) { this.special_characters = special_characters; }

    public String getSum() { return sum; }
    public void setSum(String sum) { this.sum = sum; }

    public String getLargest_number() { return largest_number; }
    public void setLargest_number(String largest_number) { this.largest_number = largest_number; }

    public String getSmallest_number() { return smallest_number; }
    public void setSmallest_number(String smallest_number) { this.smallest_number = smallest_number; }

    public List<String> getSorted_numbers() { return sorted_numbers; }
    public void setSorted_numbers(List<String> sorted_numbers) { this.sorted_numbers = sorted_numbers; }

    public Integer getAlphabet_count() { return alphabet_count; }
    public void setAlphabet_count(Integer alphabet_count) { this.alphabet_count = alphabet_count; }

    public Integer getNumber_count() { return number_count; }
    public void setNumber_count(Integer number_count) { this.number_count = number_count; }

    public Integer getSpecial_character_count() { return special_character_count; }
    public void setSpecial_character_count(Integer special_character_count) { this.special_character_count = special_character_count; }

    public Integer getVowel_count() { return vowel_count; }
    public void setVowel_count(Integer vowel_count) { this.vowel_count = vowel_count; }

    public Integer getUnique_element_count() { return unique_element_count; }
    public void setUnique_element_count(Integer unique_element_count) { this.unique_element_count = unique_element_count; }

    public Boolean getContains_duplicates() { return contains_duplicates; }
    public void setContains_duplicates(Boolean contains_duplicates) { this.contains_duplicates = contains_duplicates; }

    public Map<String, Integer> getAlphabet_frequency() { return alphabet_frequency; }
    public void setAlphabet_frequency(Map<String, Integer> alphabet_frequency) { this.alphabet_frequency = alphabet_frequency; }

    public String getLongest_alphabetic_value() { return longest_alphabetic_value; }
    public void setLongest_alphabetic_value(String longest_alphabetic_value) { this.longest_alphabetic_value = longest_alphabetic_value; }

    public String getShortest_alphabetic_value() { return shortest_alphabetic_value; }
    public void setShortest_alphabetic_value(String shortest_alphabetic_value) { this.shortest_alphabetic_value = shortest_alphabetic_value; }

    public Long getProcessing_time_ms() { return processing_time_ms; }
    public void setProcessing_time_ms(Long processing_time_ms) { this.processing_time_ms = processing_time_ms; }

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }
    private String mobile_no;

    public String getMobile_no() { return mobile_no; }
    public void setMobile_no(String mobile_no) { this.mobile_no = mobile_no; }
    // --- Nested Summary class ---
    public static class Summary {
        private int total_elements_received;
        private int valid_elements_processed;
        private int invalid_elements_ignored;

        public Summary(int total, int valid, int invalid) {
            this.total_elements_received = total;
            this.valid_elements_processed = valid;
            this.invalid_elements_ignored = invalid;
        }

        public int getTotal_elements_received() { return total_elements_received; }
        public int getValid_elements_processed() { return valid_elements_processed; }
        public int getInvalid_elements_ignored() { return invalid_elements_ignored; }
    }
}
