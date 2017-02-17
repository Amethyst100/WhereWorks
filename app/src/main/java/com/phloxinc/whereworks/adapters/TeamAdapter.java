package com.phloxinc.whereworks.adapters;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.activities.ChatActivity;
import com.phloxinc.whereworks.activities.TeamActivity;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ASAD on 2/7/2017.
 */

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<Team> teamList;
    private int level;

    public TeamAdapter(List<Team> teamList, int level) {
        this.teamList = teamList;
        this.level = level;
    }

    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
        notifyDataSetChanged();
    }

    @Override
    public TeamAdapter.TeamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new TeamAdapter.TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamAdapter.TeamViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.bind(team);
    }

    @Override
    public int getItemCount() {
        return teamList != null ? teamList.size() : 0;
    }

    class TeamViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView title;
        TextView desc;
        ImageView image;
        LinearLayout infoLayout;
        ImageView expandIcon;
        RelativeLayout expandButton;
        RecyclerView recyclerView;

        boolean expanded;

        TeamViewHolder(View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.container);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            image = (ImageView) itemView.findViewById(R.id.image);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.layout_info);
            expandIcon = (ImageView) itemView.findViewById(R.id.expand_icon);
            expandButton = (RelativeLayout) itemView.findViewById(R.id.expand_button);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

            float scale = itemView.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (30 * level * scale);
            container.setPadding(dpAsPixels, 0, 0, 0);

            expanded = false;
        }

        void bind(final Team team) {
            if (team.getTeamId() != -1) {
                title.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);

                title.setText(team.getName());
                desc.setText(team.getDescription());

                TextDrawable drawable = Utils.getTextDrawable(String.valueOf(team.getName().charAt(0)), String.valueOf(team.getTeamId()));
                image.setImageDrawable(drawable);

                Picasso.with(image.getContext()).load(Constants.TEAM_IMAGE_URL + team.getTeamPhoto())
                        .transform(new CircleTransform())
                        .placeholder(drawable)
                        .into(image);

                if (team.getChildItemList().isEmpty()) {
                    expandButton.setVisibility(View.GONE);
                } else {
                    expandButton.setVisibility(View.VISIBLE);
                    expandButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (expanded) {
                                expandIcon.setImageResource(R.drawable.ic_expand_more_black_24dp);
                                expanded = false;
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                expandIcon.setImageResource(R.drawable.ic_expand_less_black_24dp);
                                expanded = true;
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(new TeamAdapter((List<Team>) team.getChildItemList(), level + 1));
                            }
                        }
                    });
                }

                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class).putExtra("teamid", team.getTeamId()));
                    }
                });

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.getContext().startActivity(new Intent(view.getContext(), TeamActivity.class).putExtra("teamid", team.getTeamId()));
                    }
                });
            } else {
                title.setVisibility(View.INVISIBLE);
                desc.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                expandIcon.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
}
