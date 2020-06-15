package com.tu.curriculumdesign.UI;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.EditTextClearTools;
import com.tu.curriculumdesign.util.HttpUtil;
import com.tu.curriculumdesign.util.JudgeLeagle;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.et_userName) EditText et_username;
    @BindView(R.id.et_phone) EditText et_phone;
    @BindView(R.id.et_address) EditText et_address;
    @BindView(R.id.tv_userEmail) TextView tv_userEmail;
    @BindView(R.id.iv_userNameClear) ImageView iv_userNameClear;
    @BindView(R.id.iv_phoneClear) ImageView iv_phoneClear;
    @BindView(R.id.iv_addClear) ImageView iv_addClear;
    @BindView(R.id.ib_male) ImageButton ib_male;
    @BindView(R.id.ib_female) ImageButton ib_female;
    @BindView(R.id.ib_save) ImageButton ib_save;
    @BindView(R.id.fab) FloatingActionButton fab;
    private boolean male = false,female = false;
    private User currentUser;
    private Boolean flag;
    private ZLoadingDialog dialog = new ZLoadingDialog(UserInfoActivity.this);
    private String username;
    private String phone;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        currentUser = (User) intent.getExtras().getSerializable("user");
        flag = intent.getBooleanExtra("flag",false);
        init();
        initLayout(flag);
    }

    private void init(){
        tv_userEmail.setText(currentUser.getEmail());
        et_username.setText(currentUser.getName());
        et_phone.setText(currentUser.getPhone());
        et_address.setText(currentUser.getAddress());
        if(currentUser.getSex().equals("男")){
            male = true;
        }else {
            female = true;
        }
        drawSelected();
    }
    private void initLayout(boolean flag){
        if(flag){
            EditTextClearTools.addClearListener(et_username,iv_userNameClear);
            EditTextClearTools.addClearListener(et_phone,iv_phoneClear);
            EditTextClearTools.addClearListener(et_address,iv_addClear);
            ib_male.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(male){
                        male = false;
                    }else{
                        female = false;
                        male = true;
                    }
                    drawSelected();
                }
            });
            ib_female.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(female){
                        female = false;
                    }else{
                        male = false;
                        female = true;
                    }
                    drawSelected();
                }
            });
            ib_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyInfo();
                }
            });
        }else {
            et_username.setFocusable(false);
            et_phone.setFocusable(false);
            et_address.setFocusable(false);
            ib_female.setFocusable(false);
            ib_male.setFocusable(false);
            ib_save.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriendRequest(currentUser.getEmail());
                }
            });
        }
    }

    @OnClick(R.id.iv_back) void backMain(){
        username = et_username.getText().toString();
        phone = et_phone.getText().toString();
        address = et_address.getText().toString();
        if(!username.equals(currentUser.getName()) || !phone.equals(currentUser.getPhone()) || !address.equals(currentUser.getAddress()) || !(male?  "男" :"女").equals(currentUser.getSex())){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改:");
            builder.setMessage("是否保存修改?");
            builder.setPositiveButton("保存并退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    modifyInfo();
                }
            });
            builder.setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }else {
            finish();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void drawSelected(){
        if (male) {
            ib_male.setBackground(getResources().getDrawable(R.color.selected));
        } else {
            ib_male.setBackground(getResources().getDrawable(R.drawable.background));
        }
        if (female) {
            ib_female.setBackground(getResources().getDrawable(R.color.selected));
        } else {
            ib_female.setBackground(getResources().getDrawable(R.drawable.background));
        }
    }

    private void modifyInfo(){
        username = et_username.getText().toString();
        phone = et_phone.getText().toString();
        address = et_address.getText().toString();
        if(username.isEmpty() || phone.isEmpty() || address.isEmpty() || !(male||female)){
            Toast.makeText(UserInfoActivity.this,"请将信息填写完整",Toast.LENGTH_SHORT).show();
        }else if(!JudgeLeagle.isPhone(phone)){
            Toast.makeText(UserInfoActivity.this,"手机填写不正确",Toast.LENGTH_SHORT).show();
        }else{
            dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#06AFFB"))//颜色
                    .setHintText("Loading...")
                    .setHintTextSize(20) // 设置字体大小 dp
                    .setHintTextColor(Color.parseColor("#06AFFB"))  // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setDialogBackgroundColor(Color.parseColor("#ffffff")) // 设置背景色，默认白色
                    .show();
            modifyInfoRequest();
        }
    }

    private void modifyInfoRequest(){
        String controller = "/changeUserInfo";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", currentUser.getId().toString())
                .add("password", currentUser.getPassword())
                .add("name", username)
                .add("phone", phone)
                .add("address", address)
                .add("sex", male ? "男" : "女")
                .build();
        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller, "onFailure:" + e.getMessage());
                Toast.makeText(UserInfoActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                dialog.cancel();
                String string = response.body().string();
                System.out.println(string);
                if (!string.equals("true")) {
                    Toast.makeText(UserInfoActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                } else {
                    currentUser.setAddress(address);
                    currentUser.setName(username);
                    currentUser.setSex(male ? "男" : "女");
                    currentUser.setPhone(phone);
                    Toast.makeText(UserInfoActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    MyApplication.setCurrentUser(currentUser);

                }
                Looper.loop();
            }
        });
    }

    private void addFriendRequest(final String email) {
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
                Toast.makeText(UserInfoActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String result = response.body().string();
                if (result.equals("2")) {
                    Toast.makeText(UserInfoActivity.this, "请求发送成功！", Toast.LENGTH_SHORT).show();
                } else if(result.equals("1")){
                    Toast.makeText(UserInfoActivity.this, "你们已经是好友啦！", Toast.LENGTH_SHORT).show();
                }else if((result.equals("0"))){
                    Toast.makeText(UserInfoActivity.this, "你已发送好友请求啦！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserInfoActivity.this, "请求发送失败！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }
}
