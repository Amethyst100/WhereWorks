package com.phloxinc.whereworks.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.activities.ChatActivity;
import com.phloxinc.whereworks.activities.SplashScreen;
import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.bo.Message;
import com.phloxinc.whereworks.database.DatabaseUtils;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.utils.Utils;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> map = remoteMessage.getData();
        String text = map.get("message");
        String senderId = map.get("senderId");
//        String recipientId = map.get("recipientId");
//        String timestamp = map.get("timestamp");
//        String type = map.get("type");
        String name = map.containsKey("senderName") ? map.get("senderName") : "Where Works!";
//        Log.e(TAG, text);
        if (remoteMessage.getNotification() != null) {
//            RemoteMessage.Notification notification = remoteMessage.getNotification();
//            Log.d(TAG, "Notification Message Body: " + notification.getBody());
            Activity activity = ActivityController.getInstance().getCurrentActivity();
            if (!(activity instanceof ChatActivity)) {
                if (map.containsKey("teamId")) {
                    showNotification(name, text, senderId, map.get("teamId"));
                } else {
                    showNotification(name, text, senderId);
                }
            }
        }
//
//        DatabaseUtils.initDB(this);
//
//        if (!Prefs.getString("userId", "").equals(senderId)) {
//
//            Chat chat;
//            if (!map.containsKey("type")) {
//                chat = Chat.loadByMemberId(Integer.parseInt(senderId));
//                if (chat == null) {
//                    chat = new Chat();
//                    chat.setType(Chat.Type.ONE_TO_ONE);
//                    chat.setMemberId(Integer.parseInt(senderId));
//                    chat.save();
//                }
//            } else {
//                chat = Chat.loadByTeamId(Integer.parseInt(recipientId));
//                if (chat == null) {
//                    chat = new Chat();
//                    chat.setType(Chat.Type.TEAM);
//                    chat.setTeamId(Integer.parseInt(recipientId));
//                    chat.save();
//                }
//            }
//
//            final Message message = new Message();
//            message.setText(text);
//            message.setSenderId(Integer.parseInt(senderId));
//            message.setRecipientId(Integer.parseInt(recipientId));
//            message.setChatId((int) chat.getId());
//            message.setTimestamp(Long.parseLong(timestamp));
//            message.save();
//
//            Activity activity = ActivityController.getInstance().getCurrentActivity();
//            if (activity instanceof ChatActivity) {
//                final ChatActivity chatActivity = (ChatActivity) activity;
//                if (chatActivity.getCurrentChatId() == chat.getId()) {
//                    chatActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                        chatActivity.addMessage(message);
//                        }
//                    });
//                } else {
//                    showNotification(chat, message);
//                }
//            } else {
//                showNotification(chat, message);
//            }
//        }
    }

    private void showNotification(Chat chat, Message message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(chat.getChatName())
                        .setContentText(message.getText());

        Intent resultIntent;
        if (chat.getType() == Chat.Type.ONE_TO_ONE) {
            resultIntent = new Intent(this, ChatActivity.class).putExtra("memberid", chat.getMemberId());
        } else {
            resultIntent = new Intent(this, ChatActivity.class).putExtra("teamid", chat.getTeamId());
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void showNotification(String title, String message, String senderId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message);

        Intent resultIntent = new Intent(this, ChatActivity.class).putExtra("memberid", Integer.parseInt(senderId));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void showNotification(String title, String message, String senderId, String teamId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(Utils.getTextDrawable(title))
                .setContentTitle(title)
                .setContentText(message);

        Intent resultIntent = new Intent(this, ChatActivity.class).putExtra("teamid", Integer.parseInt(teamId));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
