package com.phloxinc.whereworks.bo;

import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.MemberDAO;

import java.util.List;

public class Member extends BaseEntity {

    private int memberId;
    private String fullName;
    private String email;
    private String contactNumber;
    private String status;
    private String timeZone;
    private String currentLat;
    private String currentLng;
    private String memberPhoto;
    private long sdt;
    private long udt;

    //region Getters & Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getMemberPhoto() {
        return memberPhoto;
    }

    public void setMemberPhoto(String memberPhoto) {
        this.memberPhoto = memberPhoto;
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

    public String getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(String currentLat) {
        this.currentLat = currentLat;
    }

    public String getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(String currentLng) {
        this.currentLng = currentLng;
    }
    //endregion

    public static List<Member> all() {
        return MemberDAO.getInstance().getAllMember();
    }

    public static List<Member> allNonPending() {
        return MemberDAO.getInstance().getAllNonPendingMember();
    }

    public static Member load(int memberId) {
        return MemberDAO.getInstance().getMemberByMemberId(memberId);
    }

    public void save() {
        Member member = load(memberId);
        if (member != null) {
            this.setId(member.getId());
        }
        MemberDAO.getInstance().insert(this);
    }

    public void delete() {
        MemberDAO.getInstance().delete(this);
    }
}
