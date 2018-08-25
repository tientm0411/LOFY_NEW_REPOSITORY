package lofy.fpt.edu.vn.lofy_ver108.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userId;
    private String userName;
    private String urlAvatar;
    private String userPhone;
    private double userLati;
    private double userLongti;
    public User() {
    }

    protected User(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        urlAvatar = in.readString();
        userPhone = in.readString();
        userLati = in.readDouble();
        userLongti = in.readDouble();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public double getUserLati() {
        return userLati;
    }

    public void setUserLati(double userLati) {
        this.userLati = userLati;
    }

    public double getUserLongti() {
        return userLongti;
    }

    public void setUserLongti(double userLongti) {
        this.userLongti = userLongti;
    }

    public User(String userId, String userName, String urlAvatar, String userPhone, double userLati, double userLongti) {

        this.userId = userId;
        this.userName = userName;
        this.urlAvatar = urlAvatar;
        this.userPhone = userPhone;
        this.userLati = userLati;
        this.userLongti = userLongti;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(urlAvatar);
        parcel.writeString(userPhone);
        parcel.writeDouble(userLati);
        parcel.writeDouble(userLongti);
    }
}
