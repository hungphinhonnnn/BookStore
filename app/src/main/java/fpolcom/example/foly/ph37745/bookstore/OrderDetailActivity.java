package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import an.ph69924.bansach.adapters.OrderItemAdapter;
import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.Order;
import an.ph69924.bansach.models.OrderItem;
import an.ph69924.bansach.utils.PriceFormatter;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";
    private static final int REQ_LOGIN = 1001;

    private TextView tvOrderId, tvStatus, tvCreatedAt, tvPaymentMethod;
    private TextView tvShippingAddress, tvPhone;
    private TextView tvSubtotal, tvShippingFee, tvTotalAmount;
    private TextView tvDiscountAmount;
    private LinearLayout layoutDiscount;
    private RecyclerView recyclerViewOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private ProgressBar progressBar;
    private MaterialButton btnCancelOrder;
    private CardView cardCancelSection;
    private TextView tvCancelInfo, tvCancelReason;

    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private String orderId;
    private List<OrderItem> orderItems = new ArrayList<>();
    private double shippingFee = 30000;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_order_detail);

            apiService = RetrofitClient.getInstance().getApiService();
            prefManager = new SharedPreferencesManager(this);

            orderId = getIntent() != null ? getIntent().getStringExtra("order_id") : null;

            if (orderId == null || orderId.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (!prefManager.isLoggedIn()) {
                startActivityForResult(new Intent(this, LoginActivity.class), REQ_LOGIN);
                return;
            }

            initViews();
            setupToolbar();
            setupRecyclerView();
            loadOrderDetail();

        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR", e);
            TextView tv = new TextView(this);
            tv.setPadding(32, 80, 32, 32);
            tv.setTextSize(12);
            tv.setTextColor(0xFFFF0000);
            tv.setBackgroundColor(0xFFFFFFFF);
            String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
            if (e.getCause() != null) msg += "\nCause: " + e.getCause();
            tv.setText(msg);
            setContentView(tv);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_LOGIN) {
            if (resultCode == RESULT_OK) {
                initViews();
                setupToolbar();
                setupRecyclerView();
                loadOrderDetail();
            } else {
                finish();
            }
        }
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        progressBar = findViewById(R.id.progressBar);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        cardCancelSection = findViewById(R.id.cardCancelSection);
        tvCancelInfo = findViewById(R.id.tvCancelInfo);
        tvCancelReason = findViewById(R.id.tvCancelReason);

        if (btnCancelOrder != null) {
            btnCancelOrder.setOnClickListener(v -> showCancelDialog());
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        orderItemAdapter = new OrderItemAdapter(orderItems);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrderItems.setAdapter(orderItemAdapter);
    }

    private void loadOrderDetail() {
        showProgress(true);
        String authHeader = prefManager.getAuthHeader();
        if (authHeader == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<ApiResponse<Order>> call = apiService.getOrderDetail(authHeader, orderId);
        call.enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                showProgress(false);
                if (isFinishing() || isDestroyed()) return;
                Log.d(TAG, "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body().getOrder();
                    Log.d(TAG, "Order from getOrder(): " + order);
                    if (order == null) {
                        Toast.makeText(OrderDetailActivity.this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    currentOrder = order;
                    displayOrder(order);
                } else {
                    Log.e(TAG, "API error: " + response.code() + " " + response.message());
                    Toast.makeText(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Load order detail error: " + t.getMessage(), t);
                if (!isFinishing() && !isDestroyed()) {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void displayOrder(Order order) {
        if (order.getId() != null) {
            String id = order.getId();
            String shortId = id.length() > 8 ? id.substring(id.length() - 8) : id;
            tvOrderId.setText(shortId);
        }

        if (order.getStatusDisplayName() != null) {
            tvStatus.setText(order.getStatusDisplayName());
        }
        updateStatusStyle(order.getStatus());

        if (order.getCreatedAt() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(order.getCreatedAt());
                tvCreatedAt.setText(date != null ? outputFormat.format(date) : order.getCreatedAt());
            } catch (Exception e) {
                tvCreatedAt.setText(order.getCreatedAt());
            }
        }

        if (order.getPaymentMethod() != null) {
            String pm = "coin".equals(order.getPaymentMethod()) ? "Thanh toán bằng Coin" : "Thanh toán khi nhận hàng";
            tvPaymentMethod.setText(pm);
        }

        tvShippingAddress.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "");
        tvPhone.setText(order.getPhone() != null ? order.getPhone() : "");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            orderItems = order.getItems();
            orderItemAdapter.updateOrderItems(orderItems);

            double subtotal = 0;
            for (OrderItem item : orderItems) {
                if (item != null) {
                    subtotal += item.getSubtotal();
                }
            }

            double total = order.getTotalAmount();
            double discount = order.getDiscountAmount();

            tvSubtotal.setText(PriceFormatter.formatVnd(subtotal));

            if (layoutDiscount != null && tvDiscountAmount != null) {
                if (discount > 0) {
                    layoutDiscount.setVisibility(View.VISIBLE);
                    tvDiscountAmount.setText("-" + PriceFormatter.formatVnd(discount));
                } else {
                    layoutDiscount.setVisibility(View.GONE);
                }
            }

            if (total > 0) {
                tvTotalAmount.setText(PriceFormatter.formatVnd(total));
            } else {
                tvTotalAmount.setText(PriceFormatter.formatVnd(subtotal - discount + shippingFee));
            }
        } else {
            tvSubtotal.setText(PriceFormatter.formatVnd(0));
            tvTotalAmount.setText(PriceFormatter.formatVnd(shippingFee));
        }

        tvShippingFee.setText(PriceFormatter.formatVnd(shippingFee));

        if (btnCancelOrder != null) {
            btnCancelOrder.setVisibility(order.canCancel() ? View.VISIBLE : View.GONE);
        }

        if ("cancelled".equals(order.getStatus())) {
            if (cardCancelSection != null) {
                cardCancelSection.setVisibility(View.VISIBLE);
            }
            if (tvCancelInfo != null) {
                tvCancelInfo.setText("Đơn hàng đã bị hủy");
            }
            if (tvCancelReason != null) {
                if (order.getCancelReason() != null && !order.getCancelReason().isEmpty()) {
                    tvCancelReason.setVisibility(View.VISIBLE);
                    tvCancelReason.setText("Lý do: " + order.getCancelReason());
                } else {
                    tvCancelReason.setVisibility(View.GONE);
                }
            }
        } else {
            if (cardCancelSection != null) {
                cardCancelSection.setVisibility(View.GONE);
            }
        }
    }

    private void updateStatusStyle(String status) {
        if (tvStatus == null) return;
        if ("cancelled".equals(status)) {
            tvStatus.setBackgroundResource(R.drawable.bg_status_danger);
        } else if ("delivered".equals(status)) {
            tvStatus.setBackgroundResource(R.drawable.bg_status_primary);
        } else {
            tvStatus.setBackgroundResource(R.drawable.bg_status_warning);
        }
    }

    private void showCancelDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cancel_order, null);
        TextInputEditText etReason = dialogView.findViewById(R.id.etCancelReason);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setView(dialogView)
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    String reason = etReason != null && etReason.getText() != null
                            ? etReason.getText().toString().trim() : "";
                    cancelOrder(reason);
                })
                .setNegativeButton("Giữ đơn", null)
                .show();
    }

    private void cancelOrder(String reason) {
        showProgress(true);
        String authHeader = prefManager.getAuthHeader();
        if (authHeader == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        if (!reason.isEmpty()) {
            body.put("cancelReason", reason);
        }

        Call<ApiResponse<Order>> call = apiService.cancelOrder(authHeader, orderId, body);
        call.enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                showProgress(false);
                if (isFinishing() || isDestroyed()) return;
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrderDetailActivity.this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    Order order = response.body().getOrder();
                    if (order != null) {
                        currentOrder = order;
                        displayOrder(order);
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Cancel order error: " + t.getMessage(), t);
                if (!isFinishing() && !isDestroyed()) {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
