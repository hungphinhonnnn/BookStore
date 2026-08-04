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

import java.util.ArrayList;
import java.util.List;

import an.ph69924.bansach.adapters.BookAdapter;
import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.Book;
import an.ph69924.bansach.models.BooksResponse;
import an.ph69924.bansach.utils.FavoriteManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeasonalBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private BookAdapter bookAdapter;
    private List<Book> allBooks = new ArrayList<>();
    private ApiService apiService;
    private FavoriteManager favoriteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasonal_books);

        apiService = RetrofitClient.getInstance().getApiService();
        favoriteManager = new FavoriteManager(this);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        setupRecyclerView();
        loadBooks();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(
                allBooks,
                book -> {
                    Intent intent = new Intent(SeasonalBooksActivity.this, BookDetailActivity.class);
                    intent.putExtra("book_id", book.getId());
                    startActivity(intent);
                },
                book -> favoriteManager.isFavorite(book.getId()),
                book -> {
                    favoriteManager.toggleFavorite(book.getId());
                    bookAdapter.refreshFavoriteStates();
                }
        );
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(bookAdapter);
    }

    private void loadBooks() {
        showProgress(true);
        Call<BooksResponse> call = apiService.getBooks(null, null, null, null);
        call.enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    allBooks = response.body().getBooks() != null
                            ? response.body().getBooks()
                            : new ArrayList<>();
                    bookAdapter.updateBooks(allBooks);
                    if (allBooks.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    showEmpty();
                    Toast.makeText(
                            SeasonalBooksActivity.this,
                            "Không thể tải danh sách sách (HTTP " + response.code() + ")",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                showProgress(false);
                showEmpty();
                Toast.makeText(SeasonalBooksActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
