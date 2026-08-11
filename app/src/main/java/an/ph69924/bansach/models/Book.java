package an.ph69924.bansach.models;

import com.google.gson.annotations.SerializedName;

public class Book {
    private String id;
    private String _id;
    private String title;
    private String author;
    private String description;
    private String coverImage;
    private String image;
    private double price;
    private int stock;
    private Category category;
    
    @SerializedName("preview")
    private String preview; // Khớp với trường 'preview' trong schema Backend

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author != null ? author : ""; }
    public void setAuthor(String author) { this.author = author; }
    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverImage() { return coverImage != null ? coverImage : image; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }
}
