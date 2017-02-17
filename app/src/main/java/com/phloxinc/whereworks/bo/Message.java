package com.phloxinc.whereworks.bo;

import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.MessageDAO;
import com.phloxinc.whereworks.prefs.Prefs;

import java.util.List;

public class Message extends BaseEntity {

    private int chatId;
    private String text;
    private int senderId;
    private int recipientId;
    private String senderName;
    private long timestamp;
    private double lat;
    private double lng;
    private String location;
    private String image;
    private String timeDate;
    private int teamId;

    public Message() {
    }

    public Message(String text) {
        this.text = text;
    }


    //region Getters & Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public boolean isReceiving() {
        int userId = Integer.parseInt(Prefs.getString("userId", ""));
        return userId != senderId;
    }

    public boolean isSending() {
        int userId = Integer.parseInt(Prefs.getString("userId", ""));
        return userId == senderId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public Member getSender() {
        return Member.load(senderId);
    }

    public Member getRecipient() {
        return Member.load(recipientId);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    //endregion

    public static List<Message> all() {
        return MessageDAO.getInstance().getAllMessages();
    }

    public static Message load(long messageId) {
        return MessageDAO.getInstance().getMessage(messageId);
    }

    public static List<Message> load(int chatId) {
        return MessageDAO.getInstance().getMessages(chatId);
    }

    public void save() {
        Message message = load(getId());
        if (message != null) {
            this.setId(message.getId());
        }
        MessageDAO.getInstance().insert(this);
    }

    public void delete() {
        MessageDAO.getInstance().delete(this);
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
