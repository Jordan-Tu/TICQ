package com.tu.curriculumdesign.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.bean.Comment;
import com.tu.curriculumdesign.bean.User;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<Comment> mCommentList;
    private Map<Integer,User> mUserMap;

    public CommentAdapter(List<Comment> commentList, Map<Integer, User> userMap) {
        Collections.reverse(commentList);
        this.mCommentList = commentList;
        this.mUserMap = userMap;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_comment_user;
        TextView tv_comment_content;
        TextView tv_comment_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_comment_user = itemView.findViewById(R.id.tv_comment_user);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            tv_comment_time = itemView.findViewById(R.id.tv_comment_time);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mCommentList.get(position);
        holder.tv_comment_user.setText(mUserMap.get(comment.getUserId()).getName());
        holder.tv_comment_content.setText(comment.getContent());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.tv_comment_time.setText(df.format(comment.getTime()));
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

}
