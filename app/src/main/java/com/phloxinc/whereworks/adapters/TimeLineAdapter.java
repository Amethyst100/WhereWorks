package com.phloxinc.whereworks.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.TimelineLog;

import java.util.List;
import java.util.Random;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimelineViewHolder> {

    private List<TimelineLog> feedList;

    public TimeLineAdapter(List<TimelineLog> feedList) {
        this.feedList = feedList;
    }

    public void setFeeds(List<TimelineLog> feedList) {
        this.feedList = feedList;
        notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, null);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimelineViewHolder holder, int position) {
        int colorBlue = Color.parseColor("#2196F3");
        int colorPink = Color.parseColor("#E91E63");
        int colorLime = Color.parseColor("#CDDC39");
        int[] colors = {colorBlue, colorPink, colorLime};
        int randomColor = new Random().nextInt(3);
        holder.timelinePoint.setColorFilter(colors[randomColor], PorterDuff.Mode.SRC_IN);
        TimelineLog timelineLog = feedList.get(position);
        if (timelineLog.getName() != null)
            holder.title.setText(timelineLog.getName());

        if (timelineLog.getLogType() != null) {
            switch (timelineLog.getLogType()) {
                case TEAM:
                    if (timelineLog.getLogAction() == TimelineLog.LogAction.ADD) {
                        holder.description.setText("Team " + (timelineLog.getName() != null ? timelineLog.getName() : "") + " added");
                    } else if (timelineLog.getLogAction() == TimelineLog.LogAction.DELETE) {
                        holder.description.setText("Team " + (timelineLog.getName() != null ? timelineLog.getName() : "") + " deleted");
                    }
                    holder.timelinePoint.setColorFilter(colorPink, PorterDuff.Mode.SRC_IN);
                    break;

                case MEMBER:
                    holder.timelinePoint.setColorFilter(colorLime, PorterDuff.Mode.SRC_IN);
                    switch (timelineLog.getLogAction()) {
                        case ADD:
                            holder.description.setText((timelineLog.getName() != null ? timelineLog.getName() : "") + " added as member in " + (timelineLog.getTeamName() != null ? timelineLog.getTeamName() : ""));
                            break;

                        case DELETE:
                            if (timelineLog.getTeamName() != null) {
                                holder.description.setText((timelineLog.getName() != null ? timelineLog.getName() : "") + " deleted from " + timelineLog.getTeamName());
                            } else {
                                holder.description.setText((timelineLog.getName() != null ? timelineLog.getName() : "") + " removed from contacts");
                            }
                            break;
                    }
                    break;

                case GEOFENCE:
                    if (timelineLog.getLogAction() == TimelineLog.LogAction.ADD) {
                        String text = (timelineLog.getName() != null ? timelineLog.getName() : "") + " added";
                        holder.description.setText(text);
                        holder.timelinePoint.setColorFilter(colorBlue, PorterDuff.Mode.SRC_IN);
                    }
                    break;

                default:
                    holder.description.setText(String.valueOf(timelineLog.getLogType()));
            }
        }

        if (timelineLog.getDate() != null) {
            String date = timelineLog.getDate().split(" ")[0];
            String time = timelineLog.getDate().split(" ")[1];
            time = time.split(":")[0] + ":" + time.split(":")[1] + " " + time.split(":")[2].replaceAll("[0-9]", "");
            holder.date.setText(date);
            holder.time.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {

        ImageView timelinePoint;
        TextView title;
        TextView description;
        TextView date;
        TextView time;

        TimelineViewHolder(View itemView) {
            super(itemView);

            timelinePoint = (ImageView) itemView.findViewById(R.id.timeline_point);
            title = (TextView) itemView.findViewById(R.id.title);
            title.setVisibility(View.GONE);
            description = (TextView) itemView.findViewById(R.id.desc);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
