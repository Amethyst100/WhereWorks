package com.phloxinc.whereworks.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeamMembersAdapter extends RecyclerView.Adapter<TeamMembersAdapter.MemberViewHolder> {

    private List<Integer> teamMembersIds;
    private List<Member> memberList;
    private Team team;
    private ProgressDialog dialog;

    public TeamMembersAdapter(Context context, Team team, List<Member> memberList) {
        this.team = team;
        this.memberList = memberList;
        this.teamMembersIds = new ArrayList<>();
        for (Member member : team.getMembers()) {
            teamMembersIds.add(member.getMemberId());
        }
        dialog = new ProgressDialog(context);
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {

        private AppCompatButton removeButton;
        private AppCompatButton addButton;
        TextView title;
        TextView desc;
        ImageView image;

        MemberViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            image = (ImageView) itemView.findViewById(R.id.image);
            addButton = (AppCompatButton) itemView.findViewById(R.id.add_button);
            removeButton = (AppCompatButton) itemView.findViewById(R.id.remove_button);
        }

        void bind(final Member member) {
            title.setText(member.getFullName());
            desc.setText(member.getEmail());
            TextDrawable drawable = Utils.getTextDrawable(String.valueOf(member.getFullName().charAt(0)), String.valueOf(member.getMemberId()));
            image.setImageDrawable(drawable);

            Picasso.with(image.getContext()).load(Constants.MEMBER_IMAGE_URL + member.getMemberPhoto())
                    .transform(new CircleTransform())
                    .placeholder(drawable)
                    .into(image);

            if (teamMembersIds.contains(member.getMemberId())) {
                addButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.VISIBLE);
            } else {
                addButton.setVisibility(View.VISIBLE);
                removeButton.setVisibility(View.GONE);
            }

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (Utils.IsInternetAvailable(view.getContext())) {
                        dialog.setMessage("Adding Member");
                        dialog.show();
                        new ProcessRequest<>(Process.MEMBER_TEAM_ADD, new ProcessRequest.RequestListener<Object>() {
                            @Override
                            public void onSuccess(String process, Object result) {
                                FirebaseMessaging.getInstance().subscribeToTopic("T" + team.getTeamId());
                                team.addMember(member);
                                teamMembersIds = new ArrayList<>();
                                for (Member member : team.getMembers()) {
                                    teamMembersIds.add(member.getMemberId());
                                }
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(String process) {
                                Utils.showDialog(view.getContext(), "Failed to add member");
                                dialog.dismiss();
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()), String.valueOf(member.getMemberId()));
                    }
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (Utils.IsInternetAvailable(view.getContext())) {
                        dialog.setMessage("Removing Member");
                        dialog.show();
                        new ProcessRequest<>(Process.MEMBER_TEAM_DELETE, new ProcessRequest.RequestListener<Object>() {
                            @Override
                            public void onSuccess(String process, Object result) {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("T" + team.getTeamId());
                                team.removeMember(member);
                                teamMembersIds = new ArrayList<>();
                                for (Member member : team.getMembers()) {
                                    teamMembersIds.add(member.getMemberId());
                                }
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(String process) {
                                Utils.showDialog(view.getContext(), "Failed to remove member");
                                dialog.dismiss();
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(team.getTeamId()), String.valueOf(member.getMemberId()));
                    }
                }
            });
        }
    }
}