package lofy.fpt.edu.vn.lofy_ver108.entity;

import com.google.android.gms.maps.model.LatLng;

public class Notification {
    private String notiID;
    private String notiName;
    private String groupID;
    private String userID;
    private String noti_type;
    private String noti_time;
    private String noti_icon;
    private String noti_content;
    private double latitude;
    private double longtitude;
    private String mess;

    public Notification() {
    }

    public Notification(String notiID, String notiName, String groupID, String userID, String noti_type, String noti_time, String noti_icon, String noti_content, double latitude, double longtitude, String mess) {
        this.notiID = notiID;
        this.notiName = notiName;
        this.groupID = groupID;
        this.userID = userID;
        this.noti_type = noti_type;
        this.noti_time = noti_time;
        this.noti_icon = noti_icon;
        this.noti_content = noti_content;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.mess = mess;
    }

    public String getNotiID() {

        return notiID;
    }

    public void setNotiID(String notiID) {
        this.notiID = notiID;
    }

    public String getNotiName() {
        return notiName;
    }

    public void setNotiName(String notiName) {
        this.notiName = notiName;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNoti_type() {
        return noti_type;
    }

    public void setNoti_type(String noti_type) {
        this.noti_type = noti_type;
    }

    public String getNoti_time() {
        return noti_time;
    }

    public void setNoti_time(String noti_time) {
        this.noti_time = noti_time;
    }

    public String getNoti_icon() {
        return noti_icon;
    }

    public void setNoti_icon(String noti_icon) {
        this.noti_icon = noti_icon;
    }

    public String getNoti_content() {
        return noti_content;
    }

    public void setNoti_content(String noti_content) {
        this.noti_content = noti_content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }
}
