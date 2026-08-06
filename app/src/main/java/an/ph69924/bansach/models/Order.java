package an.ph69924.bansach.models;

import java.util.List;

public class Order {
    private String id;
    private String _id;
    private String status;
    private String createdAt;
    private String paymentMethod;
    private String shippingAddress;
    private String address;
    private String phone;
    private double totalAmount;
    private List<OrderItem> items;
    private double discountAmount;
    private String discountCode;

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusDisplayName() {
        if ("pending".equals(status)) return "Cho xac nhan";
        if ("confirmed".equals(status)) return "Da xac nhan";
        if ("shipping".equals(status)) return "Dang giao";
        if ("delivered".equals(status)) return "Da giao";
        if ("cancelled".equals(status)) return "Da huy";
        return status != null ? status : "Cho xac nhan";
    }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getShippingAddress() { return shippingAddress != null ? shippingAddress : address; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
}
