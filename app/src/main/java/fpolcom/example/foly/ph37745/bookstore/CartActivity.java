package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import an.ph69924.bansach.adapters.CartAdapter;
import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.CartItem;
import an.ph69924.bansach.models.CartResponse;
import an.ph69924.bansach.utils.PriceFormatter;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private static final int REQ_LOGIN = 1001;
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView tvTotalAmount, tvEmpty;
    private Button btnCheckout;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private List<CartItem> cartItems = new ArrayList<>();
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        apiService = RetrofitClient.getInstance().getApiService();
        prefManager = new SharedPreferencesManager(this);

        // Kiểm tra đăng nhập
        if (!prefManager.isLoggedIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQ_LOGIN);
            return;
        }

        setupContent();
    }

    private void setupContent() {
        initViews();
        setupToolbar();
        setupBottomNavigation();
        setupRecyclerView();
        loadCart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_LOGIN) {
            if (resultCode == RESULT_OK) {
                setupContent();
            } else {
                finish();
            }
        }
    }

    private void initViews() {
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnCheckout = findViewById(R.id.btnCheckout);
        progressBar = findViewById(R.id.progressBar);

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CheckoutActivity.class);
            startActivity(intent);
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

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_cart);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, BookListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoriteBooksActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                updateCartItem(item, newQuantity);
            }

            @Override
            public void onItemRemoved(CartItem item) {
                removeCartItem(item);
            }
        });
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCart() {
        showProgress(true);
        Call<ApiResponse<CartResponse>> call = apiService.getCart(prefManager.getAuthHeader());
        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartResponse> apiResponse = response.body();
                    CartResponse cartResponse = apiResponse.getCart();
                    if (cartResponse == null && apiResponse.getData() != null) {
                        cartResponse = apiResponse.getData();
                    }
                    
                    if (cartResponse != null && cartResponse.getItems() != null) {
                        cartItems = cartResponse.getItems();
                        totalAmount = cartResponse.getTotalAmount();
                        updateUI();
                    } else {
                        cartItems = new ArrayList<>();
                        totalAmount = 0;
                        updateUI();
                    }
                } else {
                    Log.e(TAG, "Load cart failed: " + response.code() + " - " + response.message());
                    Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Load cart error: " + t.getMessage(), t);
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmpty(true);
            }
        });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        showProgress(true);
        Map<String, Object> body = new HashMap<>();
        // Backend expects bookId and quantity
        if (item.getBook() != null) {
            body.put("bookId", item.getBook().getId());
        } else {
            body.put("bookId", item.getId());
        }
        body.put("quantity", newQuantity);

        Call<ApiResponse<CartResponse>> call = apiService.updateCartItem(prefManager.getAuthHeader(), body);
        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartResponse> apiResponse = response.body();
                    CartResponse cartResponse = apiResponse.getCart();
                    if (cartResponse == null && apiResponse.getData() != null) {
                        cartResponse = apiResponse.getData();
                    }
                    
                    if (cartResponse != null) {
                        cartItems = cartResponse.getItems();
                        totalAmount = cartResponse.getTotalAmount();
                        updateUI();
                    }
                    Toast.makeText(CartActivity.this, "Đã cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Không thể cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                    loadCart(); // Reload cart
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadCart(); // Reload cart
            }
        });
    }

    private void removeCartItem(CartItem item) {
        showProgress(true);
        String bookId = item.getBook() != null ? item.getBook().getId() : item.getId();
        Call<ApiResponse<CartResponse>> call = apiService.removeFromCart(prefManager.getAuthHeader(), bookId);
        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, Response<ApiResponse<CartResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartResponse> apiResponse = response.body();
                    CartResponse cartResponse = apiResponse.getCart();
                    if (cartResponse == null && apiResponse.getData() != null) {
                        cartResponse = apiResponse.getData();
                    }
                    
                    if (cartResponse != null) {
                        cartItems = cartResponse.getItems();
                        totalAmount = cartResponse.getTotalAmount();
                        updateUI();
                    }
                    Toast.makeText(CartActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    loadCart(); // Reload cart
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadCart(); // Reload cart
            }
        });
    }

    private void updateUI() {
        cartAdapter.updateCartItems(cartItems);
        tvTotalAmount.setText(PriceFormatter.formatVnd(totalAmount));
        btnCheckout.setEnabled(!cartItems.isEmpty());
        showEmpty(cartItems.isEmpty());
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewCart.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewCart.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressBar != null && prefManager.isLoggedIn()) {
            loadCart(); // Reload cart when returning to this activity
        }
    }
}
