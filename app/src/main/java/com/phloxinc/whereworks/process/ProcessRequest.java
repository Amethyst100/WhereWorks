package com.phloxinc.whereworks.process;

import android.os.AsyncTask;

import com.phloxinc.whereworks.controller.MainController;

@SuppressWarnings("unchecked")
public class ProcessRequest<T> extends AsyncTask<String, Object, T> {

    private RequestListener<T> listener;
    private String process;

    public ProcessRequest (String process, RequestListener<T> listener) {
        this.process = process;
        this.listener = listener;
    }

    @Override
    protected T doInBackground(String... params) {
        MainController controller = MainController.getInstance();
        switch (process) {
            case Process.MEMBER_LOGIN:
                if (!params[0].isEmpty()) {
                    return (T) controller.login(params[0], params[2]);
                } else if (!params[1].isEmpty()) {
                    return (T) controller.loginWithNumber(params[1], params[2]);
                }
            case Process.MEMBER_VALIDATE:
                return (T) controller.validateMember(params[0], params[1], params[2]);
            case Process.MEMBER_DETAIL:
                return (T) controller.getMemberInfo();
            case Process.MEMBER_UPDATE:
                return (T) controller.updateMemberInfo(params[0], params[1], params[2]);
            case Process.MEMBER_CHANGE_PASSWORD:
                return (T) controller.changePassword(params[0], params[1]);
            case Process.MEMBER_FORGOT_PASS:
                return (T) controller.forgotPassword(params[0]);

            case Process.MEMBER_INVITATION:
                return (T) controller.sendContactRequest(params[0], params[1], params.length == 3 ? params[2] : "0");
            case Process.MEMBER_INVITATION_RESPONSE:
                return (T) controller.sendContactResponse(params[0], params[1]);
            case Process.MEMBER_REMOVE_CONTACT:
                return (T) controller.removeContact(params[0], params[1]);
            case Process.MEMBER_INVITATION_LIST:
                return (T) controller.getInvitationList();
            case Process.MEMBER_TEAM_LIST:
                return (T) controller.getContactList();


            case Process.MEMBER_TIMELINE_LIST:
                return (T) controller.getTimelineLogs();
            case Process.MEMBER_NOTIFICATION_LIST:
                return (T) controller.getNotifications();
            case Process.MEMBER_PUBLIC_DETAIL:
                return null;
            case Process.MEMBER_PUBLIC_NOTIFICATION_LIST:
                return null;
            case Process.MEMBER_PUBLIC_TIMELINE_LIST:
                return null;


            case Process.TEAM_LIST:
                return (T) controller.getTeams();
            case Process.TEAM_ADD:
                return (T) controller.createTeam(params[0], params[1], params.length == 3 ? params[2] : "0");
            case Process.TEAM_UPDATE:
                return (T) controller.updateTeam(params[0], params[1], params[2], params.length == 4 ? params[3] : "0");
            case Process.TEAM_DELETE:
                return (T) controller.deleteTeam(params[0]);
            case Process.TEAM_MEMBER_LIST:
                return (T) controller.getTeamMembers(params[0]);
            case Process.MEMBER_TEAM_ADD:
                return (T) controller.addMember(params[0], params[1]);
            case Process.MEMBER_TEAM_DELETE:
                return (T) controller.removeMember(params[0], params[1]);
            case Process.MEMBER_TEAM_LEAVE:
                return (T) controller.leaveTeam(params[0], params[1]);


            case Process.MEMBER_LIVE_LOCATION_LIST:
                return (T) controller.getMembersLiveLocationList(params[0]);
            case Process.MEMBER_OLD_LOCATION_LIST:
                return (T) controller.getMembersOldLocationList(params[0], params[1], params[2], params[3], params[4]);
            case Process.MEMBER_APP_LOCATION_ADD:
                return (T) controller.addLocation(params[0], params[1], params[2], params[3]);
            case Process.MEMBER_CHECKLOCSTAT_UPDATE:
                return (T) controller.updatePermission(params[0]);
            case Process.MEMBER_CHECKLOCSTAT_LIST:
                return (T) controller.getPermissionList();


            case Process.PHOTO_CHANGE:
                if (params.length == 2)
                    return (T) controller.updatePhoto(params[0], "2", params[1]); // for team pic
                else
                    return (T) controller.updatePhoto("0", "1", params[0]); // for member pic
            case Process.ADD_NOTIFICATION:
                return (T) controller.addNotification(params[0], params[1], params[2], params[3], "5", "2");


            case Process.MEMBER_LOGOUT:
                return null;


            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(T result) {
        if (listener != null) {
            if (result != null) {
                listener.onSuccess(process, result);
            } else {
                listener.onFailure(process);
            }
        }
    }

    public interface RequestListener<T> {
        void onSuccess(String process, T result);
        void onFailure(String process);
    }
}
