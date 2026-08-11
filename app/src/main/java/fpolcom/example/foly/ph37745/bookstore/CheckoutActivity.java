package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.CartResponse;
import an.ph69924.bansach.models.CoinResponse;
import an.ph69924.bansach.models.Order;
import an.ph69924.bansach.models.User;
import an.ph69924.bansach.utils.PriceFormatter;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private static final int REQ_LOGIN = 1001;
    private static final int REQ_VOUCHER = 1002;
    private TextInputEditText edtFullName, edtPhone, edtAddress;
    private RadioGroup radioGroupPayment;
    private RadioButton radioCoin, radioCash;
    private TextView tvItemCount, tvSubtotal, tvShippingFee, tvTotalAmount, tvCoinBalance;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private double subtotal = 0;
    private double shippingFee = 30000;
    private double totalAmount = 0;
    private int itemCount = 0;
    private double coinBalance = 0;

    // Voucher fields
    private String selectedVoucherId = null;
    private String selectedVoucherCode = null;
    private double voucherDiscount = 0;
    private String voucherDiscountType = null;
    private double voucherMaxDiscount = 0;
    private double discountAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

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
        loadCart();
        loadUserProfile();
        setupPaymentMethod();
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
        } else if (requestCode == REQ_VOUCHER) {
            if (resultCode == RESULT_OK && data != null) {
                selectedVoucherId = data.getStringExtra("selected_voucher_id");
                selectedVoucherCode = data.getStringExtra("selected_voucher_code");
                voucherDiscount = data.getDoubleExtra("selected_voucher_discount", 0);
                voucherDiscountType = data.getStringExtra("selected_voucher_type");
                voucherMaxDiscount = data.getDoubleExtra("selected_voucher_max_discount", 0);

                calculateTotal();
                updateUI();
            }
        }
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radioCoin = findViewById(R.id.radioCoin);
        radioCash = findViewById(R.id.radioCash);
        tvItemCount = findViewById(R.id.tvItemCount);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvCoinBalance = findViewById(R.id.tvCoinBalance);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        progressBar = findViewById(R.id.progressBar);

        LinearLayout layoutSelectVoucher = findViewById(R.id.layoutSelectVoucher);
        layoutSelectVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, VoucherListActivity.class);
            intent.putExtra("selected_voucher_id", selectedVoucherId);
            intent.putExtra("subtotal", subtotal);
            startActivityForResult(intent, REQ_VOUCHER);
        });

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
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

    private void setupPaymentMethod() {
        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCoin) {
                tvCoinBalance.setVisibility(View.VISIBLE);
            } else {
                tvCoinBalance.setVisibility(View.GONE);
            }
        });
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
                        itemCount = cartResponse.getItems().size();
                        subtotal = cartResponse.getTotalAmount();
                        calculateTotal();
                        updateUI();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "Load cart failed: " + response.code());
                    Toast.makeText(CheckoutActivity.this, "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Load cart error: " + t.getMessage(), t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadUserProfile() {
        Call<CoinResponse> call = apiService.getCoins(prefManager.getAuthHeader());
        call.enqueue(new Callback<CoinResponse>() {
            @Override
            public void onResponse(Call<CoinResponse> call, Response<CoinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CoinResponse coinResponse = response.body();
                    coinBalance = coinResponse.getCoinBalance();
                    updateCoinBalanceUI();
                }
            }

            @Override
            public void onFailure(Call<CoinResponse> call, Throwable t) {
                Log.e(TAG, "Load coins error: " + t.getMessage(), t);
            }
        });
    }

    private void updateCoinBalanceUI() {
        if (radioCoin.isChecked()) {
            tvCoinBalance.setVisibility(View.VISIBLE);
        }
        tvCoinBalance.setText("Số dư Coin: " + PriceFormatter.formatVnd(coinBalance));
    }

    private void calculateTotal() {
        discountAmount = 0;
        if (selectedVoucherCode != null) {
            if ("percentage".equals(voucherDiscountType) || "percent".equals(voucherDiscountType)) {
                discountAmount = (subtotal * voucherDiscount) / 100.0;
                if (voucherMaxDiscount > 0 && discountAmount > voucherMaxDiscount) {
                    discountAmount = voucherMaxDiscount;
                }
            } else {
                discountAmount = voucherDiscount;
            }
            if (discountAmount > subtotal) {
                discountAmount = subtotal;
            }
        }
        totalAmount = subtotal - discountAmount + shippingFee;
    }

    private void updateUI() {
        tvItemCount.setText(itemCount + " sản phẩm");
        tvSubtotal.setText(PriceFormatter.formatVnd(subtotal));
        tvShippingFee.setText(PriceFormatter.formatVnd(shippingFee));
        tvTotalAmount.setText(PriceFormatter.formatVnd(totalAmount));

        TextView tvVoucherStatus = findViewById(R.id.tvVoucherStatus);
        LinearLayout layoutDiscount = findViewById(R.id.layoutDiscount);
        TextView tvDiscountAmount = findViewById(R.id.tvDiscountAmount);

        if (selectedVoucherCode != null) {
            tvVoucherStatus.setText("Đã áp dụng: " + selectedVoucherCode);
            tvVoucherStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscountAmount.setText("-" + PriceFormatter.formatVnd(discountAmount));
        } else {
            tvVoucherStatus.setText("Chọn hoặc nhập mã giảm giá");
            tvVoucherStatus.setTextColor(getResources().getColor(R.color.textSecondary));
            layoutDiscount.setVisibility(View.GONE);
        }
    }

    private void placeOrder() {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        boolean payByCoin = radioCoin.isChecked();
        String paymentMethod = payByCoin ? "coin" : "cash_on_delivery";

        // Validation
        if (fullName.isEmpty()) {
            edtFullName.setError("Vui lòng nhập họ và tên");
            edtFullName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            edtAddress.requestFocus();
            return;
        }

        if (payByCoin && coinBalance < totalAmount) {
            Toast.makeText(this, "Số dư coin không đủ. Vui lòng nạp thêm hoặc chọn COD", Toast.LENGTH_LONG).show();
            return;
        }

        showProgress(true);
        Map<String, Object> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("address", address);
        body.put("city", "");
        body.put("postalCode", "");
        body.put("phone", phone);
        body.put("paymentMethod", paymentMethod);
        body.put("notes", "");
        if (selectedVoucherCode != null) {
            body.put("discountCode", selectedVoucherCode);
        }

        Call<ApiResponse<Order>> call = apiService.createOrder(prefManager.getAuthHeader(), body);
        call.enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    Order order = apiResponse.getOrder();
                    if (order == null && apiResponse.getData() != null) {
                        order = (Order) apiResponse.getData();
                    }
                    
                    if (order != null) {
                        Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CheckoutActivity.this, OrderDetailActivity.class);
                        intent.putExtra("order_id", order.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Đặt hàng thất bại";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError();
                    }
                    Toast.makeText(CheckoutActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Place order error: " + t.getMessage(), t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPlaceOrder.setEnabled(!show);
    }
}
