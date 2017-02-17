package com.phloxinc.whereworks.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.Notification;
import com.phloxinc.whereworks.utils.Utils;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> feedList;

    public NotificationAdapter(List<Notification> feedList) {
        this.feedList = feedList;
    }

    public void setFeeds(List<Notification> feedList) {
        this.feedList = feedList;
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, null);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = feedList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        TextView date;
        TextView time;
        ImageView image;

        NotificationViewHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.text);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        void bind(Notification notification) {
            String notificationText = notification.getText();
            if (notificationText == null)
                notificationText = "";
            switch (notification.getType()) {
                case ADD_CONTACT_REQUEST:
                    text.setText(notification.getFullName() + " " + notificationText + (!notification.getTeamName().contains("null") ? (" " + notification.getTeamName()) : ""));
                    break;

                case LOCATION:
                case LOCATION_SHARING_CONSENT:
                case SCHEDULER:
                    text.setText(notification.getFullName() + " " + notificationText);
                    break;

                default:
                    text.setText(notification.getFullName() + " " + notificationText);
            }
            if (!notification.getFullName().isEmpty())
                image.setImageDrawable(Utils.getTextDrawable(String.valueOf(notification.getFullName().charAt(0)), notification.getFullName()));
            else
                image.setImageDrawable(Utils.getTextDrawable("A", notificationText));

            if (notification.getDate() != null) {
                String notificationDate = notification.getDate().split(" ")[0];
                String notificationTime = notification.getDate().split(" ")[1];
                notificationTime = notificationTime.split(":")[0] + ":" + notificationTime.split(":")[1] + " " + notificationTime.split(":")[2].replaceAll("[0-9]", "");
                date.setText(notificationDate);
                time.setText(notificationTime);
            }
        }
    }
}
