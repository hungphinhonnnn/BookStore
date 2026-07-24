package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import an.ph69924.bansach.adapters.BookAdapter;
import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.Book;
import an.ph69924.bansach.models.BooksResponse;
import an.ph69924.bansach.models.CategoriesResponse;
import an.ph69924.bansach.models.Category;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListActivity extends AppCompatActivity {
    private static final String TAG = "BookListActivity";

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private EditText edtSearch;
    private LinearLayout categoryContainer;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private List<Book> allBooks = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private String selectedCategoryId = null;
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        apiService = RetrofitClient.getInstance().getApiService();
        prefManager = new SharedPreferencesManager(this);

        if (!prefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupSearch();
        setupActions();
        renderCategoryChips();
        loadCategories();
        loadBooks();
    }

    private void initViews() {
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        edtSearch = findViewById(R.id.edtSearch);
        categoryContainer = findViewById(R.id.categoryContainer);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(allBooks, book -> {
            Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
            intent.putExtra("book_id", book.getId());
            startActivity(intent);
        });
        recyclerViewBooks.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                filterBooks();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupActions() {
        findViewById(R.id.btnCart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.btnProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.btnOrders).setOnClickListener(v -> startActivity(new Intent(this, OrderHistoryActivity.class)));
        findViewById(R.id.btnExplore).setOnClickListener(v -> clearFilters());
        findViewById(R.id.btnAllCategories).setOnClickListener(v -> clearFilters());
        findViewById(R.id.btnExploreAll).setOnClickListener(v -> clearFilters());

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                clearFilters();
                return true;
            } else if (itemId == R.id.nav_category) {
                clearFilters();
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadCategories() {
        Call<CategoriesResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> loadedCategories = response.body().getCategories();
                    categories = loadedCategories != null ? loadedCategories : new ArrayList<>();
                    renderCategoryChips();
                } else {
                    Log.w(TAG, "Load categories failed: code=" + response.code() + ", msg=" + response.message());
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Log.w(TAG, "Load categories error: " + t.getMessage(), t);
            }
        });
    }

    private void renderCategoryChips() {
        if (categoryContainer == null) {
            return;
        }

        categoryContainer.removeAllViews();
        addCategoryChip("Tất cả", null, selectedCategoryId == null);

        for (Category category : categories) {
            addCategoryChip(category.getName(), category.getId(), category.getId().equals(selectedCategoryId));
        }
    }

    private void addCategoryChip(String label, String categoryId, boolean selected) {
        TextView chip = new TextView(this);
        chip.setText(label);
        chip.setGravity(Gravity.CENTER);
        chip.setTextSize(15);
        chip.setTextColor(selected ? 0xFF075BD8 : 0xFF4B5563);
        chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        chip.setBackgroundResource(selected ? R.drawable.bg_category_chip_selected : R.drawable.bg_category_chip);
        chip.setMinWidth(dp(88));
        chip.setMaxLines(2);
        chip.setOnClickListener(v -> {
            selectedCategoryId = categoryId;
            renderCategoryChips();
            filterBooks();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(64)
        );
        params.setMarginEnd(dp(12));
        categoryContainer.addView(chip, params);
    }

    private void loadBooks() {
        showProgress(true);
        Call<BooksResponse> call = apiService.getBooks(null, null, null, null);
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getBooks();
                    allBooks = books != null ? books : new ArrayList<>();
                    filterBooks();
                } else {
                    showEmpty(true);
                    String msg = "Không thể tải danh sách sách (HTTP " + response.code() + " - " + response.message() + ")";
                    Log.e(TAG, msg);
                    Toast.makeText(BookListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                showProgress(false);
                showEmpty(true);
                Log.e(TAG, "Lỗi kết nối khi tải sách: " + t.getMessage(), t);
                Toast.makeText(BookListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBooks() {
        List<Book> filteredBooks = new ArrayList<>();
        String normalizedQuery = searchQuery.toLowerCase();

        for (Book book : allBooks) {
            boolean matchesSearch = normalizedQuery.isEmpty()
                    || safeLower(book.getTitle()).contains(normalizedQuery)
                    || safeLower(book.getAuthor()).contains(normalizedQuery);
            boolean matchesCategory = selectedCategoryId == null
                    || (book.getCategory() != null && selectedCategoryId.equals(book.getCategory().getId()));

            if (matchesSearch && matchesCategory) {
                filteredBooks.add(book);
            }
        }

        bookAdapter.updateBooks(filteredBooks);
        showEmpty(filteredBooks.isEmpty());
    }

    private void clearFilters() {
        selectedCategoryId = null;
        searchQuery = "";
        edtSearch.setText("");
        renderCategoryChips();
        filterBooks();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBooks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBooks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
