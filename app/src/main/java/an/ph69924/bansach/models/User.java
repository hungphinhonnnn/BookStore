package an.ph69924.bansach.models;

public class User {
    private String id;
    private String _id;
    private String username;
    private String password;
    private String role;
    private String avatar;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
