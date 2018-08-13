package lofy.fpt.edu.vn.lofy_ver108.entity;

public class UserRequest {
    private String userId;
    private String groupId;

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

    public UserRequest(String userId, String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public UserRequest() {
    }
}
