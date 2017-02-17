package com.phloxinc.whereworks.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
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
import com.phloxinc.whereworks.activities.MemberInfoActivity;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter<T> extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<T> feedList;
    private boolean toggleMember;
    private Team team;

    public ContactAdapter(List<T> feedList) {
        this.feedList = feedList;
        this.toggleMember = true;
    }

    public ContactAdapter(List<T> feedList, Team team, boolean toggleMember) {
        this.feedList = feedList;
        this.team = team;
        this.toggleMember = toggleMember;
    }

    @SuppressLint("InflateParams")
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, int position) {
        final T feed = feedList.get(position);
        if (feed instanceof Member) {
            holder.bindContact((Member) feed, team, toggleMember);
        }
    }

    @Override
    public int getItemCount() {
        if (feedList != null)
            return feedList.size();
        else
            return 0;
    }

    public void setFeeds(List<T> feeds) {
        this.feedList = feeds;
        notifyDataSetChanged();
    }

    public void setFeeds(List<T> feeds, Team team) {
        this.feedList = feeds;
        this.team = team;
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView desc;
        TextView number;
        TextView ownerTag;
        ImageView image;
        TextView pendingStatus;
        LinearLayout infoLayout;

        ContactViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            number = (TextView) itemView.findViewById(R.id.number);
            ownerTag = (TextView) itemView.findViewById(R.id.owner_tag);
            image = (ImageView) itemView.findViewById(R.id.image);
            infoLayout = (LinearLayout) itemView.findViewById(R.id.layout_info);
            pendingStatus = (TextView) itemView.findViewById(R.id.status_pending);
        }

        void bindContact(final Member member, Team team, boolean toggleMember) {
            if (member.getMemberId() != -1) {
                title.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);

                title.setText(member.getFullName());
                desc.setText(member.getEmail());
                if (member.getContactNumber() != null && !member.getContactNumber().equals("0")) {
                    number.setText(member.getContactNumber());
                } else {
                    number.setVisibility(View.GONE);
                }

                if (!toggleMember) {
                    pendingStatus.setVisibility(View.GONE);

                    if (team != null) {
                        if (member.getMemberId() == team.getHeadId()) {
                            ownerTag.setVisibility(View.VISIBLE);
                        } else {
                            ownerTag.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (member.getStatus() != null) {
                        if (member.getStatus().contains("2")) {
                            pendingStatus.setVisibility(View.VISIBLE);
                        } else {
                            pendingStatus.setVisibility(View.GONE);
                        }
                    } else {
                        pendingStatus.setVisibility(View.GONE);
                    }
                }

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        view.getContext().startActivity(new Intent(view.getContext(), MemberInfoActivity.class).putExtra("memberid", member.getMemberId()));
                    }
                });

                infoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class).putExtra("memberid", member.getMemberId()));
                    }
                });

                TextDrawable drawable = Utils.getTextDrawable(String.valueOf(member.getFullName().charAt(0)), String.valueOf(member.getMemberId()));
                image.setImageDrawable(drawable);

                Picasso.with(image.getContext()).load(Constants.MEMBER_IMAGE_URL + member.getMemberPhoto())
                        .transform(new CircleTransform())
                        .placeholder(drawable)
                        .into(image);
            } else {
                title.setVisibility(View.INVISIBLE);
                desc.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                number.setVisibility(View.GONE);
                pendingStatus.setVisibility(View.GONE);
            }
        }
    }
}
