package com.phloxinc.whereworks.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.firebase.MessageModel;
import com.phloxinc.whereworks.utils.Utils;

import java.util.List;

public class FirebaseChatAdapter extends RecyclerView.Adapter<FirebaseChatAdapter.ChatViewHolder> {

    private List<MessageModel> mDataSet;

    public FirebaseChatAdapter(List<MessageModel> dataSet) {
        this.mDataSet = dataSet;
    }

    public void setDataSet(List<MessageModel> dataSet) {
        this.mDataSet = dataSet;
        notifyDataSetChanged();
    }

    public void addItem(MessageModel data) {
        mDataSet.add(data);
        notifyItemInserted(mDataSet.size() - 1);
        notifyItemChanged(mDataSet.size() - 2);
    }

    public void removeItem(MessageModel data) {
        int position = mDataSet.indexOf(data);
        if (position != -1) {
            mDataSet.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position - 1, mDataSet.size());
        }
    }

    @Override
    public FirebaseChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new FirebaseChatAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FirebaseChatAdapter.ChatViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView image;
        LinearLayout container;

        ChatViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
            container = (LinearLayout) text.getParent();
        }

        void bind(int position) {
            MessageModel message = mDataSet.get(position);
            int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, text.getResources().getDisplayMetrics());
            int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, text.getResources().getDisplayMetrics());

            if (message.isReceiving()) {
                setSendingBubble(position);
                image.setImageDrawable(Utils.getTextDrawable(String.valueOf(message.SenderName.charAt(0)), message.SenderName));
            } else {
                setReceivingBubble(position);
                image.setVisibility(View.GONE);
            }
            text.setPadding(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal);
            text.setText(message.Message);
        }

        private void setSendingBubble(int position) {
            container.setGravity(Gravity.LEFT);
            boolean begining = false;
            boolean ending = false;
            if (position == 0) {
                begining = true;
            } else {
                if (!mDataSet.get(position - 1).isReceiving()) {
                    begining = true;
                }
            }
            if (position == mDataSet.size() - 1) {
                ending = true;
            } else {
                if (!mDataSet.get(position + 1).isReceiving()) {
                    ending = true;
                }
            }
            if (begining && ending) {
                text.setBackgroundResource(R.drawable.receiving_bubble);
            } else if (begining && !ending) {
                text.setBackgroundResource(R.drawable.receiving_bubble_begin);
            } else if (!begining && ending) {
                text.setBackgroundResource(R.drawable.receiving_bubble_end);
            } else {
                text.setBackgroundResource(R.drawable.receiving_bubble_middle);
            }
            text.setTextColor(Color.BLACK);
        }

        private void setReceivingBubble(int position) {
            container.setGravity(Gravity.RIGHT);
            boolean begining = false;
            boolean ending = false;
            if (position == 0) {
                begining = true;
            } else {
                if (!mDataSet.get(position - 1).isSending()) {
                    begining = true;
                }
            }
            if (position == mDataSet.size() - 1) {
                ending = true;
            } else {
                if (!mDataSet.get(position + 1).isSending()) {
                    ending = true;
                }
            }
            if (begining && ending) {
                text.setBackgroundResource(R.drawable.sending_bubble);
            } else if (begining && !ending) {
                text.setBackgroundResource(R.drawable.sending_bubble_begin);
            } else if (!begining && ending) {
                text.setBackgroundResource(R.drawable.sending_bubble_end);
            } else {
                text.setBackgroundResource(R.drawable.sending_bubble_middle);
            }
            text.setTextColor(Color.WHITE);
        }
    }
}
