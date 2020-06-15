package com.tu.curriculumdesign.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class TestActivity extends AppCompatActivity {
    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.tv_request) TextView tv_request;
    private List<User> userList = new ArrayList<>();


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            userList = (List<User>) msg.obj;
            String string = "";
            for(int i=0 ;i<userList.size();i++){
                string += userList.get(i).getEmail();
            }
            tv_request.setText(string);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_addFriend)void addFriend(){
        String email = et_email.getText().toString();
        addFriendRequest(email);
    }

    @OnClick(R.id.bt_receiveFriend)void receiveFriend(){
        getFriendRequest();
    }

    @OnClick(R.id.bt_acceptFriend)void acceptFriend(){
        acceptFriendRequest();
    }

    public void addFriendRequest(final String email) {
        String controller = "/addFriend";
        RequestBody requestBody = new FormBody.Builder()
                .add("senderId",MyApplication.getCurrentUser().getId().toString())
                .add("receiverEmail",email)
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(TestActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                if (result.equals("true")) {
                    Toast.makeText(TestActivity.this, "请求发送成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestActivity.this, "你们已经是好友啦！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }

    public void getFriendRequest() {
        String controller = "/displayFriendApplication";
        RequestBody requestBody = new FormBody.Builder()
                .add("receiverId",MyApplication.getCurrentUser().getId().toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(TestActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                List<User> userList = JSON.parseArray(result, User.class);
                if (!userList.isEmpty()) {
                    Message obtain = Message.obtain();
                    obtain.obj = userList;
                    handler.sendMessage(obtain);
                    Toast.makeText(TestActivity.this, "接收成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestActivity.this, "没有好友请求！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }

    public void acceptFriendRequest() {
        String controller = "/acceptFriendApplication";
        RequestBody requestBody = new FormBody.Builder()
                .add("receiverId",MyApplication.getCurrentUser().getId().toString())
                .add("senderId",userList.get(0).getId().toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(TestActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                if (result.equals("true")) {
                    Toast.makeText(TestActivity.this, "你们成为好友了，快去聊天吧！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestActivity.this, "失败了！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }
}
