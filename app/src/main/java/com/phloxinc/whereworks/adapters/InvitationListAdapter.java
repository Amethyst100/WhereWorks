package com.phloxinc.whereworks.adapters;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.util.List;

public class InvitationListAdapter extends RecyclerView.Adapter<InvitationListAdapter.RequestViewHolder> {

    private List<?> mDataSet;
    private ProgressDialog dialog;

    public InvitationListAdapter(List<?> mDataSet) {
        this.mDataSet = mDataSet;
    }

    public void setDataSet(List<?> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite, null);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        Member member = (Member) mDataSet.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatButton acceptButton;
        private final AppCompatButton declineButton;
        private final TextView title;
        private final TextView desc;
        private final ImageView image;

        RequestViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
            image = (ImageView) itemView.findViewById(R.id.image);
            acceptButton = (AppCompatButton) itemView.findViewById(R.id.accept);
            declineButton = (AppCompatButton) itemView.findViewById(R.id.decline);
        }

        void bind(final Member member) {

            title.setText(member.getFullName());
            desc.setText(member.getEmail());

            TextDrawable drawable = Utils.getTextDrawable(String.valueOf(member.getFullName().charAt(0)), String.valueOf(member.getMemberId()));
            image.setImageDrawable(drawable);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.IsInternetAvailable(view.getContext())) {
                        dialog = new ProgressDialog(view.getContext());
                        dialog.setMessage("Accepting request");
                        dialog.show();
                        new ProcessRequest<>(Process.MEMBER_INVITATION_RESPONSE, new ProcessRequest.RequestListener<Object>() {
                            @Override
                            public void onSuccess(String process, Object result) {
                                dialog.dismiss();
                                member.save();
                                mDataSet.remove(member);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String process) {
                                dialog.dismiss();
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(member.getMemberId()), "1");
                    } else {
                        Utils.showDialog(view.getContext(), "No Internet");
                    }
                }
            });

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.IsInternetAvailable(view.getContext())) {
                        dialog = new ProgressDialog(view.getContext());
                        dialog.setMessage("Declining request");
                        dialog.show();
                        new ProcessRequest<>(Process.MEMBER_INVITATION_RESPONSE, new ProcessRequest.RequestListener<Object>() {
                            @Override
                            public void onSuccess(String process, Object result) {
                                dialog.dismiss();
                                mDataSet.remove(member);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(String process) {
                                dialog.dismiss();
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(member.getMemberId()), "2");
                    } else {
                        Utils.showDialog(view.getContext(), "No Internet");
                    }
                }
            });
        }
    }
}
