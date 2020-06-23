package com.tu.curriculumdesign.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.util.HttpUtil;
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

public class PostActivity extends BaseActivity {
    @BindView(R.id.et_new_title) EditText et_new_title;
    @BindView(R.id.et_new_content) EditText et_new_content;
    private ZLoadingDialog dialog = new ZLoadingDialog(PostActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ib_back) void backMain(){
        startActivity(new Intent(PostActivity.this,MainActivity.class));
    }

    @OnClick(R.id.ib_edit_ok)void post(){
        String title = et_new_title.getText().toString();
        String content = et_new_content.getText().toString();
        if(title.isEmpty()){
            Toast.makeText(PostActivity.this,"请填写标题",Toast.LENGTH_SHORT).show();
        }else if(content.isEmpty()){
            Toast.makeText(PostActivity.this,"请输入正文",Toast.LENGTH_SHORT).show();
        }else{
            dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                    .setLoadingColor(Color.parseColor("#06AFFB"))//颜色
                    .setHintText("Loading...")
                    .setHintTextSize(20) // 设置字体大小 dp
                    .setHintTextColor(Color.parseColor("#06AFFB"))  // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setDialogBackgroundColor(Color.parseColor("#ffffff")) // 设置背景色，默认白色
                    .show();
            postRequest(title,content);
        }
    }

    private void postRequest(final String title,final String content){
        String controller = "/postTopic";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId",MyApplication.getCurrentUser().getId().toString())
                .add("title",title)
                .add("content",content)
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(PostActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                dialog.cancel();
                if(response.body().string().equals("true")){
                    Toast.makeText(PostActivity.this, "发表成功，快去看看吧！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostActivity.this, "网络繁忙，稍后再试！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }
}
