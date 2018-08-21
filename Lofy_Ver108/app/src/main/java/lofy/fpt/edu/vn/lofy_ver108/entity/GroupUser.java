package lofy.fpt.edu.vn.lofy_ver108.entity;

public class GroupUser {
    private String groupsUsersID;
    private String userId;
    private String groupId;
    private boolean isHost;
    private boolean isVice;
    private String timeStamp;
    private double sizeRadius;
    private boolean statusUser;

    public GroupUser() {
    }

    public String getGroupsUsersID() {
        return groupsUsersID;
    }

    public void setGroupsUsersID(String groupsUsersID) {
        this.groupsUsersID = groupsUsersID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public boolean isVice() {
        return isVice;
    }

    public void setVice(boolean vice) {
        isVice = vice;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getSizeRadius() {
        return sizeRadius;
    }

    public void setSizeRadius(double sizeRadius) {
        this.sizeRadius = sizeRadius;
    }

    public boolean isStatusUser() {
        return statusUser;
    }

    public void setStatusUser(boolean statusUser) {
        this.statusUser = statusUser;
    }

    public GroupUser(String groupsUsersID, String userId, String groupId, boolean isHost, boolean isVice, String timeStamp, double sizeRadius, boolean statusUser) {
        this.groupsUsersID = groupsUsersID;
        this.userId = userId;
        this.groupId = groupId;
        this.isHost = isHost;
        this.isVice = isVice;
        this.timeStamp = timeStamp;
        this.sizeRadius = sizeRadius;
        this.statusUser = statusUser;
    }
}