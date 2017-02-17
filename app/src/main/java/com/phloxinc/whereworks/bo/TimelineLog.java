package com.phloxinc.whereworks.bo;

import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.TimelineLogDAO;

import java.util.List;

public class TimelineLog extends BaseEntity {

    private int logId;
    private int actorId;
    private int parentId;
    private int childId;
    private LogType logType;
    private LogAction logAction;
    private String name;
    private String teamName;
    private String date;
    private long sdt;

    public enum LogType {
        NONE, TEAM, GEOFENCE, MEMBER, LOCATION, NOTIFICATION, PACKAGE, PAYMENT
    }

    public enum LogAction {
        NONE, ADD, UPDATE, DELETE, LIST, VERIFY, LOGOUT
    }

    //region Getters & Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public LogAction getLogAction() {
        return logAction;
    }

    public void setLogAction(LogAction logAction) {
        this.logAction = logAction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSdt() {
        return sdt;
    }

    public void setSdt(long sdt) {
        this.sdt = sdt;
    }
    //endregion

    public static List<TimelineLog> all() {
        return TimelineLogDAO.getInstance().getAllTimelineLogs();
    }

    public static TimelineLog load (int logId) {
        return TimelineLogDAO.getInstance().getTimelineLogById(logId);
    }

    public void save() {
        TimelineLog timelineLog = load(logId);
        if (timelineLog != null) {
            this.setId(timelineLog.getId());
        }
        TimelineLogDAO.getInstance().insert(this);
    }

    public void delete() {
        TimelineLogDAO.getInstance().delete(this);
    }

}
