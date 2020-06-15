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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.adapter.MsgListAdapter;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.User;
import com.tu.curriculumdesign.util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MessageFragment extends Fragment {

    private RecyclerView msg_list_recycler_view;
    private SwipeRefreshLayout msg_list_swipeRefresh;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MsgListAdapter msgListAdapter = new MsgListAdapter((List<User>)msg.obj);
            msg_list_recycler_view.setAdapter(msgListAdapter);
            msg_list_swipeRefresh.setRefreshing(false);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_home,container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        msg_list_recycler_view = getActivity().findViewById(R.id.msg_list_recycler_view);
        msg_list_swipeRefresh = getActivity().findViewById(R.id.msg_list_swipeRefresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        requestFriendData();
        msg_list_recycler_view.setLayoutManager(linearLayoutManager);
        msg_list_swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        msg_list_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestFriendData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestFriendData();
    }

    protected void requestFriendData() {
        String controller = "/displayAllFriend";
        RequestBody requestBody = new FormBody.Builder()
                .add("id",MyApplication.getCurrentUser().getId().toString())
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
                List<User> userList= JSON.parseArray(response.body().string(),User.class);
                Message obtain = Message.obtain();
                obtain.obj = userList;
                handler.sendMessage(obtain);
            }
        });
    }



}
