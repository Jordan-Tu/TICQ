//package com.tu.curriculumdesign.UI;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.alibaba.fastjson.JSON;
//import com.tu.curriculumdesign.R;
//import com.tu.curriculumdesign.adapter.TopicAdapter;
//import com.tu.curriculumdesign.application.MyApplication;
//import com.tu.curriculumdesign.bean.Topic;
//import com.tu.curriculumdesign.util.HttpUtil;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//
//public class CreateActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            TopicAdapter topicAdapter = new TopicAdapter((List<Topic>)msg.obj);
//            recyclerView.setAdapter(topicAdapter);
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create);
//        requestData();
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//    }
//
//    private void requestData() {
//        String controller = "/viewAllTopic";
//        HttpUtil.sendOkHttpRequest(controller, new FormBody.Builder().build(), new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Looper.prepare();
//                Log.d(controller,"onFailure:"+e.getMessage());
//                Toast.makeText(MyApplication.getContext(), "出错了！", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
//                System.out.println(response.toString());
//                String string = response.body().string();
//                System.out.println(string);
//                List<Topic> topicList = new ArrayList<>();
//                topicList = JSON.parseArray(string,Topic.class);
//                Message obtain = Message.obtain();
//                obtain.obj = topicList;
//                handler.sendMessage(obtain);
//            }
//        });
//    }
//}
