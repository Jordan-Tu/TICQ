package com.tu.curriculumdesign.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.EditTextClearTools;
import com.tu.curriculumdesign.util.HttpUtil;
import com.tu.curriculumdesign.util.JudgeLeagle;
import com.tu.curriculumdesign.util.PasswordVisibilityTool;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_password) EditText et_password;
    @BindView(R.id.tv_register) TextView register;
    @BindView(R.id.iv_nameClear) ImageView nameClear;
    @BindView(R.id.iv_pwClear) ImageView pwClear;
    @BindView(R.id.iv_pwVis) ImageView pwVis;
    @BindView(R.id.remember_pw) CheckBox remember_pw;
    @BindView(R.id.auto_login) CheckBox auto_login;
    private ZLoadingDialog dialog = new ZLoadingDialog(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
        getPreference();
    }

    private void init(){
        LitePal.getDatabase();
        EditTextClearTools.addClearListener(et_email,nameClear);
        EditTextClearTools.addClearListener(et_password,pwClear);
        PasswordVisibilityTool.addVisibilityListener(et_password,pwVis);
    }

    @OnClick(R.id.btn_login) void login(){
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this,"登录信息不完善",Toast.LENGTH_SHORT).show();
        }else if(!JudgeLeagle.isEmail(email)){
            Toast.makeText(LoginActivity.this,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
        }else {
            dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#06AFFB"))//颜色
                    .setHintText("Loading...")
                    .setHintTextSize(20) // 设置字体大小 dp
                    .setHintTextColor(Color.parseColor("#06AFFB"))  // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setDialogBackgroundColor(Color.parseColor("#ffffff")) // 设置背景色，默认白色
                    .show();
            LoginRequest(email,password);
        }

    }

    @OnClick(R.id.tv_register) void toRegister(){
        Toast.makeText(LoginActivity.this,"注册",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    public void LoginRequest(final String email, final String password) {
        String controller = "/login";
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(LoginActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                dialog.cancel();
                Integer id = Integer.parseInt(response.body().string());
                if (id != -1) {
                    saveUser(id);
                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    addPreference(email,password);
                } else {
                    Toast.makeText(LoginActivity.this, "邮箱或密码错误！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }

    private void saveUser(Integer id){
        String controller = "/displayUserInfo";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id.toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(LoginActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String string = response.body().string();
                User loginUser = JSON.parseObject(string).toJavaObject(User.class);
                if (loginUser!=null) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    MyApplication.setCurrentUser(loginUser);
                    startActivity(intent);
                }
                Looper.loop();
            }
        });


    }

    private void getPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("TICQ",MODE_PRIVATE);
        boolean rememberPassword = sharedPreferences.getBoolean("REMEMBER_PASSWORD",false);
        boolean autoLogin = sharedPreferences.getBoolean("AUTO_LOGIN",false);
        String email = sharedPreferences.getString("USER_EMAIL","");
        et_email.setText(email);
        if(rememberPassword){
            String password = sharedPreferences.getString("USER_PASSWORD","");
            et_password.setText(password);
            remember_pw.setChecked(true);
            if(autoLogin){
                auto_login.setChecked(true);
                Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                addPreference(email,password);
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        }
    }

    private void addPreference(final String email, final String password){
        SharedPreferences sharedPreferences = getSharedPreferences("TICQ", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL",email);
        if(remember_pw.isChecked()){
            editor.putString("USER_PASSWORD",password);
            editor.putBoolean("REMEMBER_PASSWORD",true);
        }else{
            editor.putString("USER_PASSWORD","");
            editor.putBoolean("REMEMBER_PASSWORD",false);
        }
        if(auto_login.isChecked()){
            editor.putBoolean("AUTO_LOGIN",true);
        }else {
            editor.putBoolean("AUTO_LOGIN",false);
        }
        editor.commit();
    }
}
