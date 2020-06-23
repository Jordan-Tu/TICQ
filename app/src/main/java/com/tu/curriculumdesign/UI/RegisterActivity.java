package com.tu.curriculumdesign.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jkb.vcedittext.VerificationCodeEditText;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.util.EditTextClearTools;
import com.tu.curriculumdesign.util.HttpUtil;
import com.tu.curriculumdesign.util.JudgeLeagle;
import com.tu.curriculumdesign.util.PasswordVisibilityTool;
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

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.et_userName) EditText et_username;
    @BindView(R.id.et_email) EditText et_email;
    @BindView(R.id.et_password) EditText et_password;
    @BindView(R.id.et_password1) EditText et_password1;
    @BindView(R.id.et_phone) EditText et_phone;
    @BindView(R.id.et_address) EditText et_address;
    @BindView(R.id.iv_userNameClear) ImageView iv_userNameClear;
    @BindView(R.id.iv_emailClear) ImageView iv_emailClear;
    @BindView(R.id.iv_pwClear) ImageView iv_pwClear;
    @BindView(R.id.iv_pwClear1) ImageView iv_pwClear1;
    @BindView(R.id.iv_pwVis) ImageView iv_pwVis;
    @BindView(R.id.iv_pwVis1) ImageView iv_pwVis1;
    @BindView(R.id.iv_phoneClear) ImageView iv_phoneClear;
    @BindView(R.id.iv_addClear) ImageView iv_addClear;
    @BindView(R.id.ib_male) ImageButton ib_male;
    @BindView(R.id.ib_female) ImageButton ib_female;
    private VerificationCodeEditText verificationCode;
    private Button btn_verify_confirm;
    private boolean male = false,female = false;
    private ZLoadingDialog dialog = new ZLoadingDialog(RegisterActivity.this);
    private String username;
    private String email;
    private String password;
    private String password1;
    private String phone;
    private String address;
    private String captcha;

    public static final int UPDATE_LAYOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        EditTextClearTools.addClearListener(et_username,iv_userNameClear);
        EditTextClearTools.addClearListener(et_email,iv_emailClear);
        EditTextClearTools.addClearListener(et_password,iv_pwClear);
        EditTextClearTools.addClearListener(et_password1,iv_pwClear1);
        EditTextClearTools.addClearListener(et_phone,iv_phoneClear);
        EditTextClearTools.addClearListener(et_address,iv_addClear);
        PasswordVisibilityTool.addVisibilityListener(et_password,iv_pwVis);
        PasswordVisibilityTool.addVisibilityListener(et_password1,iv_pwVis1);
    }

    @OnClick(R.id.ib_male) void selectMale(){
        if(male){
            male = false;
        }else{
            female = false;
            male = true;
        }
        drawSelected();
    }

    @OnClick(R.id.ib_female) void selectFemale(){
        if(female){
            female = false;
        }else{
            male = false;
            female = true;
        }
        drawSelected();
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


    @OnClick(R.id.tv_login) void login(){
        Toast.makeText(RegisterActivity.this,"登录",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }

    @OnClick(R.id.btn_register) void register(){
        username = et_username.getText().toString();
        email = et_email.getText().toString();
        password = et_password.getText().toString();
        password1 = et_password1.getText().toString();
        phone = et_phone.getText().toString();
        address = et_address.getText().toString();
        if(username.isEmpty() || email.isEmpty() || password.isEmpty() || password1.isEmpty() || phone.isEmpty() || address.isEmpty() || !(male||female)){
            Toast.makeText(RegisterActivity.this,"请将信息填写完整",Toast.LENGTH_SHORT).show();
        }else if(!JudgeLeagle.isEmail(email)){
            Toast.makeText(RegisterActivity.this,"邮箱格式不正确",Toast.LENGTH_SHORT).show();
        }else if(!JudgeLeagle.isPhone(phone)){
            Toast.makeText(RegisterActivity.this,"手机填写不正确",Toast.LENGTH_SHORT).show();
        }else if(!password.equals(password1)){
            Toast.makeText(RegisterActivity.this,"两次密码填写不一致",Toast.LENGTH_SHORT).show();
        }else{
            dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#06AFFB"))//颜色
                    .setHintText("Loading...")
                    .setHintTextSize(20) // 设置字体大小 dp
                    .setHintTextColor(Color.parseColor("#06AFFB"))  // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setDialogBackgroundColor(Color.parseColor("#ffffff")) // 设置背景色，默认白色
                    .show();
            getVerifyCode();
        }
    }

    private void getVerifyCode(){
        String controller = "/registerCaptcha";
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(RegisterActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                dialog.cancel();
                String responseString = response.body().string();
                if(responseString.equals("-1")){
                    Toast.makeText(RegisterActivity.this, "邮箱已被注册，请更换！", Toast.LENGTH_SHORT).show();
                }else if(responseString.equals("1")){
                    Toast.makeText(RegisterActivity.this, "验证码发送成功！", Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = UPDATE_LAYOUT;
                    handler.sendMessage(message);
                }else{
                    Toast.makeText(RegisterActivity.this, "验证码发送失败！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }

    private void RegisterRequest() {
        String controller = "/register";
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("name", username)
                .add("phone", phone)
                .add("address", address)
                .add("captcha", captcha)
                .add("sex", male ? "男" : "女")
                .build();
        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller, "onFailure:" + e.getMessage());
                Toast.makeText(RegisterActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                dialog.cancel();
                if (response.body().string().equals("-1")) {
                    Toast.makeText(RegisterActivity.this, "验证码错误！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
                Looper.loop();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_LAYOUT){
                setContentView(R.layout.verify_code);
                verificationCode = findViewById(R.id.verify_code);
                btn_verify_confirm = findViewById(R.id.btn_verify_confirm);
                btn_verify_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        captcha = verificationCode.getText().toString();
                        if (captcha.length() != 4) {
                            Toast.makeText(RegisterActivity.this, "验证码格式错误！", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.show();
                            RegisterRequest();
                        }
                    }
                });
            }
        }
    };

}
