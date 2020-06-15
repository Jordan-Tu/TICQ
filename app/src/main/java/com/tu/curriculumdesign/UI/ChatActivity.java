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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.google.android.material.navigation.NavigationView;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.MsgAdapter;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.ChatRecord;
import com.tu.curriculumdesign.bean.Comment;
import com.tu.curriculumdesign.bean.Msg;
import com.tu.curriculumdesign.bean.Topic;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import static com.tu.curriculumdesign.application.MyApplication.getContext;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.tv_chat_chatName) TextView tv_chat_chatName;
    @BindView(R.id.et_message_input) EditText et_message_input;
    @BindView(R.id.bt_message_send) Button bt_message_send;
    @BindView(R.id.msg_recycler_view) RecyclerView msg_recycler_view;
    @BindView(R.id.chat_drawer_layout) DrawerLayout drawer_layout;
    @BindView(R.id.chat_navigation_view) NavigationView navigation_view;
    private List<Msg> msgList = new ArrayList<>();
    private List<ChatRecord> chatRecordList = new ArrayList<>();
    private MsgAdapter msgAdapter;
    private User chatUser;
    private Timer timer;

    private Handler handler = new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            List<ChatRecord> chatRecordListNew = (List<ChatRecord>)msg.obj;
            if( msg.what == 1 || chatRecordListNew.size()>chatRecordList.size()){
                chatRecordList = chatRecordListNew;
                Collections.sort(chatRecordList);
                for(int i=msgList.size();i<chatRecordList.size();i++){
                    System.out.println(chatRecordList.get(i).getSenderId().toString());
                    if(chatRecordList.get(i).getSenderId().equals(chatUser.getId())){
                        msgList.add(new Msg(chatRecordList.get(i).getContent(),Msg.TYPE_RECEIVED));
                    }else if(chatRecordList.get(i).getSenderId().equals(MyApplication.getCurrentUser().getId())){
                        msgList.add(new Msg(chatRecordList.get(i).getContent(),Msg.TYPE_SEND));
                    }
                }
                if(msg.what == 1){
                    msgAdapter = new MsgAdapter(msgList);
                    msg_recycler_view.setAdapter(msgAdapter);
                }else {
                    msgAdapter.notifyDataSetChanged();
                }
                msg_recycler_view.smoothScrollToPosition(msgList.size()>0?msgList.size()-1:0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        ButterKnife.bind(this);
        chatUser = (User) getIntent().getExtras().getSerializable("chatUser");
        init();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msg_recycler_view.setLayoutManager(linearLayoutManager);
        et_message_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }else{
                    msg_recycler_view.smoothScrollToPosition(msgList.size()>0?msgList.size()-1:0);
                }
            }
        });

        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("WrongConstant")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer_layout.closeDrawer(Gravity.RIGHT);
                if(item.getTitle().toString().equals("关于他")){
                    Intent intent = new Intent(ChatActivity.this, UserInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user",chatUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if(item.getTitle().toString().equals("解除好友关系")){
                    clickDelete();
                }
                return true;
            }
        });

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                TextView tv_userName = drawerView.findViewById(R.id.tv_userName);
                TextView tv_userEmail = drawerView.findViewById(R.id.tv_userEmail);
                tv_userName.setText(chatUser.getName());
                tv_userEmail.setText(chatUser.getEmail());
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {


            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    void init(){
        tv_chat_chatName.setText(chatUser.getName());
        requestChatData(true);
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                requestChatData();
            }
        }, 1000,1000);
        timer.cancel();
    }

    @OnClick(R.id.iv_back) void back(){
        finish();
    }


    protected void requestChatData(){
        requestChatData(false);
    }

    protected void requestChatData(boolean flag) {
        String controller = "/displayAllMessage";
        RequestBody requestBody = new FormBody.Builder()
                .add("senderId",MyApplication.getCurrentUser().getId().toString())
                .add("receiverId",chatUser.getId().toString())
                .build();
        HttpUtil.sendOkHttpRequest(controller,requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(MyApplication.getContext(), "网络繁忙！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                List<ChatRecord> chatRecordList = JSON.parseArray(response.body().string(),ChatRecord.class);
                Message obtain = Message.obtain();
                obtain.obj = chatRecordList;
                if(flag){
                    obtain.what = 1;
                }
                handler.sendMessage(obtain);
            }
        });
    }

    @OnClick(R.id.tv_chat_chatName) void openDrawerLayout(){
        drawer_layout.openDrawer(Gravity.RIGHT);
    }

    @OnClick(R.id.bt_message_send) void sendMessage() {
        String message = et_message_input.getText().toString();
        if(message.equals("")){
            Toast.makeText(ChatActivity.this, "内容不能为空哦！", Toast.LENGTH_SHORT).show();
        }else{
            sendMessage(message);
            et_message_input.clearFocus();
            et_message_input.setText("");
        }
    }


    private void sendMessage(String content){
        String controller = "/sendMessage";
        RequestBody requestBody = new FormBody.Builder()
                .add("senderId",MyApplication.getCurrentUser().getId().toString())
                .add("receiverId",chatUser.getId().toString())
                .add("content",content)
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(ChatActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                if (response.body().string().equals("true")) {
                    requestChatData();
                } else {
                    Toast.makeText(ChatActivity.this, "发送失败！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }

    private void clickDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否真的解除好友?");
        builder.setPositiveButton("执意解除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFriend();
            }
        });
        builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void deleteFriend(){
        String controller = "/deleteFriend";
        RequestBody requestBody = new FormBody.Builder()
                .add("senderId",MyApplication.getCurrentUser().getId().toString())
                .add("receiverId",chatUser.getId().toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(ChatActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                if (response.body().string().equals("true")) {
                    Toast.makeText(ChatActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                        timer.cancel();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    Toast.makeText(ChatActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }


}
