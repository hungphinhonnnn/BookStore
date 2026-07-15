package an.ph69924.bansach.models;

import java.util.List;

public class OrdersResponse {
    private List<Order> orders;
    private List<Order> data;

    public List<Order> getOrders() { return orders != null ? orders : data; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    public List<Order> getData() { return data; }
    public void setData(List<Order> data) { this.data = data; }
}
