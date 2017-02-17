package com.phloxinc.whereworks.bo;

import com.phloxinc.whereworks.database.BaseEntity;
import com.phloxinc.whereworks.database.dao.LocationLogDAO;

import java.util.List;

public class LocationLog extends BaseEntity {

    private int locationId;
    private int memberId;
    private String name;
    private String number;
    private String email;
    private double lat;
    private double lng;
    private String date;
    private String locEntry;
    private String locUpdate;
    private String text;
    private String city;

    //region Getters & Setters
    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocEntry() {
        return locEntry;
    }

    public void setLocEntry(String locEntry) {
        this.locEntry = locEntry;
    }

    public String getLocUpdate() {
        return locUpdate;
    }

    public void setLocUpdate(String locUpdate) {
        this.locUpdate = locUpdate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text != null)
            this.text = text;
        else
            this.text = "";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city != null)
            this.city = city;
        else
            this.city = "";
    }
    //endregion

    public static List<LocationLog> all() {
        return LocationLogDAO.getInstance().getAllLogs();
    }

    public static LocationLog load(int logId) {
        return LocationLogDAO.getInstance().getLog(logId);
    }

    public static List<LocationLog> loadByMemberId(int memberId) {
        return LocationLogDAO.getInstance().getLogsByMemberId(memberId);
    }

    public void save() {
        LocationLog log = load(locationId);
        if (log != null) {
            this.setId(log.getId());
        }
        LocationLogDAO.getInstance().insert(this);
    }

    public void delete() {
        LocationLogDAO.getInstance().delete(this);
    }
}
