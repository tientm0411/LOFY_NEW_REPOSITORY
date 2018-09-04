package lofy.fpt.edu.vn.lofy_ver108.entity;

import android.graphics.Bitmap;

public class BitmapUser {
private String userId;
private Bitmap bitmap;

    public BitmapUser() {
    }

    public BitmapUser(String userId, Bitmap bitmap) {

        this.userId = userId;
        this.bitmap = bitmap;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
