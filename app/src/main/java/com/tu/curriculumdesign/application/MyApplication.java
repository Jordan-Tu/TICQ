package com.tu.curriculumdesign.application;

import android.app.Application;
import android.content.Context;

import com.tu.curriculumdesign.bean.User;

import org.litepal.LitePal;

public class MyApplication extends Application {
    private static Context context;
    private static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
    }

    public static Context getContext(){
        return context;
    }

    public static void setCurrentUser(User user){
        currentUser = user;
    }

    public static User getCurrentUser(){
        return currentUser;
    }


}
