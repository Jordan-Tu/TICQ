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
import com.tu.curriculumdesign.UI.ChatActivity;
import com.tu.curriculumdesign.application.MyApplication;
import com.tu.curriculumdesign.bean.User;

import java.util.List;

public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.ViewHolder> {
    private List<User> mFriendList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        TextView tv_photo_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_photo_name = itemView.findViewById(R.id.tv_photo_name);
        }
    }

    public MsgListAdapter(List<User> userList){
        this.mFriendList = userList;
    }

    @NonNull
    @Override
    public MsgListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msglist_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                User chatUser = mFriendList.get(position);
                Toast.makeText(MyApplication.getContext(), chatUser.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyApplication.getContext(), ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("chatUser",chatUser);
                intent.putExtras(bundle);
                MyApplication.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mFriendList.get(position);
        holder.tv_name.setText(user.getName());
        holder.tv_photo_name.setText(user.getName().substring(0,1));
    }


    @Override
    public int getItemCount() {
        return mFriendList.size();
    }


}
