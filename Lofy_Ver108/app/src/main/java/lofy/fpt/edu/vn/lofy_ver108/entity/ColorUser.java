package lofy.fpt.edu.vn.lofy_ver108.entity;

public class ColorUser {
    private int id;
    private String colorCodeHex;

    public ColorUser() {
    }

    public ColorUser(int id, String colorCodeHex) {
        this.id = id;
        this.colorCodeHex = colorCodeHex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColorCodeHex() {
        return colorCodeHex;
    }

    public void setColorCodeHex(String colorCodeHex) {
        this.colorCodeHex = colorCodeHex;
    }
}
