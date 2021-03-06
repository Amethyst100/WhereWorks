package com.phloxinc.whereworks.process;

public interface Process {

    String MEMBER_LOGIN = "MEMBER_LOGIN";
    String MEMBER_VALIDATE = "MEMBER_VALIDATE";
    String MEMBER_DETAIL = "MEMBER_DETAIL";

    String MEMBER_INVITATION = "MEMBER_INVITATION";
    String MEMBER_INVITATION_RESPONSE = "MEMBER_INVITATION_RESPONSE";
    String MEMBER_REMOVE_CONTACT = "MEMBER_REMOVE_CONTACT";
    String MEMBER_INVITATION_LIST = "MEMBER_INVITATION_LIST";
    String MEMBER_TEAM_LIST = "MEMBER_TEAM_LIST";

    String MEMBER_TIMELINE_LIST = "MEMBER_TIMELINE_LIST";
    String MEMBER_NOTIFICATION_LIST = "MEMBER_NOTIFICATION_LIST";

    String TEAM_LIST = "TEAM_LIST";
    String TEAM_ADD = "TEAM_ADD";
    String TEAM_UPDATE = "TEAM_UPDATE";
    String TEAM_DELETE = "TEAM_DELETE";
    String TEAM_MEMBER_LIST = "TEAM_MEMBER_LIST";
    String MEMBER_TEAM_ADD = "MEMBER_TEAM_ADD";
    String MEMBER_TEAM_DELETE = "MEMBER_TEAM_DELETE";
    String MEMBER_TEAM_LEAVE = "MEMBER_TEAM_LEAVE";

    String MEMBER_UPDATE = "MEMBER_UPDATE";
    String MEMBER_CHANGE_PASSWORD = "MEMBER_CHANGE_PASSWORD";
    String MEMBER_FORGOT_PASS = "MEMBER_FORGOT_PASS";

    String MEMBER_PUBLIC_DETAIL = "MEMBER_PUBLIC_DETAIL";
    String MEMBER_PUBLIC_NOTIFICATION_LIST = "MEMBER_PUBLIC_NOTIFICATION_LIST";
    String MEMBER_PUBLIC_TIMELINE_LIST = "MEMBER_PUBLIC_TIMELINE_LIST";

    String MEMBER_LIVE_LOCATION_LIST = "MEMBER_LIVE_LOCATION_LIST";
    String MEMBER_OLD_LOCATION_LIST = "MEMBER_OLD_LOCATION_LIST";
    String MEMBER_APP_LOCATION_ADD = "MEMBER_APP_LOCATION_ADD";
    String MEMBER_CHECKLOCSTAT_UPDATE = "MEMBER_CHECKLOCSTAT_UPDATE";
    String MEMBER_CHECKLOCSTAT_LIST = "MEMBER_CHECKLOCSTAT_LIST";

    String PHOTO_CHANGE = "PHOTO_CHANGE";
    String ADD_NOTIFICATION = "ADD_NOTIFICATION";

    String MEMBER_LOGOUT = "MEMBER_LOGOUT";
}
