package com.example.akshay.demo;

/**
 * Created by akshay on 4/16/2017.
 */

public class Trips {
    String title;
    String location;
    String photo;

    public String getTpId() {
        return tpId;
    }

    public void setTpId(String tpId) {
        this.tpId = tpId;
    }

    String tpId;

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    String adminId;
    public String CheckAndGetDpUrl() {
        if(photo.equals(""))
        {
            return null;
        }else
        {
            return photo;
        }}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
