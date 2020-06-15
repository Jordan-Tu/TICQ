package com.tu.curriculumdesign.bean;


import java.sql.Timestamp;

public class Comment {
    private Integer id;
    private String content;
    private Integer userId;
    private Integer topicId;
    private Timestamp time;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", topicId=" + topicId +
                ", time=" + time +
                '}';
    }

    public Comment(){
    }

    public Comment(Integer id, String content, Integer userId, Integer topicId, Timestamp time) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.topicId = topicId;
        this.time = time;
    }

    public Comment(String content, Integer userId, Integer topicId, Timestamp time) {
        this.content = content;
        this.userId = userId;
        this.topicId = topicId;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
