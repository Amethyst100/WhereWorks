package com.phloxinc.whereworks.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phloxinc.whereworks.CircleTransform;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.activities.ChatActivity;
import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.bo.Message;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

@SuppressWarnings("unchecked")
public class ChatListAdapter<T> extends RecyclerView.Adapter<ChatListAdapter.RecyclerViewHolder> {

    private List<T> mDataSet;

    public ChatListAdapter(List<T> mDataSet) {
        this.mDataSet = mDataSet;
    }

    public void setDataSet(List<T> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.RecyclerViewHolder holder, int position) {
        T feed = mDataSet.get(position);
        holder.bind(feed);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView title;
        private final TextView desc;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);

        }

        void bind(T feed) {
            final Chat chat = (Chat) feed;
            String chatName = chat.getChatName();

            title.setText(chatName);
            if (!chatName.isEmpty())
                image.setImageDrawable(Utils.getTextDrawable(String.valueOf(chatName.charAt(0)), chatName));
            else
                image.setImageDrawable(Utils.getTextDrawable("A"));

            List<Message> messageList = chat.getMessages();
            if (messageList != null && messageList.size() != 0) {
                String lastMessage = messageList.get(messageList.size() - 1).getText();
                desc.setText(lastMessage);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chat.getType() == Chat.Type.ONE_TO_ONE) {
                        view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class).putExtra("memberid", chat.getMemberId()));
                    } else {
                        view.getContext().startActivity(new Intent(view.getContext(), ChatActivity.class).putExtra("teamid", chat.getTeamId()));
                    }
                }
            });
        }
    }
}
