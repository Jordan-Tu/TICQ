package com.tu.curriculumdesign.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.CommentAdapter;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.Comment;
import com.tu.curriculumdesign.bean.Topic;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.HttpUtil;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.tu.curriculumdesign.application.MyApplication.getContext;

public class TopicActivity extends AppCompatActivity {

    @BindView(R.id.tv_topic_title) TextView tv_topic_title;
    @BindView(R.id.tv_topic_content) TextView tv_topic_content;
    @BindView(R.id.tv_post_time) TextView tv_post_time;
    @BindView(R.id.et_comment_input) EditText et_comment_input;
    @BindView(R.id.tv_post_author) TextView tv_post_author;
    @BindView(R.id.comment_recycler_view) RecyclerView comment_recycler_view;
    @BindView(R.id.comment_swipeRefresh) SwipeRefreshLayout comment_swipeRefresh;

    private static final Integer GET_TOPIC = 1;
    private static final Integer GET_USER = 2;
    private static final Integer SUCCESS = 3;

    private Topic topic;
    private Map<Integer,User> userMap = new HashMap<>();
    private List<Comment> commentList = new ArrayList<>();
    private Set<Integer> idSet = new HashSet<>();

    private ZLoadingDialog dialog = new ZLoadingDialog(TopicActivity.this);

    @OnClick(R.id.bt_comment_send) void postComment(){
        String comment = et_comment_input.getText().toString();
        if(comment.equals("")){
            Toast.makeText(TopicActivity.this, "评论不能为空哦！", Toast.LENGTH_SHORT).show();
        }else{
            remarkRequest(comment);
            et_comment_input.clearFocus();
            et_comment_input.setText("");
        }
    }

    @OnClick(R.id.iv_back) void backMain(){
        finish();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == GET_TOPIC){
                commentList =(List<Comment>) msg.obj;
                idSet.add(topic.getUserId());
                if(commentList.size() != 0){
                    for (int i=0;i<commentList.size();i++){
                        idSet.add(commentList.get(i).getUserId());
                    }
                }else{
                    getUser(topic.getUserId());
                }
                Iterator iterator = idSet.iterator();
                while (iterator.hasNext()) {
                    getUser((Integer)iterator.next());
                }

            }else if(msg.what == GET_USER){
                User user = (User) msg.obj;
                userMap.put(user.getId(),user);
                if(userMap.size() == idSet.size()){
                    CommentAdapter commentAdapter = new CommentAdapter(commentList,userMap);
                    comment_recycler_view.setAdapter(commentAdapter);
                    tv_post_author.setText(userMap.get(topic.getUserId()).getName());
                    for(Map.Entry<Integer, User> entry : userMap.entrySet()) {
                        System.out.print("Key = " + entry.getKey() + ",value=" + entry.getValue().toString());
                    }
                    comment_swipeRefresh.setRefreshing(false);
                    dialog.cancel();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_detail);
        ButterKnife.bind(this);
        topic = (Topic) getIntent().getExtras().getSerializable("topic");
        init();
        comment_swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        comment_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestCommentData();
            }
        });
        et_comment_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null)
                        manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    private void init(){
        tv_topic_title.setText(topic.getTitle());
        tv_topic_content.setText(topic.getContent());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_post_time.setText(df.format(topic.getTime()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                .setLoadingColor(Color.parseColor("#06AFFB"))//颜色
                .setHintText("Loading...")
                .setHintTextSize(20) // 设置字体大小 dp
                .setHintTextColor(Color.parseColor("#06AFFB"))  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#ffffff")) // 设置背景色，默认白色
                .show();
        requestCommentData();
        comment_recycler_view.setLayoutManager(linearLayoutManager);
    }

    @OnClick(R.id.tv_post_author) void toUserInfo(){
        Intent intent = new Intent(TopicActivity.this, UserInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",userMap.get(topic.getUserId()));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void requestCommentData() {
        String controller = "/viewComment";
        RequestBody requestBody = new FormBody.Builder()
                .add("topicId",topic.getId().toString())
                .build();
        HttpUtil.sendOkHttpRequest(controller,requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(MyApplication.getContext(), "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                List<Comment> commentList = JSON.parseArray(response.body().string(),Comment.class);
                Message obtain = Message.obtain();
                obtain.what= GET_TOPIC;
                obtain.obj =commentList;
                handler.sendMessage(obtain);
            }
        });
    }

    private void getUser(Integer id){
        String controller = "/displayUserInfo";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id.toString())
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(TopicActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                String string = response.body().string();
                User user = null;
                try {
                    user = JSON.parseObject(string).toJavaObject(User.class);
                }catch (Exception e){
                    Log.d(controller,"onFailure:"+e.getMessage());
                }
                if (user!=null) {
                    Message message = new Message();
                    message.what = GET_USER;
                    message.obj= user;
                    handler.sendMessage(message);
                }
                Looper.loop();
            }
        });


    }

    private void remarkRequest(String content){
        String controller = "/remark";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", MyApplication.getCurrentUser().getId().toString())
                .add("topicId",topic.getId().toString())
                .add("content",content)
                .build();

        HttpUtil.sendOkHttpRequest(controller, requestBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(TopicActivity.this, "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Looper.prepare();
                if (response.body().string().equals("true")) {
                    Toast.makeText(TopicActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
                    requestCommentData();
                } else {
                    Toast.makeText(TopicActivity.this, "评论失败！", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
    }
}
