package com.phloxinc.whereworks.bo;

import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.ChatDAO;

import java.util.List;

public class Chat extends BaseEntity {

    private int memberId;
    private int teamId;
    private Type type;

    public enum Type {
        ONE_TO_ONE, TEAM
    }

    //region Getters & Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Member getMember() {
        return Member.load(memberId);
    }

    public Team getTeam() {
        return Team.load(teamId);
    }

    public String getChatName() {
        if (type == Type.ONE_TO_ONE) {
            Member member = getMember();
            return member != null ? member.getFullName() : "";
        } else if (type == Type.TEAM) {
            Team team = getTeam();
            return team != null ? team.getName() : "";
        }
        return "";
    }

    public List<Message> getMessages() {
        return Message.load(Integer.parseInt(String.valueOf(getId())));
    }
    //endregion

    public static List<Chat> all() {
        return ChatDAO.getInstance().getAllChats();
    }

    public static Chat load(long chatId) {
        return ChatDAO.getInstance().getChat(chatId);
    }

    public static Chat loadByMemberId(int memberId) {
        return ChatDAO.getInstance().getOneToOneChat(memberId);
    }

    public static Chat loadByTeamId(int teamId) {
        return ChatDAO.getInstance().getTeamChat(teamId);
    }

    public void save() {
        Chat chat = load(getId());
        if (chat != null) {
            this.setId(chat.getId());
        }
        ChatDAO.getInstance().insert(this);
    }

    public void delete() {
        ChatDAO.getInstance().delete(this);
    }
}
