package com.cineman.model.dto;

public class SearchCriteria9 {
    private String keyword;

    public SearchCriteria9() {}

    public SearchCriteria9(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String normalize() {
        return keyword == null ? "" : keyword.trim().toLowerCase();
    }

    public boolean isValid() {
        return keyword != null && !keyword.trim().isEmpty();
    }
}
