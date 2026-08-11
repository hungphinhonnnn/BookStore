package an.ph69924.bansach.models;

public class CoinTransaction {
    private String id;
    private String _id;
    private int amount;
    private String type;
    private String description;
    private int balanceAfter;
    private String createdAt;

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
