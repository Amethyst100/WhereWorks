package com.phloxinc.whereworks.bo;

import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.NotificationDAO;

import java.util.List;

public class Notification extends BaseEntity {

    private int notificationId;
    private String number;
    private String email;
    private String fullName;
    private String teamName;
    private int receiverId;
    private int senderId;
    private NotificationType type;
    private String text;
    private Long sdt;
    private String date;

    public enum NotificationType {
        NONE, ADD_CONTACT_REQUEST, LOCATION, LOCATION_SHARING_CONSENT, SCHEDULER
    }

    //region Getters & Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getSdt() {
        return sdt;
    }

    public void setSdt(Long sdt) {
        this.sdt = sdt;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
//        if (sdt != 0) {
//            Date date = new Date(sdt);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
//            return dateFormat.format(date);
//        } else {
//            return null;
//        }
        return this.date;
    }
    //endregion

    public static List<Notification> all() {
        return NotificationDAO.getInstance().getAllNotifications();
    }

    public static Notification load(int notificationId) {
        return NotificationDAO.getInstance().getNotificationByNotificationId(notificationId);
    }

    public void save() {
        Notification notification = load(notificationId);
        if (notification != null) {
            this.setId(notification.getId());
        }
        NotificationDAO.getInstance().insert(this);
    }

    public void delete() {
        NotificationDAO.getInstance().delete(this);
    }
}
