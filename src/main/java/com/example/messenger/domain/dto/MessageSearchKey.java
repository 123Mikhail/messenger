package com.example.messenger.domain.dto;

import java.util.Objects;

public class MessageSearchKey {
    private final String chatTitle;
    private final String keyword;
    private final int pageNumber;
    private final int pageSize;

    public MessageSearchKey(String chatTitle, String keyword, int pageNumber, int pageSize) {
        this.chatTitle = chatTitle;
        this.keyword = keyword;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageSearchKey that = (MessageSearchKey) o;
        return pageNumber == that.pageNumber &&
                pageSize == that.pageSize &&
                Objects.equals(chatTitle, that.chatTitle) &&
                Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatTitle, keyword, pageNumber, pageSize);
    }
}