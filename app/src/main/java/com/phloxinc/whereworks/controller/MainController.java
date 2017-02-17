package com.phloxinc.whereworks.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.bo.LocationLog;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.MemberTeamMap;
import com.phloxinc.whereworks.bo.Message;
import com.phloxinc.whereworks.bo.Notification;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.bo.TimelineLog;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.controller.auth.AuthController;
import com.phloxinc.whereworks.http.RequestManager;
import com.phloxinc.whereworks.prefs.Prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MainController {

    private static MainController instance;

    private String userId;
    private String token;

    private MainController() {
        setToken();
    }

    private void setToken() {
        userId = Prefs.getString("userId", "");
        token = Prefs.getString("token", "");
    }

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        instance.setToken();
        return instance;
    }

    public String login(String email, String password) {
        return AuthController.getInstance().login(email, password);
    }

    public String loginWithNumber(String number, String password) {
        return AuthController.getInstance().loginWithNumber(number, password);
    }

    public String validateMember(String email, String number, String name) {
        return AuthController.getInstance().validateMember(email, number, name, userId, token);
    }

    public String getMemberInfo() {
        return AuthController.getInstance().getMemberInfo(userId, token);
    }

    public String updateMemberInfo(String name, String number, String timezone) {
        return AuthController.getInstance().updateMemberInfo(name, number, timezone, userId, token);
    }

    public String changePassword(String oldPassword, String newPassword) {
        return AuthController.getInstance().changePassword(oldPassword, newPassword, userId, token);
    }

    public String forgotPassword(String email) {
        return AuthController.getInstance().forgotPassword(email);
    }


    public String sendContactRequest(String email, String name, String number) {
        return ContactController.getInstance().sendContactRequest(email, name, number, userId, token);
    }

    public String sendContactResponse(String parentId, String response) {
        return ContactController.getInstance().sendContactResponse(parentId, response, userId, token);
    }

    public String removeContact(String teamId, String memberId) {
        return ContactController.getInstance().removeContact(teamId, memberId, userId, token);
    }

    public List<?> getInvitationList() {
        return ContactController.getInstance().getInvitationList(userId, token);
    }


    public List<TimelineLog> getTimelineLogs() {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_TIMELINE_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> timelineMapList = (ArrayList<Map<String, Object>>) response.get("data");
//                    List<TimelineLog> timelineLogList = new ArrayList<>();
                    for (TimelineLog timelineLog : TimelineLog.all()) {
                        timelineLog.delete();
                    }

                    for (Map<String, Object> timelineMap : timelineMapList) {
                        TimelineLog timelineLog = new TimelineLog();
                        timelineLog.setLogId(Integer.parseInt(String.valueOf(timelineMap.get("mem_log_id"))));
                        timelineLog.setActorId(Integer.parseInt(String.valueOf(timelineMap.get("mem_log_actor"))));
                        timelineLog.setParentId(Integer.parseInt(String.valueOf(timelineMap.get("mem_parent_id"))));
                        timelineLog.setChildId(Integer.parseInt(String.valueOf(timelineMap.get("mem_child_id"))));
                        timelineLog.setLogType(TimelineLog.LogType.values()[Integer.parseInt(String.valueOf(timelineMap.get("mem_log_type")))]);
                        timelineLog.setLogAction(TimelineLog.LogAction.values()[Integer.parseInt(String.valueOf(timelineMap.get("mem_log_action")))]);
                        if (timelineMap.containsKey("mem_log_desc")) {
                            if (timelineMap.get("mem_log_desc") != null) {
                                Map<String, String> descMap = (Map<String, String>) timelineMap.get("mem_log_desc");
                                if (descMap.containsKey("name"))
                                    timelineLog.setName(descMap.get("name"));
                                if (descMap.containsKey("teamname"))
                                    timelineLog.setTeamName(descMap.get("teamname"));
                                if (descMap.containsKey("date"))
                                    timelineLog.setDate(descMap.get("date"));
                            } else {
                                continue;
                            }
                        }
//                        timelineLog.setSdt(Long.parseLong(String.valueOf(timelineMap.get("mem_log_sdt"))));
                        timelineLog.save();
//                        timelineLogList.add(timelineLog);
                    }
                    return TimelineLog.all();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Notification> getNotifications() {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_NOTIFICATION_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> notificationMapList = (ArrayList<Map<String, Object>>) response.get("data");
//                    List<Notification> notificationList = new ArrayList<>();
                    for (Map<String, Object> notificationMap : notificationMapList) {
                        Notification notification = new Notification();
                        notification.setNotificationId(Integer.parseInt(String.valueOf(notificationMap.get("notify_id"))));

                        if (notificationMap.containsKey("mem_MSISDN")) {
                            notification.setNumber(String.valueOf(notificationMap.get("mem_MSISDN")));
                        }

                        if (notificationMap.containsKey("mem_email")) {
                            notification.setEmail(String.valueOf(notificationMap.get("mem_email")));
                        }

                        if (notificationMap.containsKey("mem_full_name")) {
                            notification.setFullName(String.valueOf(notificationMap.get("mem_full_name")));
                        }

                        if (notificationMap.containsKey("mem_team_name")) {
                            notification.setTeamName(String.valueOf(notificationMap.get("mem_team_name")));
                        }

                        if (notificationMap.containsKey("mem_receiver_id")) {
                            notification.setReceiverId(Integer.parseInt(String.valueOf(notificationMap.get("mem_receiver_id"))));
                        }

                        if (notificationMap.containsKey("mem_sender_id")) {
                            notification.setSenderId(Integer.parseInt(String.valueOf(notificationMap.get("mem_sender_id"))));
                        }

                        if (notificationMap.containsKey("notify_type")) {
                            notification.setType(Notification.NotificationType.values()[Integer.parseInt(String.valueOf(notificationMap.get("notify_type")))]);
                        }

                        if (notificationMap.containsKey("notify_text")) {
                            notification.setText(String.valueOf(notificationMap.get("notify_text")));
                        }

                        if (notificationMap.containsKey("notify_sdt")) {
//                            notification.setSdt(Long.parseLong(String.valueOf(notificationMap.get("notify_sdt"))));
                            notification.setDate(String.valueOf(notificationMap.get("notify_sdt")));
                        }
                        notification.save();
//                        notificationList.add(notification);
                    }
                    return Notification.all();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String addLocation(String lat, String lng, String city, String address) {

        Map<String, String> params = new HashMap<>();
        params.put("Lat", lat);
        params.put("Lng", lng);
        if (city != null)
            params.put("city", city.replace(" ", "+"));
        if (address != null)
            params.put("text", address.replace(" ", "+"));
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_APP_LOCATION_ADD);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return "Success";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String updatePermission(String statusValue) {

        Map<String, String> params = new HashMap<>();
        params.put("statval", statusValue);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_CHECKLOCSTAT_UPDATE);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return "Success";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPermissionList() {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_CHECKLOCSTAT_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> stateList = (ArrayList<Map<String, Object>>) response.get("data");
                    return String.valueOf(stateList.get(0).get("meta_value"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMembersLiveLocationList(String teamId) {

        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("teammemid", "0");
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_LIVE_LOCATION_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> locationMapList = (ArrayList<Map<String, Object>>) response.get("data");
                    for (Map<String, Object> locationMap : locationMapList) {
                        LocationLog log = LocationLog.load(Integer.parseInt(String.valueOf(locationMap.get("loc_id"))));
                        if (log == null) {
                            log = new LocationLog();
                        }
                        log.setLocationId(Integer.parseInt(String.valueOf(locationMap.get("loc_id"))));
                        log.setMemberId(Integer.parseInt(String.valueOf(locationMap.get("mem_id"))));
                        log.setName(String.valueOf(locationMap.get("mem_full_name")));
                        log.setEmail(String.valueOf(locationMap.get("mem_email")));
                        log.setNumber(String.valueOf(locationMap.get("mem_MSISDN")));
                        log.setLat(Double.parseDouble(String.valueOf(locationMap.get("loc_lat"))));
                        log.setLng(Double.parseDouble(String.valueOf(locationMap.get("loc_lng"))));
                        log.setDate(String.valueOf(locationMap.get("loc_sdt")));
                        log.setLocEntry(String.valueOf(locationMap.get("loc_entry")));
                        log.setLocUpdate(String.valueOf(locationMap.get("loc_update")));
                        log.setText(String.valueOf(locationMap.get("location_text")));
                        log.setCity(String.valueOf(locationMap.get("location_city")));
                        log.save();
                    }
                    return "Success";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LocationLog> getMembersOldLocationList(String teamId, String memberId, String date, String fromTime, String toTime) {
        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("teammemid", memberId);
        params.put("fromtime", fromTime);
        params.put("totime", toTime);
        params.put("locdate", date);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_OLD_LOCATION_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> locationMapList = (ArrayList<Map<String, Object>>) response.get("data");
                    for (Map<String, Object> locationMap : locationMapList) {
                        LocationLog log = LocationLog.load(Integer.parseInt(String.valueOf(locationMap.get("loc_id"))));
                        if (log == null) {
                            log = new LocationLog();
                        }
                        log.setLocationId(Integer.parseInt(String.valueOf(locationMap.get("loc_id"))));
                        log.setMemberId(Integer.parseInt(String.valueOf(locationMap.get("mem_id"))));
                        log.setName(String.valueOf(locationMap.get("mem_full_name")));
                        log.setEmail(String.valueOf(locationMap.get("mem_email")));
                        log.setNumber(String.valueOf(locationMap.get("mem_MSISDN")));
                        log.setLat(Double.parseDouble(String.valueOf(locationMap.get("loc_lat"))));
                        log.setLng(Double.parseDouble(String.valueOf(locationMap.get("loc_lng"))));
                        log.setDate(String.valueOf(locationMap.get("loc_sdt")));
                        log.setLocEntry(String.valueOf(locationMap.get("loc_entry")));
                        log.setLocUpdate(String.valueOf(locationMap.get("loc_update")));
                        log.setText(String.valueOf(locationMap.get("location_text")));
                        log.setCity(String.valueOf(locationMap.get("location_city")));
                        log.save();
                    }
                    return LocationLog.loadByMemberId(Integer.parseInt(memberId));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Member> getContactList() {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_TEAM_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Member> memberList = Member.all();
                    for (Member member : memberList) {
                        member.delete();
                    }
                    List<Map<String, Object>> memberMapList = (ArrayList<Map<String, Object>>) response.get("data");
                    for (Map<String, Object> memberMap : memberMapList) {
                        Member member = Member.load(Integer.parseInt(String.valueOf(memberMap.get("mem_child_id"))));
                        if (member == null) {
                            member = new Member();
                        }
                        member.setMemberId(Integer.parseInt(String.valueOf(memberMap.get("mem_child_id"))));
                        if (memberMap.containsKey("mem_MSISDN")) {
                            member.setContactNumber(String.valueOf(memberMap.get("mem_MSISDN")));
                        }
                        if (memberMap.containsKey("mem_full_name")) {
                            member.setFullName(String.valueOf(memberMap.get("mem_full_name")));
                        }
                        if (memberMap.containsKey("mem_email")) {
                            member.setEmail(String.valueOf(memberMap.get("mem_email")));
                        }
                        if (memberMap.containsKey("team_mem_status")) {
                            member.setStatus(String.valueOf(memberMap.get("team_mem_status")));
                        }
                        if (memberMap.containsKey("mem_photo")) {
                            member.setMemberPhoto(String.valueOf(memberMap.get("mem_photo")));
                        }

                        member.save();
                    }

                    return Member.all();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Team> getTeams() {
        return TeamController.getInstance().getTeams(userId, token);
    }

    public String createTeam(String name, String desc, String parentId) {
        return TeamController.getInstance().createTeam(name, desc, parentId, userId, token);
    }

    public String deleteTeam(String teamId) {
        return TeamController.getInstance().deleteTeam(teamId, userId, token);
    }

    public String updateTeam(String teamId, String name, String desc, String parentId) {
        return TeamController.getInstance().updateTeam(teamId, name, desc, parentId, userId, token);
    }

    public List<Member> getTeamMembers(String teamId) {
        return TeamController.getInstance().getTeamMembers(teamId, userId, token);
    }

    public String addMember(String teamId, String memberId) {
        return TeamController.getInstance().addMember(teamId, memberId, userId, token);
    }

    public String removeMember(String teamId, String memberId) {
        return TeamController.getInstance().removeMember(teamId, memberId, userId, token);
    }

    public String leaveTeam(String teamId, String teamOwnerId) {
        return TeamController.getInstance().leaveTeam(teamId, teamOwnerId, userId, token);
    }

    public String addNotification(String teamId, String receiverId, String senderId, String text, String type, String status) {

        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("receiverid", receiverId);
        params.put("senderid", senderId);
        params.put("text", text);
        params.put("type", type);
        params.put("userid", userId);
        params.put("token", token);
        params.put("status", status);
        params.put("process", Process.ADD_NOTIFICATION);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return "Success";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String updatePhoto(String teamId, String type, String fileName) {

        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("type", type);
        params.put("userid", userId);
        params.put("token", token);
        params.put("file", fileName);
        params.put("process", Process.PHOTO_CHANGE);


        try {
            Map<String, Object> response = RequestManager.getInstance().postMultipart(Constants.SERVER_URL, params);
//            Map<String, Object> response = RequestManager.getInstance().postUpload(Constants.SERVER_URL, params);
//            if (response != null) {
//                String message = (String) response.get("message");
//                if (message.contains("Success")) {
//                    return "Success";
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearData() {
        Prefs.putBoolean("Login", false);
        Prefs.putBoolean("Reporting", false);
        Prefs.putString("userName", "");
        Prefs.putString("userEmail", "");
        Prefs.putString("userNumber", "");
        Prefs.putString("LiveTrack", "");
        List<Member> memberList = Member.all();
        for (Member member : memberList) {
            member.delete();
        }

        List<MemberTeamMap> memberTeamMapList = MemberTeamMap.all();
        for (MemberTeamMap memberTeamMap : memberTeamMapList) {
            memberTeamMap.delete();
        }

        List<Team> teamList = Team.all();
        for (Team team : teamList) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("T" + team.getTeamId());
            team.delete();
        }

        List<Chat> chatList = Chat.all();
        for (Chat chat : chatList) {
            chat.delete();
        }

        List<Message> messageList = Message.all();
        for (Message message : messageList) {
            message.delete();
        }

        List<TimelineLog> timelineLogList = TimelineLog.all();
        for (TimelineLog timelineLog : timelineLogList) {
            timelineLog.delete();
        }

        List<Notification> notificationList = Notification.all();
        for (Notification notification : notificationList) {
            notification.delete();
        }
    }
}
