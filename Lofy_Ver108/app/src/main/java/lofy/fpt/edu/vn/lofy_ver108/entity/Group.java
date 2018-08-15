package lofy.fpt.edu.vn.lofy_ver108.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class Group {
    private String groupId;
    private String groupName;
    private String start_Date;
    private String end_Date;
    private double start_Long;
    private double start_Lat;
    private double end_Long;
    private double end_Lat;
    private String status;
    private ArrayList<String> colorUser;
    private ArrayList<Polyline> polylines;
    private ArrayList<LatLng> listRest;

    public Group() {
    }

    public Group(String groupId, String groupName, String start_Date, String end_Date, double start_Long, double start_Lat, double end_Long, double end_Lat, String status, ArrayList<String> colorUser, ArrayList<Polyline> polylines, ArrayList<LatLng> listRest) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.start_Date = start_Date;
        this.end_Date = end_Date;
        this.start_Long = start_Long;
        this.start_Lat = start_Lat;
        this.end_Long = end_Long;
        this.end_Lat = end_Lat;
        this.status = status;
        this.colorUser = colorUser;
        this.polylines = polylines;
        this.listRest = listRest;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStart_Date() {
        return start_Date;
    }

    public void setStart_Date(String start_Date) {
        this.start_Date = start_Date;
    }

    public String getEnd_Date() {
        return end_Date;
    }

    public void setEnd_Date(String end_Date) {
        this.end_Date = end_Date;
    }

    public double getStart_Long() {
        return start_Long;
    }

    public void setStart_Long(double start_Long) {
        this.start_Long = start_Long;
    }

    public double getStart_Lat() {
        return start_Lat;
    }

    public void setStart_Lat(double start_Lat) {
        this.start_Lat = start_Lat;
    }

    public double getEnd_Long() {
        return end_Long;
    }

    public void setEnd_Long(double end_Long) {
        this.end_Long = end_Long;
    }

    public double getEnd_Lat() {
        return end_Lat;
    }

    public void setEnd_Lat(double end_Lat) {
        this.end_Lat = end_Lat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getColorUser() {
        return colorUser;
    }

    public void setColorUser(ArrayList<String> colorUser) {
        this.colorUser = colorUser;
    }

    public ArrayList<Polyline> getPolylines() {
        return polylines;
    }

    public void setPolylines(ArrayList<Polyline> polylines) {
        this.polylines = polylines;
    }

    public ArrayList<LatLng> getListRest() {
        return listRest;
    }

    public void setListRest(ArrayList<LatLng> listRest) {
        this.listRest = listRest;
    }
}
