package com.tu.curriculumdesign.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.UI.TopicActivity;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.Topic;
import com.tu.curriculumdesign.bean.User;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;


public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private List<Topic> mTopicList;
    private List<User> mAuthorList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_item_title);
            tv_content = itemView.findViewById(R.id.tv_item_content);
            tv_time = itemView.findViewById(R.id.tv_item_time);
        }
    }

    public TopicAdapter(List<Topic> topicList){
        Collections.reverse(topicList);
        this.mTopicList = topicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Topic topic = mTopicList.get(position);
                Intent intent = new Intent(MyApplication.getContext(), TopicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("topic",topic);
                intent.putExtras(bundle);
                MyApplication.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = mTopicList.get(position);
        holder.tv_title.setText(topic.getTitle());
        String content = topic.getContent();
        try {
            holder.tv_content.setText(content.length()<90?content:subStringByBytes(content,88,"UTF-8")+"...");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.tv_time.setText(df.format(topic.getTime()));
    }

    @Override
    public int getItemCount() {
        return mTopicList.size();
    }

    public static String subStringByBytes(String string, int bytes,String charSetName) throws UnsupportedEncodingException {
        String subAfter = string.substring(0, bytes);
        int temp = bytes;
        try {
            while (bytes < subAfter.getBytes(charSetName).length) {
                subAfter = subAfter.substring(0, --temp);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return subAfter;
    }


}
