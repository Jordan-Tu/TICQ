package com.tu.curriculumdesign.bean;

public class Notification {
    public static final  int TYPE_APPLICATION = 0;
    public static final int TYPE_DELETE = 1;
    private User user;
    private int type;

    public Notification(User user,int type){
        this.user = user;
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public int getType() {
        return type;
    }
}
