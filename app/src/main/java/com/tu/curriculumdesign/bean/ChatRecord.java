package com.tu.curriculumdesign.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class ChatRecord implements Serializable,Comparable<ChatRecord> {
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Timestamp time;
    private Integer isRead;

    @Override
    public String toString() {
        return "ChatRecord{" +
                "senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", isRead=" + isRead +
                '}';
    }

    public ChatRecord() {
    }

    public ChatRecord(Integer senderId, Integer receiverId, String content, Timestamp time) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
        this.content = content;
        this.isRead = 0;
    }

    public ChatRecord(Integer senderId, Integer receiverId, String content, Timestamp time, Integer isRead) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    @Override
    public int compareTo(ChatRecord o) {
        return this.getTime().after(o.getTime())?1:-1;
    }
}