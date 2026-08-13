package an.ph69924.bansach.models;

import java.util.Date;

public class Review {
    private String id;
    private String _id;
    private User userId; // Đổi từ String sang User để khớp với populate("userId") từ backend
    private String bookId;
    private float rating;
    private String comment;
    private Date createdAt;

    public Review() {}

    public Review(User userId, String bookId, float rating, String comment) {
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    
    public User getUserId() { return userId; }
    public void setUserId(User userId) { this.userId = userId; }

    public String getUserName() {
        if (userId == null) return "Người dùng";
        String name = "";
        if (userId.getFirstName() != null) name += userId.getFirstName();
        if (userId.getLastName() != null) name += " " + userId.getLastName();
        return name.isEmpty() ? userId.getUsername() : name.trim();
    }

    public String getUserAvatar() {
        return userId != null ? userId.getAvatar() : null;
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
