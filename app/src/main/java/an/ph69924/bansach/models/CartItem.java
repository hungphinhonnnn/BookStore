package an.ph69924.bansach.models;

public class CartItem {
    private String id;
    private String _id;
    private Book book;
    private int quantity;
    private double price;
    private double subtotal;

    public String getId() { return id != null ? id : _id; }
    public void setId(String id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price > 0 ? price : book != null ? book.getPrice() : 0; }
    public void setPrice(double price) { this.price = price; }
    public double getSubtotal() { return subtotal > 0 ? subtotal : getPrice() * quantity; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
