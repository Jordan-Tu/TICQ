package com.tu.curriculumdesign.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.NotificationAdapter;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.Notification;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;


public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.notice_recycler_view)
    RecyclerView notice_recycler_view;
    private NotificationAdapter notificationAdapter;

    private List<Notification> notificationList = new ArrayList<>();

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.left_layout) {
                int position = (Integer) findViewById(v.getId()).getTag();
                onclickItem(position);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Notification.TYPE_APPLICATION) {
                List<User> userList = (List<User>) msg.obj;
                for (int i = 0; i < userList.size(); i++) {
                    notificationList.add(new Notification(userList.get(i), Notification.TYPE_APPLICATION));
                }
                System.out.println("getFriendDeleteRequest()");
                getFriendDeleteRequest();
            } else if (msg.what == Notification.TYPE_DELETE) {
                List<User> userList1 = (List<User>) msg.obj;
                System.out.println("msg.what == Notification.TYPE_DELETE"+userList1.toString());
                for (int i = 0; i < userList1.size(); i++) {
                    notificationList.add(new Notification(userList1.get(i), Notification.TYPE_DELETE));
                }
                for (int i = 0; i < notificationList.size(); i++) {
                    System.out.println(notificationList.get(i).getType() + notificationList.get(i).getUser().toString());
                }
            }
            notificationAdapter = new NotificationAdapter(notificationList,mOnClick);
            notice_recycler_view.setAdapter(notificationAdapter);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);
        ButterKnife.bind(this);
        getFriendRequest();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        notice_recycler_view.setLayoutManager(linearLayoutManager);
    }



    @OnClick(R.id.iv_back)void back(){
        finish();
    }

    public void onclickItem(Integer id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请求:");
        builder.setMessage("是否同意添加好友?");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                operateFriendRequest(notificationList.get(id).getUser().getId(),true);
                notificationList.remove(id);
                notificationAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                operateFriendRequest(notificationList.get(id).getUser().getId(),false);
                notificationList.remove(id);
                notificationAdapter.notifyDataSetChanged();
            }
        });

        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void getFriendRequest() {
        String controller = "/displayFriendApplication";
        RequestBody requestBody = new FormBody.Builder()
                .add("receiverId",MyApplication.getCurrentUser().getId().toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(NotificationActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                List<User> userList = JSON.parseArray(result, User.class);
                System.out.println(userList.toString());
                Message message = Message.obtain();
                message.obj = userList;
                message.what = Notification.TYPE_APPLICATION;
                if (!userList.isEmpty()) {
                    Toast.makeText(NotificationActivity.this, "好友请求接收成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationActivity.this, "没有好友请求！", Toast.LENGTH_SHORT).show();
                }
                handler.sendMessage(message);
                Looper.loop();
            }
        });
    }


    private void getFriendDeleteRequest() {
        String controller = "/acceptDeleteFriendMessage";
        RequestBody requestBody = new FormBody.Builder()
                .add("receiverId",MyApplication.getCurrentUser().getId().toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(NotificationActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                List<User> userList = JSON.parseArray(result, User.class);
                System.out.println(userList.toString());
                Message message = Message.obtain();
                message.obj = userList;
                message.what = Notification.TYPE_DELETE;
                if (!userList.isEmpty()) {
                    Toast.makeText(NotificationActivity.this, "删除通知接收成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationActivity.this, "没有删除通知！", Toast.LENGTH_SHORT).show();
                }
                handler.sendMessage(message);
                Looper.loop();
            }
        });
    }

    public void operateFriendRequest(Integer id,boolean operation) {
        String controller;
        if(operation){
            controller = "/acceptFriendApplication";
        }else {
            controller = "/refuseFriendApplication";
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("receiverId",MyApplication.getCurrentUser().getId().toString())
                .add("senderId",id.toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(NotificationActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                if (result.equals("true")) {
                    Toast.makeText(NotificationActivity.this, operation?"你们成为好友了，快去聊天吧！":"您已拒绝！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationActivity.this, "失败了！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }
}
