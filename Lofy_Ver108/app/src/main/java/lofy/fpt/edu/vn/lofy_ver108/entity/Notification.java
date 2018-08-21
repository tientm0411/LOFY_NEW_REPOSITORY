package lofy.fpt.edu.vn.lofy_ver108.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Notification implements Parcelable{
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

    protected Notification(Parcel in) {
        notiID = in.readString();
        notiName = in.readString();
        groupID = in.readString();
        userID = in.readString();
        noti_type = in.readString();
        noti_time = in.readString();
        noti_icon = in.readString();
        noti_content = in.readString();
        latitude = in.readDouble();
        longtitude = in.readDouble();
        mess = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(notiID);
        parcel.writeString(notiName);
        parcel.writeString(groupID);
        parcel.writeString(userID);
        parcel.writeString(noti_type);
        parcel.writeString(noti_time);
        parcel.writeString(noti_icon);
        parcel.writeString(noti_content);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longtitude);
        parcel.writeString(mess);
    }
}
