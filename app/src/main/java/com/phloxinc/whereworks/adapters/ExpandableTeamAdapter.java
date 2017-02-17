package com.phloxinc.whereworks.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.activities.ChatActivity;
import com.phloxinc.whereworks.activities.TeamActivity;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ExpandableTeamAdapter extends ExpandableRecyclerAdapter<ExpandableTeamAdapter.TeamViewHolder, ExpandableTeamAdapter.SubTeamViewHolder> {

    /**
     * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
     * <p>
     * Changes to {@link #mParentItemList} should be made through add/remove methods in
     * {@link ExpandableRecyclerAdapter}
     *
     * @param parentItemList List of all {@link ParentListItem} objects to be
     *                       displayed in the RecyclerView that this
     *                       adapter is linked to
     */
    public ExpandableTeamAdapter(@NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
    }

    @Override
    public TeamViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.item_team, parentViewGroup, false);
        return new TeamViewHolder(view);
    }

    @Override
    public SubTeamViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = LayoutInflater.from(childViewGroup.getContext()).inflate(R.layout.item_sub_team, childViewGroup, false);
        return new SubTeamViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(final TeamViewHolder holder, int position, ParentListItem parentListItem) {
        Team team = (Team) parentListItem;
        holder.bind(team);
    }

    @Override
    public void onBindChildViewHolder(SubTeamViewHolder holder, int position, Object childListItem) {
        Team team = (Team) childListItem;
        holder.bind(team);
    }

    class TeamViewHolder extends ParentViewHolder {

        TextView title;
        TextView desc;
        ImageView image;
        ImageView ownerIcon;
        LinearLayout infoLayout;
        ImageView expandIcon;
        RelativeLayout expandButton;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this RecyclerViewHolder
         */
        public TeamViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            image = (ImageView) itemView.findViewById(R.id.image);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.layout_info);
            expandIcon = (ImageView) itemView.findViewById(R.id.expand_icon);
            expandButton = (RelativeLayout) itemView.findViewById(R.id.expand_button);
            ownerIcon = (ImageView) itemView.findViewById(R.id.owner_icon);
        }

        public void bind(final Team team) {
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
                            if (isExpanded()) {
                                collapseView();
                                expandIcon.setImageResource(R.drawable.ic_expand_more_black_24dp);
                            } else {
                                expandView();
                                expandIcon.setImageResource(R.drawable.ic_expand_less_black_24dp);
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
            }
        }

        @Override
        public boolean shouldItemViewClickToggleExpansion() {
            return false;
        }
    }

    class SubTeamViewHolder extends ChildViewHolder {

        TextView title;
        TextView desc;
        ImageView image;
        LinearLayout infoLayout;

        /**
         * Default constructor.
         *
         * @param itemView The {@link View} being hosted in this RecyclerViewHolder
         */
        public SubTeamViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            image = (ImageView) itemView.findViewById(R.id.image);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.layout_info);
        }

        public void bind(final Team team) {
            title.setText(team.getName());
            desc.setText(team.getDescription());

            TextDrawable drawable = Utils.getTextDrawable(String.valueOf(team.getName().charAt(0)), String.valueOf(team.getTeamId()));
            image.setImageDrawable(drawable);

            Picasso.with(image.getContext()).load(Constants.TEAM_IMAGE_URL + team.getTeamPhoto())
                    .transform(new CircleTransform())
                    .placeholder(drawable)
                    .into(image);

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
        }
    }
}
