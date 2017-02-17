package com.phloxinc.whereworks.controller;

import android.util.Log;

import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.http.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ContactController {

    private static ContactController instance;
    public static final int ACCEPT_REQUEST = 1;
    public static final int REJECT_REQUEST = 2;

    public static ContactController getInstance() {
        if (instance == null) {
            instance = new ContactController();
        }
        return instance;
    }

    String sendContactRequest(String email, String name, String number, String userId, String token) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("msisdn", number);
            params.put("name", name);
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_INVITATION);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
//                    int memberId = Integer.parseInt(String.valueOf(response.get("data")));
//                    Member member = new Member();
//                    member.setMemberId(memberId);
//                    member.setFullName(name);
//                    member.setEmail(email);
//                    member.setContactNumber(number);
//                    member.save();
                    return "Success";
                }
            }
        } catch (IOException e) {
            Log.e("ContactController" , e.getMessage());
        }
        return null;
    }

    String sendContactResponse(String parentId, String inviteResponse, String userId, String token) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("parentid", parentId);
            params.put("inviteresp", inviteResponse);
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_INVITATION_RESPONSE);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("ContactController" , e.getMessage());
        }
        return null;
    }

    String removeContact(String teamId, String memberId, String userId, String token) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("teamid", teamId);
            params.put("childid", memberId);
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_REMOVE_CONTACT);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            Log.e("ContactController" , e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    List<Member> getInvitationList(String userId, String token) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("userid", userId);
            params.put("token", token);
            params.put("process", Process.MEMBER_INVITATION_LIST);

            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> invitationMapList = (ArrayList<Map<String, Object>>) response.get("data");
                    List<Member> memberList = new ArrayList<>();
                    for (Map<String, Object> invitationMap : invitationMapList) {
                        Member member = new Member();
                        member.setMemberId(Integer.parseInt(String.valueOf(invitationMap.get("mem_parent_id"))));
                        if (invitationMap.containsKey("mem_MSISDN")) {
                            member.setContactNumber(String.valueOf(invitationMap.get("mem_MSISDN")));
                        }
                        if (invitationMap.containsKey("mem_full_name")) {
                            member.setFullName(String.valueOf(invitationMap.get("mem_full_name")));
                        }
                        if (invitationMap.containsKey("mem_email")) {
                            member.setEmail(String.valueOf(invitationMap.get("mem_email")));
                        }
                        if (invitationMap.containsKey("team_mem_status")) {
                            member.setStatus(String.valueOf(invitationMap.get("team_mem_status")));
                        }
                        memberList.add(member);
                    }
                    return memberList;
                }
            }
        } catch (IOException e) {
            Log.e("ContactController" , e.getMessage());
        }
        return null;
    }
}
