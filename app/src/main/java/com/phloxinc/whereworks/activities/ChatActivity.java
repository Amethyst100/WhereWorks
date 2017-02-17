package com.phloxinc.whereworks.activities;

import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.phloxinc.whereworks.ActivityController;
import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.ChatAdapter;
import com.phloxinc.whereworks.adapters.FirebaseChatAdapter;
import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Message;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.database.DatabaseUtils;
import com.phloxinc.whereworks.firebase.MessageModel;
import com.phloxinc.whereworks.prefs.Prefs;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, ValueEventListener {

    private FloatingActionButton fab;
    private EditText messageBar;
    private LinearLayoutManager layoutManager;
    private ChatAdapter adapter;
    private String fcmToken;
    private Member member;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Chat chat;
    private DatabaseReference messageReference;

    private List<MessageModel> messageModelList = new ArrayList<>();
    private FirebaseChatAdapter firebaseChatAdapter;
    private int teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DatabaseUtils.initDB(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageBar = (EditText) findViewById(R.id.message_bar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnTouchListener(this);
        fab.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messageReference = database.getReference("messages");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("memberid")) {
                final int memberId = extras.getInt("memberid");
                messageReference.limitToLast(1000).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<MessageModel> messageList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MessageModel messageModel = new MessageModel(snapshot);
//                            MessageModel messageModel = snapshot.getValue(MessageModel.class);
                            int userId = Integer.parseInt(Prefs.getString("userId", "0"));
                            if ((messageModel.ReceiverID.contains(String.valueOf(memberId)) && messageModel.SenderID.contains(String.valueOf(userId))) || (messageModel.ReceiverID.contains(String.valueOf(userId)) && messageModel.SenderID.contains(String.valueOf(memberId)))) {
                                messageList.add(messageModel);
                            }
                        }
                        firebaseChatAdapter.setDataSet(messageList);
                        recyclerView.smoothScrollToPosition(messageList.size());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                member = Member.load(memberId);
                chat = Chat.loadByMemberId(memberId);
                if (chat == null) {
                    chat = new Chat();
                    chat.setMemberId(memberId);
                    chat.setType(Chat.Type.ONE_TO_ONE);
                    chat.save();
                }
                List<Message> messageList = chat.getMessages();

                if (actionBar != null) {
                    actionBar.setTitle(chat.getChatName());
                }
                DatabaseReference myRef = database.getReference("M" + String.valueOf(memberId));
                myRef.addValueEventListener(this);

                adapter = new ChatAdapter(messageList);
                firebaseChatAdapter = new FirebaseChatAdapter(messageModelList);
                recyclerView.setAdapter(firebaseChatAdapter);

            } else if (extras.containsKey("teamid")) {
                teamId = extras.getInt("teamid");
//                Team team = Team.load(teamId);
                messageReference.limitToLast(1000).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<MessageModel> messageList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MessageModel messageModel = new MessageModel(snapshot);
//                            MessageModel messageModel = snapshot.getValue(MessageModel.class);
                            if (messageModel.TeamID != null && messageModel.TeamID.contains(String.valueOf(teamId))) {
                                messageList.add(messageModel);
                            }
                        }
                        firebaseChatAdapter.setDataSet(messageList);
                        recyclerView.smoothScrollToPosition(messageList.size());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                chat = Chat.loadByTeamId(teamId);
                if (chat == null) {
                    chat = new Chat();
                    chat.setTeamId(teamId);
                    chat.setType(Chat.Type.TEAM);
                    chat.save();
                }

                List<Message> messageList = chat.getMessages();

                if (actionBar != null) {
                    actionBar.setTitle(chat.getChatName());
                }
                adapter = new ChatAdapter(messageList);
                firebaseChatAdapter = new FirebaseChatAdapter(messageModelList);
                recyclerView.setAdapter(firebaseChatAdapter);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                final String text = messageBar.getText().toString();
                if (text.length() > 0) {
                    messageBar.setText("");
                    final Message message = new Message(text);
                    Long timestamp = new Date().getTime();
                    message.setSenderId(Integer.parseInt(Prefs.getString("userId", "")));
                    if (chat.getType() == Chat.Type.ONE_TO_ONE) {
                        message.setRecipientId(chat.getMemberId());
                        message.setTeamId(0);
                    } else {
                        message.setRecipientId(chat.getTeamId());
                        message.setTeamId(chat.getTeamId());
                    }
                    message.setChatId((int) chat.getId());
                    message.setTimestamp(timestamp);
                    if (Prefs.getBoolean("Reporting", false)) {
                        Location location = Utils.getLocation(this);
                        if (location != null) {
                            message.setLat(location.getLatitude());
                            message.setLng(location.getLongitude());
                            Address address = Utils.getAddressFromLocation(this, location.getLatitude(), location.getLongitude());
                            if (address != null) {
                                String addressText = address.getAddressLine(0);
                                message.setLocation(addressText);
                            }
                        } else {
                            message.setLat(0);
                            message.setLng(0);
                            message.setLocation("Location is not available.");
                        }
                    } else {
                        message.setLat(0);
                        message.setLng(0);
                        message.setLocation("Location is not available.");
                    }
                    message.setSenderName(Prefs.getString("userName", ""));
                    message.setImage("");
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a", Locale.getDefault());
                    message.setTimeDate(dateFormat.format(calendar.getTime()));
                    message.save();
                    MessageModel messageModel = new MessageModel(message);
                    messageReference.push().setValue(messageModel);
                    adapter.addItem(message);
                    layoutManager.scrollToPosition(adapter.getItemCount() - 1);
                    if (Utils.IsInternetAvailable(this)) {
                        if (chat.getType() == Chat.Type.ONE_TO_ONE) {
                            new ProcessRequest<String>(Process.ADD_NOTIFICATION, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0", String.valueOf(message.getRecipientId()), String.valueOf(message.getSenderId()), message.getText());
                        } else {
                            new ProcessRequest<String>(Process.ADD_NOTIFICATION, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(teamId), String.valueOf(message.getRecipientId()), String.valueOf(message.getSenderId()), message.getText());
                        }

                        new AsyncTask<String, String, String>() {

                            @Override
                            protected String doInBackground(String... strings) {
                                if (chat.getType() == Chat.Type.ONE_TO_ONE) {
                                    if (fcmToken != null) {
                                        sendMessage(text);
                                    }
                                } else {
                                    sendMessageToTopic(message);
                                }
                                return null;
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
                break;
        }
    }

    private void sendMessageToTopic(Message message) {
        Gson gson = new Gson();
        JsonObject body = new JsonObject();

        JsonObject data = new JsonObject();
        data.addProperty("senderId", Prefs.getString("userId", "0"));
        data.addProperty("recipientId", message.getRecipientId());
        data.addProperty("timestamp", new Date().getTime());
        data.addProperty("message", message.getText());
        data.addProperty("teamId", teamId);
        data.addProperty("senderName", chat.getChatName());
        data.addProperty("type", "team");
        body.add("data", data);

        JsonObject notification = new JsonObject();
        notification.addProperty("title", chat.getChatName());
        notification.addProperty("body", message.getText());
        notification.addProperty("sound", "default");
        body.add("notification", notification);

        body.addProperty("to", "/topics/" + "T" + chat.getTeamId());
        String json = gson.toJson(body);

        URL url;
        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + Constants.SERVER_KEY);

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            byte[] bytes = json.getBytes("UTF-8");
            OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
            outputStream.write(bytes);
            outputStream.close();

            int code = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String text) {

        Gson gson = new Gson();
        JsonObject body = new JsonObject();

        JsonObject data = new JsonObject();
        data.addProperty("senderId", Prefs.getString("userId", "0"));
        data.addProperty("recipientId", member.getMemberId());
        data.addProperty("timestamp", new Date().getTime());
        data.addProperty("message", text);
        data.addProperty("senderName", member.getFullName());
        body.add("data", data);

        JsonObject notification = new JsonObject();
        notification.addProperty("title", member.getFullName());
        notification.addProperty("body", text);
        notification.addProperty("sound", "default");
        body.add("notification", notification);

        body.addProperty("to", fcmToken);
        String json = gson.toJson(body);

        URL url;
        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + Constants.SERVER_KEY);

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            byte[] bytes = json.getBytes("UTF-8");
            OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
            outputStream.write(bytes);
            outputStream.close();

            int code = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (view.getId()) {
            case R.id.fab:
                float scale = 1.0f;
                float maxScale = 1.2f * scale;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fab.animate().scaleX(maxScale).scaleY(maxScale).setDuration(200).setInterpolator(new BounceInterpolator()).start();
                        break;

                    case MotionEvent.ACTION_UP:
                        fab.animate().scaleX(scale).scaleY(scale).setDuration(500).setInterpolator(new BounceInterpolator()).start();
                        view.performClick();
                }
                return true;
        }
        return false;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (chat.getType() == Chat.Type.ONE_TO_ONE) {
            if (member != null) {
                if (dataSnapshot.getKey().contains("M" + String.valueOf(member.getMemberId()))) {
                    fcmToken = dataSnapshot.getValue(String.class);
                    if (fcmToken != null) {
                        if (fcmToken.isEmpty())
                            actionBar.setSubtitle("Not Available");
                    } else {
                        actionBar.setSubtitle("Not Available");
                    }
                }
            }
        }
//        else if (chat.getType() == Chat.Type.TEAM) {
//            if (dataSnapshot.getKey().contains("T" + String.valueOf(member.getMemberId()))) {
//                fcmToken = dataSnapshot.getValue(String.class);
//                if (fcmToken != null) {
//                    if (fcmToken.isEmpty())
//                        actionBar.setSubtitle("Not Available");
//                } else {
//                    actionBar.setSubtitle("Not Available");
//                }
//            }
//        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityController.getInstance().setCurrentActivity(this);
    }

//    public void addMessage(Message message) {
//        adapter.addItem(message);
//        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
//    }

    public long getCurrentChatId() {
        return chat != null ? chat.getId() : 0;
    }
}
