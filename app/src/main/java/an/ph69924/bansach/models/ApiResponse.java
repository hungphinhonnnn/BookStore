package an.ph69924.bansach.models;

public class ApiResponse<T> {
    private String message;
    private String error;
    private String token;
    private T data;
    private User user;
    private Book book;
    private CartResponse cart;
    private Order order;
    private OrdersResponse orders;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book != null ? book : data instanceof Book ? (Book) data : null; }
    public void setBook(Book book) { this.book = book; }
    public CartResponse getCart() { return cart != null ? cart : data instanceof CartResponse ? (CartResponse) data : null; }
    public void setCart(CartResponse cart) { this.cart = cart; }
    public Order getOrder() { return order != null ? order : data instanceof Order ? (Order) data : null; }
    public void setOrder(Order order) { this.order = order; }
    public OrdersResponse getOrders() { return orders != null ? orders : data instanceof OrdersResponse ? (OrdersResponse) data : null; }
    public void setOrders(OrdersResponse orders) { this.orders = orders; }
}
