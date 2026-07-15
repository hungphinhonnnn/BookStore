package an.ph69924.bansach.models;

public class Category {
    private String id;
    private String _id;
    private String name;

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }
}
