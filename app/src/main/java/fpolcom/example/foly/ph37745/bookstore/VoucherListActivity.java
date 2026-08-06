package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import an.ph69924.bansach.adapters.VoucherAdapter;
import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.Voucher;
import an.ph69924.bansach.models.VouchersResponse;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherListActivity extends AppCompatActivity {
    private static final String TAG = "VoucherListActivity";
    private RecyclerView recyclerViewVouchers;
    private VoucherAdapter voucherAdapter;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;
    private List<Voucher> vouchers = new ArrayList<>();
    private String selectedVoucherId;
    private double subtotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);

        selectedVoucherId = getIntent().getStringExtra("selected_voucher_id");
        subtotal = getIntent().getDoubleExtra("subtotal", 0);

        apiService = RetrofitClient.getInstance().getApiService();
        prefManager = new SharedPreferencesManager(this);

        if (!prefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadVouchers();
    }

    private void initViews() {
        recyclerViewVouchers = findViewById(R.id.recyclerViewVouchers);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);
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

    private void setupRecyclerView() {
        voucherAdapter = new VoucherAdapter(vouchers, voucher -> {
            Intent intent = new Intent();
            intent.putExtra("selected_voucher_id", voucher.getId());
            intent.putExtra("selected_voucher_code", voucher.getCode());
            intent.putExtra("selected_voucher_discount", voucher.getDiscount());
            intent.putExtra("selected_voucher_type", voucher.getDiscountType());
            intent.putExtra("selected_voucher_max_discount", voucher.getMaxDiscount());
            setResult(RESULT_OK, intent);
            finish();
        });
        recyclerViewVouchers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVouchers.setAdapter(voucherAdapter);
    }

    private void loadVouchers() {
        showProgress(true);
        Call<ApiResponse<VouchersResponse>> call = apiService.getVouchers();
        call.enqueue(new Callback<ApiResponse<VouchersResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<VouchersResponse>> call, Response<ApiResponse<VouchersResponse>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<VouchersResponse> apiResponse = response.body();
                    VouchersResponse vouchersResponse = apiResponse.getData();
                    if (vouchersResponse != null && vouchersResponse.getVouchers() != null) {
                        vouchers = vouchersResponse.getVouchers();
                        for (Voucher v : vouchers) {
                            if (v.getId().equals(selectedVoucherId)) {
                                v.setSelected(true);
                            }
                            if (subtotal > 0 && v.getMinOrderValue() > subtotal) {
                                v.setStatus("invalid");
                            }
                        }
                        updateUI();
                    } else {
                        vouchers = new ArrayList<>();
                        updateUI();
                    }
                } else {
                    Log.e(TAG, "Load vouchers failed: " + response.code());
                    Toast.makeText(VoucherListActivity.this, "Khong the tai danh sach voucher", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<VouchersResponse>> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Load vouchers error: " + t.getMessage(), t);
                Toast.makeText(VoucherListActivity.this, "Loi ket noi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmpty(true);
            }
        });
    }

    private void updateUI() {
        voucherAdapter.updateVouchers(vouchers);
        showEmpty(vouchers.isEmpty());
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewVouchers.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewVouchers.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
