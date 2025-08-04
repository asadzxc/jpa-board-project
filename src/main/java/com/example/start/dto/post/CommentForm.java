package com.example.start.dto.post;

public class CommentForm {
    private String content;
    private Long parentId;  //  대댓글일 경우 부모 댓글의 ID


    public CommentForm() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
