package com.tu.curriculumdesign.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tu.curriculumdesign.R;
import com.tu.curriculumdesign.bean.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private List<Notification> notificationList;
    private View.OnClickListener listener;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftName;
        TextView rightName;
        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftName = view.findViewById(R.id.left_name);
            rightName = view.findViewById(R.id.right_name);
        }
    }

    public NotificationAdapter(List<Notification> notificationList, View.OnClickListener listener) {
        this.notificationList=notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        if (notification.getType() == Notification.TYPE_APPLICATION) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftName.setText(notificationList.get(position).getUser().getEmail());
            holder.leftLayout.setTag(position);
            holder.leftLayout.setOnClickListener(listener);
        } else if (notification.getType() == Notification.TYPE_DELETE) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightName.setText(notificationList.get(position).getUser().getEmail());
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
