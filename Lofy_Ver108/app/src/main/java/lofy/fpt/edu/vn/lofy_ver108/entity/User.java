package lofy.fpt.edu.vn.lofy_ver108.entity;

public class User {
    private String userId;
    private String userName;
    private String urlAvatar;
    private String userPhone;
    private double userLati;
    private double userLongti;
    public User() {
    }

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
}
