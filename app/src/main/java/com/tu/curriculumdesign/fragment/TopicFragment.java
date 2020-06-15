package com.tu.curriculumdesign.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.TopicAdapter;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.Topic;
import com.tu.curriculumdesign.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;

public class TopicFragment extends Fragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout topic_swipeRefresh;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            TopicAdapter topicAdapter = new TopicAdapter((List<Topic>)msg.obj);
            recyclerView.setAdapter(topicAdapter);
            topic_swipeRefresh.setRefreshing(false);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.topic_home,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = getActivity().findViewById(R.id.recycler_view);
        topic_swipeRefresh = getActivity().findViewById(R.id.topic_swipeRefresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        requestTopicData();
        recyclerView.setLayoutManager(linearLayoutManager);
        topic_swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        topic_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestTopicData();
            }
        });
    }

    protected void requestTopicData() {
        String controller = "/viewAllTopic";
        HttpUtil.sendOkHttpRequest(controller, new FormBody.Builder().build(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Log.d(controller,"onFailure:"+e.getMessage());
                Toast.makeText(MyApplication.getContext(), "出错了！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                List<Topic> topicList = JSON.parseArray(response.body().string(),Topic.class);
                Message obtain = Message.obtain();
                obtain.obj = topicList;
                handler.sendMessage(obtain);
            }
        });
    }

}
