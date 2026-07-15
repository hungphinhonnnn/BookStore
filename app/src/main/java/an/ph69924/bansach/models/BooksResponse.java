package an.ph69924.bansach.models;

import java.util.List;

public class BooksResponse {
    private List<Book> books;
    private List<Book> data;

    public List<Book> getBooks() { return books != null ? books : data; }
    public void setBooks(List<Book> books) { this.books = books; }
    public List<Book> getData() { return data; }
    public void setData(List<Book> data) { this.data = data; }
}
