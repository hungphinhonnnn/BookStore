package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.Book;
import an.ph69924.bansach.models.CartResponse;
import an.ph69924.bansach.models.Review;
import an.ph69924.bansach.utils.CartBadgeHelper;
import an.ph69924.bansach.utils.PriceFormatter;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    private ImageView imgBookCover;
    private TextView tvTitle, tvAuthor, tvCategory, tvPrice, tvDescription;
    private TextView tvAvgRating, tvReviewCount, tvNoReviews, tvPurchasedBadge;
    private RatingBar ratingBarSummary;
    private ProgressBar progressBar;
    private Button btnAddToCart, btnWriteReview, btnReadPreview;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private String bookId;
    private Book currentBook;
    private List<Review> reviewList = new ArrayList<>();
    private Review myReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookId = getIntent().getStringExtra("book_id");
        if (bookId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getInstance().getApiService();
        prefManager = new SharedPreferencesManager(this);

        initViews();
        setupToolbar();
        setupReviewRecyclerView();
        loadBookDetail();
        loadReviews();
    }

    private void initViews() {
        imgBookCover = findViewById(R.id.imgBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        progressBar = findViewById(R.id.progressBar);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        tvAvgRating = findViewById(R.id.tvAvgRating);
        tvReviewCount = findViewById(R.id.tvReviewCount);
        ratingBarSummary = findViewById(R.id.ratingBarSummary);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        tvPurchasedBadge = findViewById(R.id.tvPurchasedBadge);
        btnReadPreview = findViewById(R.id.btnReadPreview);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);

        btnAddToCart.setOnClickListener(v -> {
            if (currentBook != null) {
                addToCart();
            }
        });

        btnWriteReview.setOnClickListener(v -> showReviewDialog(myReview));

        btnReadPreview.setOnClickListener(v -> {
            if (currentBook != null) {
                Intent intent = new Intent(BookDetailActivity.this, PreviewActivity.class);
                intent.putExtra("preview_content", currentBook.getPreview());
                intent.putExtra("book_title", currentBook.getTitle());
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupReviewRecyclerView() {
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);

        if (prefManager.isLoggedIn()) {
            reviewAdapter.setCurrentUserId(prefManager.getUserId());
        }

        reviewAdapter.setOnReviewActionListener(new ReviewAdapter.OnReviewActionListener() {
            @Override
            public void onEditReview(Review review) {
                showReviewDialog(review);
            }

            @Override
            public void onDeleteReview(Review review) {
                confirmDeleteReview(review);
            }
        });
    }

    private void loadBookDetail() {
        showProgress(true);
        Call<ApiResponse<Book>> call = apiService.getBookDetail(bookId);
        call.enqueue(new Callback<ApiResponse<Book>>() {
            @Override
            public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    Book book = response.body().getBook();
                    if (book != null) {
                        displayBook(book);
                    } else {
                        Toast.makeText(BookDetailActivity.this, "Không tìm thấy thông tin sách", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(BookDetailActivity.this, "Không thể tải thông tin sách", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadReviews() {
        Call<ApiResponse<List<Review>>> call = apiService.getReviewsByBook(bookId);
        call.enqueue(new Callback<ApiResponse<List<Review>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Review>>> call, Response<ApiResponse<List<Review>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Review> reviews = response.body().getData();
                    if (reviews != null && !reviews.isEmpty()) {
                        reviewList = reviews;
                        reviewAdapter.setReviews(reviewList);
                        tvNoReviews.setVisibility(View.GONE);
                        recyclerViewReviews.setVisibility(View.VISIBLE);
                    } else {
                        tvNoReviews.setVisibility(View.VISIBLE);
                        recyclerViewReviews.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Review>>> call, Throwable t) {
                Log.e(TAG, "Load reviews error: " + t.getMessage());
            }
        });

        if (prefManager.isLoggedIn()) {
            Call<ApiResponse<Map<String, Object>>> checkCall = apiService.checkReviewStatus(prefManager.getAuthHeader(), bookId);
            checkCall.enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                @Override
                public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> data = response.body().getData();
                        if (data != null) {
                            Boolean hasReviewed = (Boolean) data.get("hasReviewed");
                            Boolean canReview = (Boolean) data.get("canReview");

                            if (Boolean.TRUE.equals(canReview)) {
                                btnWriteReview.setVisibility(View.VISIBLE);
                                btnWriteReview.setText("Viết đánh giá");
                                myReview = null;
                            } else if (Boolean.TRUE.equals(hasReviewed)) {
                                btnWriteReview.setVisibility(View.VISIBLE);
                                btnWriteReview.setText("Sửa đánh giá");
                                Object reviewObj = data.get("review");
                                if (reviewObj instanceof Map) {
                                    Map<?, ?> rm = (Map<?, ?>) reviewObj;
                                    myReview = new Review();
                                    myReview.setId((String) rm.get("_id"));
                                    myReview.setRating(rm.get("rating") != null ? ((Number) rm.get("rating")).floatValue() : 0);
                                    myReview.setComment((String) rm.get("comment"));
                                } else {
                                    myReview = null;
                                }
                            } else {
                                btnWriteReview.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                    // ignore
                }
            });
        }
    }

    private void displayBook(Book book) {
        currentBook = book;
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvPrice.setText(PriceFormatter.formatVnd(book.getPrice()));
        tvDescription.setText(book.getDescription());

        if (book.getCategory() != null) {
            tvCategory.setText(book.getCategory().getName());
        } else {
            tvCategory.setText("Không phân loại");
        }

        if (book.getReviewCount() > 0) {
            ratingBarSummary.setRating((float) book.getAvgRating());
            tvAvgRating.setText(String.format("%.1f", book.getAvgRating()));
            tvReviewCount.setText("(" + book.getReviewCount() + " đánh giá)");
        } else {
            ratingBarSummary.setRating(0);
            tvAvgRating.setText("0.0");
            tvReviewCount.setText("(0 đánh giá)");
        }

        String imageUrl = book.getCoverImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("/")) {
                imageUrl = "http://10.0.2.2:3000" + imageUrl;
            } else if (!imageUrl.startsWith("http")) {
                imageUrl = "http://10.0.2.2:3000/uploads/" + imageUrl;
            }
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgBookCover);
        }

        if (prefManager.isLoggedIn()) {
            btnAddToCart.setVisibility(View.VISIBLE);
            checkPurchased(bookId);
        } else {
            btnAddToCart.setVisibility(View.GONE);
        }

        Log.d(TAG, "preview = [" + book.getPreview() + "]");
        btnReadPreview.setVisibility(View.VISIBLE);
    }

    private void checkPurchased(String bookId) {
        Call<ApiResponse<Map<String, Object>>> call = apiService.checkPurchased(prefManager.getAuthHeader(), bookId);
        call.enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body().getData();
                    if (data != null) {
                        Boolean purchased = (Boolean) data.get("purchased");
                        if (Boolean.TRUE.equals(purchased)) {
                            btnAddToCart.setVisibility(View.GONE);
                            tvPurchasedBadge.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                // ignore
            }
        });
    }

    private void showReviewDialog(Review existingReview) {
        boolean isEdit = existingReview != null;
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_write_review, null);
        RatingBar ratingBarInput = dialogView.findViewById(R.id.ratingBarInput);
        TextInputEditText etComment = dialogView.findViewById(R.id.etReviewComment);
        TextView tvBookTitle = dialogView.findViewById(R.id.tvReviewBookTitle);

        if (currentBook != null) {
            tvBookTitle.setText(currentBook.getTitle());
        }

        if (isEdit && existingReview != null) {
            ratingBarInput.setRating(existingReview.getRating());
            etComment.setText(existingReview.getComment());
        }

        String positiveText = isEdit ? "Cập nhật" : "Gửi đánh giá";

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton(positiveText, null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button submitBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            submitBtn.setOnClickListener(v -> {
                float rating = ratingBarInput.getRating();
                String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";

                if (rating < 1) {
                    Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (comment.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEdit) {
                    updateReview(existingReview.getId(), rating, comment, dialog);
                } else {
                    submitReview(rating, comment, dialog);
                }
            });
        });

        dialog.show();
    }

    private void submitReview(float rating, String comment, AlertDialog dialog) {
        Map<String, Object> body = new HashMap<>();
        body.put("bookId", bookId);
        body.put("rating", (int) rating);
        body.put("comment", comment);

        Call<ApiResponse<Review>> call = apiService.postReview(prefManager.getAuthHeader(), body);
        call.enqueue(new Callback<ApiResponse<Review>>() {
            @Override
            public void onResponse(Call<ApiResponse<Review>> call, Response<ApiResponse<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(BookDetailActivity.this, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadReviews();
                    loadBookDetail();
                } else {
                    String errorMsg = "Không thể gửi đánh giá";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                    Toast.makeText(BookDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReview(String reviewId, float rating, String comment, AlertDialog dialog) {
        Map<String, Object> body = new HashMap<>();
        body.put("rating", (int) rating);
        body.put("comment", comment);

        Call<ApiResponse<Review>> call = apiService.updateReview(prefManager.getAuthHeader(), reviewId, body);
        call.enqueue(new Callback<ApiResponse<Review>>() {
            @Override
            public void onResponse(Call<ApiResponse<Review>> call, Response<ApiResponse<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(BookDetailActivity.this, "Đã cập nhật đánh giá", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadReviews();
                    loadBookDetail();
                } else {
                    String errorMsg = "Không thể cập nhật đánh giá";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                    Toast.makeText(BookDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteReview(Review review) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteReview(review.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteReview(String reviewId) {
        Call<ApiResponse<Void>> call = apiService.deleteReview(prefManager.getAuthHeader(), reviewId);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookDetailActivity.this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();
                    myReview = null;
                    loadReviews();
                    loadBookDetail();
                } else {
                    String errorMsg = "Không thể xóa đánh giá";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                    Toast.makeText(BookDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart() {
        if (!prefManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAddToCart.setEnabled(false);
        Map<String, Object> body = new HashMap<>();
        body.put("bookId", currentBook.getId());
        body.put("quantity", 1);

        Call<ApiResponse<CartResponse>> call = apiService.addToCart(prefManager.getAuthHeader(), body);
        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                btnAddToCart.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(BookDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    CartBadgeHelper.updateBadge(BookDetailActivity.this);
                    return;
                }

                String errorMsg = "Không thể thêm vào giỏ hàng";
                try {
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError();
                    } else if (response.errorBody() != null) {
                        String raw = response.errorBody().string();
                        errorMsg = "HTTP " + response.code() + ": " + raw;
                    } else {
                        errorMsg = "HTTP " + response.code();
                    }
                } catch (Exception e) {
                    Log.e("BookDetailActivity", "Error reading errorBody", e);
                    errorMsg = "HTTP " + response.code();
                }

                if (response.code() == 401) {
                    Toast.makeText(BookDetailActivity.this, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.e("BookDetailActivity", "Add to cart failed: " + errorMsg);
                Toast.makeText(BookDetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                btnAddToCart.setEnabled(true);
                Log.e("BookDetailActivity", "Add to cart error: " + t.getMessage(), t);
                Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
