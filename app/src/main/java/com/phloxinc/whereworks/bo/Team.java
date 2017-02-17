package com.phloxinc.whereworks.bo;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.TeamDAO;

import java.util.List;

public class Team extends BaseEntity implements ParentListItem {

    private int teamId;
    private String name;
    private String description;
    private int parentId;
    private int headId;
    private String teamPhoto;
    private long sdt;
    private long udt;

    //region Getters & Setters
    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public String getTeamPhoto() {
        return teamPhoto;
    }

    public void setTeamPhoto(String teamPhoto) {
        this.teamPhoto = teamPhoto;
    }

    public long getSdt() {
        return sdt;
    }

    public void setSdt(long sdt) {
        this.sdt = sdt;
    }

    public long getUdt() {
        return udt;
    }

    public void setUdt(long udt) {
        this.udt = udt;
    }
    //endregion

    public static List<Team> all() {
        return TeamDAO.getInstance().getAllTeams();
    }

    public static List<Team> allParentTeams() {
        return TeamDAO.getInstance().getParentTeams();
    }

    public static Team load(int teamId) {
        return TeamDAO.getInstance().getTeamByTeamId(teamId);
    }

    public void save() {
        Team team = load(teamId);
        if (team != null) {
            this.setId(team.getId());
        }
        TeamDAO.getInstance().insert(this);
    }

    public void delete() {
        TeamDAO.getInstance().delete(this);
    }

    public List<Member> getMembers() {
        return MemberTeamMap.getMembers(teamId);
    }

    @Override
    public List<?> getChildItemList() {
        return TeamDAO.getInstance().getSubTeams(teamId);
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public void addMember(Member member) {
        MemberTeamMap map = new MemberTeamMap();
        map.setTeamId(teamId);
        map.setMemberId(member.getMemberId());
        map.save();
    }

    public void removeMember(Member member) {
        MemberTeamMap map = MemberTeamMap.load(teamId, member.getMemberId());
        map.delete();
    }
}
