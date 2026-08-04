package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

public class SearchActivity extends AppCompatActivity {

    private static final long DEBOUNCE_MS = 300;

    private EditText edtSearch;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> allBooks = new ArrayList<>();
    private ApiService apiService;
    private FavoriteManager favoriteManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable debounceRunnable = this::performSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        apiService = RetrofitClient.getInstance().getApiService();
        favoriteManager = new FavoriteManager(this);

        edtSearch = findViewById(R.id.edtSearch);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView = findViewById(R.id.recyclerViewSearch);

        setupRecyclerView();
        setupSearch();
        String query = getIntent().getStringExtra("query");
        if (query != null && !query.isEmpty()) {
            edtSearch.setText(query);
            edtSearch.setSelection(edtSearch.length());
        }
        performSearch();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        edtSearch.requestFocus();
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(
                allBooks,
                book -> {
                    Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
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

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(debounceRunnable);
                handler.postDelayed(debounceRunnable, DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handler.removeCallbacks(debounceRunnable);
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = edtSearch.getText().toString().trim();
        showProgress(true);
        Call<BooksResponse> call = apiService.getBooks(
                null, null, null, query.isEmpty() ? null : query
        );
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
                        showEmpty(query.isEmpty() ? "Chưa có sách nào" : "Không tìm thấy sách nào");
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    showEmpty("Không thể tải danh sách sách");
                    Toast.makeText(
                            SearchActivity.this,
                            "Lỗi: HTTP " + response.code() + " - " + response.message(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                showProgress(false);
                showEmpty("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void showEmpty(String message) {
        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
