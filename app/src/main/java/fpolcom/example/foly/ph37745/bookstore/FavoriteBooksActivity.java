package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import an.ph69924.bansach.adapters.BookAdapter;
import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.Book;
import an.ph69924.bansach.models.BooksResponse;
import an.ph69924.bansach.utils.FavoriteManager;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteBooksActivity extends AppCompatActivity {
    private RecyclerView recyclerViewBooks;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private BookAdapter bookAdapter;
    private ApiService apiService;
    private FavoriteManager favoriteManager;
    private SharedPreferencesManager prefManager;
    private List<Book> allBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_books);

        apiService = RetrofitClient.getInstance().getApiService();
        favoriteManager = new FavoriteManager(this);
        prefManager = new SharedPreferencesManager(this);

        if (!prefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupActions();
        setupBottomNavigation();
        loadBooks();
    }

    private void initViews() {
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(
                new ArrayList<>(),
                book -> {
                    Intent intent = new Intent(FavoriteBooksActivity.this, BookDetailActivity.class);
                    intent.putExtra("book_id", book.getId());
                    startActivity(intent);
                },
                book -> favoriteManager.isFavorite(book.getId()),
                book -> {
                    favoriteManager.toggleFavorite(book.getId());
                    renderFavorites();
                    Toast.makeText(this, "Đã bỏ khỏi yêu thích", Toast.LENGTH_SHORT).show();
                }
        );
        recyclerViewBooks.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void setupActions() {
        findViewById(R.id.btnCart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.btnProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_favorites);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, BookListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favorites) {
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

    private void loadBooks() {
        showProgress(true);
        apiService.getBooks(null, null, null, null).enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getBooks();
                    allBooks = books != null ? books : new ArrayList<>();
                    renderFavorites();
                } else {
                    Toast.makeText(FavoriteBooksActivity.this, "Không thể tải sách yêu thích", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(FavoriteBooksActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmpty(true);
            }
        });
    }

    private void renderFavorites() {
        Set<String> favoriteIds = favoriteManager.getFavoriteIds();
        List<Book> favorites = new ArrayList<>();
        for (Book book : allBooks) {
            if (favoriteIds.contains(book.getId())) {
                favorites.add(book);
            }
        }
        bookAdapter.updateBooks(favorites);
        showEmpty(favorites.isEmpty());
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBooks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBooks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookAdapter != null) {
            renderFavorites();
        }
    }
}
