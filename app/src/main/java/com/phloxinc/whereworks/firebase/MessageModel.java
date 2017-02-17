package com.phloxinc.whereworks.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.phloxinc.whereworks.bo.Message;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.prefs.Prefs;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class MessageModel {

    public String Lat;
    public String Lng;
    public String Location;
    public String Message;
    public String ProfilePhoto;
    public String ReceiverID;
    public String ReceiverName;
    public String SenderID;
    public String SenderName;
    public String SentOn;
    public String TeamID;

    public MessageModel() {
    }

    public MessageModel(Message message) {
        this.Lat = String.valueOf(message.getLat());
        this.Lng = String.valueOf(message.getLng());
        this.Location = message.getLocation();
        this.Message = message.getText();
        this.ProfilePhoto = message.getImage();
        if (message.getTeamId() == 0) {
            this.ReceiverID = String.valueOf(message.getRecipientId());
            this.ReceiverName = message.getRecipient().getFullName();
            this.TeamID = "0";
        } else {
            this.ReceiverID = "0";
            Team team = Team.load(message.getTeamId());
            this.ReceiverName = team.getName();
            this.TeamID = String.valueOf(message.getRecipientId());
        }
        this.SenderID = String.valueOf(message.getSenderId());
        this.SenderName = message.getSenderName();
        this.SentOn = message.getTimeDate();
    }

    public MessageModel(DataSnapshot snapshot) {
        for (DataSnapshot fields : snapshot.getChildren()) {
            if (fields.getKey().equals("Lat")) {
                Lat = (String) fields.getValue();
            }
            if (fields.getKey().equals("Lng")) {
                Lng = (String) fields.getValue();
            }
            if (fields.getKey().equals("Location")) {
                Location = (String) fields.getValue();
            }
            if (fields.getKey().equals("Message")) {
                Message = (String) fields.getValue();
            }
            if (fields.getKey().equals("ProfilePhoto")) {
                ProfilePhoto = (String) fields.getValue();
            }
            if (fields.getKey().equals("ReceiverID")) {
                ReceiverID = String.valueOf(fields.getValue());
            }
            if (fields.getKey().equals("ReceiverName")) {
                ReceiverName = (String) fields.getValue();
            }
            if (fields.getKey().equals("SenderID")) {
                SenderID = (String) fields.getValue();
            }
            if (fields.getKey().equals("SenderName")) {
                SenderName = (String) fields.getValue();
            }
            if (fields.getKey().equals("SentOn")) {
                SentOn = (String) fields.getValue();
            }
            if (fields.getKey().equals("TeamID")) {
                TeamID = String.valueOf(fields.getValue());
            }
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Lat", Lat);
        result.put("Lng", Lng);
        result.put("Location", Location);
        result.put("Message", Message);
        result.put("ProfilePhoto", ProfilePhoto);
        result.put("ReceiverID", ReceiverID);
        result.put("ReceiverName", ReceiverName);
        result.put("SenderID", SenderID);
        result.put("SenderName", SenderName);
        result.put("SentOn", SentOn);
        result.put("TeamID", TeamID);

        return result;
    }

    @Exclude
    public boolean isReceiving() {
        String userId = Prefs.getString("userId", "");
        return !userId.contains(SenderID);
    }

    @Exclude
    public boolean isSending() {
        String userId = Prefs.getString("userId", "");
        return userId.contains(SenderID);
    }
}
