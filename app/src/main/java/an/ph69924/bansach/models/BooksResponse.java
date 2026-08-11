package an.ph69924.bansach.models;

import java.util.List;

public class BooksResponse {
    private List<Book> books;
    private List<Book> data;
    private List<Book> products;

    public List<Book> getBooks() {
        if (books != null) return books;
        if (data != null) return data;
        return products;
    }

    public void setBooks(List<Book> books) { this.books = books; }
    public List<Book> getData() { return data; }
    public void setData(List<Book> data) { this.data = data; }
    public List<Book> getProducts() { return products; }
    public void setProducts(List<Book> products) { this.products = products; }
}
