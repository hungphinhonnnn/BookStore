package an.ph69924.bansach.models;

import java.util.List;

public class CategoriesResponse {
    private List<Category> categories;
    private List<Category> data;

    public List<Category> getCategories() { return categories != null ? categories : data; }
    public void setCategories(List<Category> categories) { this.categories = categories; }
    public List<Category> getData() { return data; }
    public void setData(List<Category> data) { this.data = data; }
}
