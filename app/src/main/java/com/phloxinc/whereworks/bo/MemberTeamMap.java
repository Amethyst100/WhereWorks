package com.phloxinc.whereworks.bo;


import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.MemberDAO;
import com.phloxinc.whereworks.database.dao.MemberTeamMapsDAO;

import java.util.ArrayList;
import java.util.List;

public class MemberTeamMap extends BaseEntity {

    private int teamId;
    private int memberId;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public static List<Member> getMembers(int teamId) {
        List<MemberTeamMap> mapList = MemberTeamMapsDAO.getInstance().getMapListByTeamId(teamId);
        List<Member> memberList = new ArrayList<>();
        for (MemberTeamMap map : mapList) {
            Member member = MemberDAO.getInstance().getMemberByMemberId(map.getMemberId());
            memberList.add(member);
        }
        return memberList;
    }

    public static List<MemberTeamMap> getMapsById(int memberId) {
        return MemberTeamMapsDAO.getInstance().getMapListByMemberId(memberId);
    }

    public static MemberTeamMap load(int teamId, int memberId) {
        return MemberTeamMapsDAO.getInstance().getMap(teamId, memberId);
    }

    public void save() {
        MemberTeamMap team = load(teamId, memberId);
        if (team != null) {
            this.setId(team.getId());
        }
        MemberTeamMapsDAO.getInstance().insert(this);
    }

    public void delete() {
        MemberTeamMapsDAO.getInstance().delete(this);
    }

    public static List<MemberTeamMap> all() {
        return MemberTeamMapsDAO.getInstance().getAllMaps();
    }
}
