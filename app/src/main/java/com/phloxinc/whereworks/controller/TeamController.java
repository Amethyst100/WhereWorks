package com.phloxinc.whereworks.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.phloxinc.whereworks.bo.Chat;
import com.phloxinc.whereworks.bo.MemberTeamMap;
import com.phloxinc.whereworks.constant.Constants;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.bo.Team;
import com.phloxinc.whereworks.http.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
class TeamController {

    private static TeamController instance;

    public static TeamController getInstance() {
        if (instance == null) {
            instance = new TeamController();
        }
        return instance;
    }

    List<Team> getTeams(String userId, String token) {

        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.TEAM_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    Map<String, Object> teamMapListGroups = (Map<String, Object>) response.get("data");
                    List<Map<String, Object>> teamMapList = (ArrayList<Map<String, Object>>) teamMapListGroups.get("MYTEAM");
                    for (Map<String, Object> teamMap : teamMapList) {
                        Team team = new Team();
                        if (teamMap.containsKey("mem_team_id") && teamMap.get("mem_team_id") != null) {
                            team.setTeamId(Integer.parseInt(String.valueOf(teamMap.get("mem_team_id"))));
                            if (team.getTeamId() == 0) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                        if (teamMap.containsKey("mem_team_name") && teamMap.get("mem_team_name") != null) {
                            team.setName(String.valueOf(teamMap.get("mem_team_name")));
                        } else {
                            continue;
                        }
                        if (teamMap.containsKey("mem_team_desc") && teamMap.get("mem_team_desc") != null) {
                            team.setDescription(String.valueOf(teamMap.get("mem_team_desc")));
                        }
                        if (teamMap.containsKey("mem_team_pid") && teamMap.get("mem_team_pid") != null) {
                            team.setParentId(Integer.parseInt(String.valueOf(teamMap.get("mem_team_pid"))));
                        }
                        if (teamMap.containsKey("mem_parent_id") && teamMap.get("mem_parent_id") != null) {
                            team.setHeadId(Integer.parseInt(String.valueOf(teamMap.get("mem_parent_id"))));
                        }
                        if (teamMap.containsKey("team_photo") && teamMap.get("team_photo") != null) {
                            team.setTeamPhoto(String.valueOf(teamMap.get("team_photo")));
                        }

                        if (team.getTeamId() != 0) {
                            team.save();
                            FirebaseMessaging.getInstance().subscribeToTopic("T" + team.getTeamId());
                        }
                    }

                    List<Map<String, Object>> otherTeamMapList = (ArrayList<Map<String, Object>>) teamMapListGroups.get("OTHERTEAM");
                    for (Map<String, Object> teamMap : otherTeamMapList) {
                        Team team = new Team();
                        if (teamMap.containsKey("mem_team_id") && teamMap.get("mem_team_id") != null) {
                            team.setTeamId(Integer.parseInt(String.valueOf(teamMap.get("mem_team_id"))));
                            if (team.getTeamId() == 0) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                        if (teamMap.containsKey("mem_team_name") && teamMap.get("mem_team_name") != null) {
                            team.setName(String.valueOf(teamMap.get("mem_team_name")));
                        } else {
                            continue;
                        }
                        if (teamMap.containsKey("mem_team_desc") && teamMap.get("mem_team_desc") != null) {
                            team.setDescription(String.valueOf(teamMap.get("mem_team_desc")));
                        }
                        if (teamMap.containsKey("mem_team_pid") && teamMap.get("mem_team_pid") != null) {
                            team.setParentId(Integer.parseInt(String.valueOf(teamMap.get("mem_team_pid"))));
                        }
                        if (teamMap.containsKey("mem_parent_id") && teamMap.get("mem_parent_id") != null) {
                            team.setHeadId(Integer.parseInt(String.valueOf(teamMap.get("mem_parent_id"))));
                        }

                        if (team.getTeamId() != 0) {
                            team.save();
                            FirebaseMessaging.getInstance().subscribeToTopic("T" + team.getTeamId());
                        }
                    }
                    return Team.allParentTeams();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    String createTeam(String name, String desc, String parentId, String userId, String token) {

        Map<String, String> params = new HashMap<>();
        params.put("teamname", name);
        params.put("teamdesc", desc);
        params.put("teampid", parentId);
        params.put("teamid", "0");
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.TEAM_ADD);

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

    String deleteTeam(String teamId, String userId, String token) {

        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.TEAM_DELETE);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    Chat chat = Chat.loadByTeamId(Integer.parseInt(teamId));
                    if (chat != null) {
                        chat.delete();
                    }
                    Team team = Team.load(Integer.parseInt(teamId));
                    team.delete();
                    return "Success";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    String updateTeam(String teamId, String name, String desc, String parentId, String userId, String token) {

        Map<String, String> params = new HashMap<>();
        params.put("teamname", name);
        params.put("teamdesc", desc);
        params.put("teampid", parentId);
        params.put("teamid", teamId);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.TEAM_UPDATE);

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

    List<Member> getTeamMembers(String teamId, String userId, String token) {

        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.TEAM_MEMBER_LIST);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    List<Map<String, Object>> memberMapList = (ArrayList<Map<String, Object>>) response.get("data");
                    List<Member> memberList = new ArrayList<>();
                    for (Map<String, Object> memberMap : memberMapList) {
                        int memberId = Integer.parseInt(String.valueOf(memberMap.get("mem_child_id")));
                        if (memberId != Integer.parseInt(userId)) {
                            Member member = Member.load(memberId);
                            if (member == null) {
                                member = new Member();
                            }
                            member.setMemberId(Integer.parseInt(String.valueOf(memberMap.get("mem_child_id"))));
                            member.setFullName(String.valueOf(memberMap.get("mem_full_name")));
                            member.setEmail(String.valueOf(memberMap.get("mem_email")));
                            member.setContactNumber(String.valueOf(memberMap.get("mem_MSISDN")));
                            member.save();
                            MemberTeamMap map = new MemberTeamMap();
                            map.setTeamId(Integer.parseInt(teamId));
                            map.setMemberId(member.getMemberId());
                            map.save();
                            memberList.add(member);
                        }
                    }
                    return memberList;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    String addMember(String teamId, String memberId, String userId, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("childid", memberId);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_TEAM_ADD);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    String removeMember(String teamId, String memberId, String userId, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("childid", memberId);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_TEAM_DELETE);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    String leaveTeam(String teamId, String teamOwnerId, String userId, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("teamid", teamId);
        params.put("parentid", teamOwnerId);
        params.put("userid", userId);
        params.put("token", token);
        params.put("process", Process.MEMBER_TEAM_LEAVE);

        try {
            Map<String, Object> response = RequestManager.getInstance().post(Constants.SERVER_URL, params);
            if (response != null) {
                String message = (String) response.get("message");
                if (message.contains("Success")) {
                    return String.valueOf(response.get("data"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
