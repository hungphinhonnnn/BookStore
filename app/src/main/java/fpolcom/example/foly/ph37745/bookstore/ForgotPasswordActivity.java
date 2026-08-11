package fpolcom.example.foly.ph37745.bookstore;

import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtEmail, edtResetToken, edtNewPassword, edtConfirmPassword;
    private ImageView imgToggleNewPassword, imgToggleConfirmPassword;
    private Button btnSendCode, btnResetPassword;
    private TextView tvError, tvMessage;
    private LinearLayout groupReset;
    private View viewDivider;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = RetrofitClient.getInstance().getApiService();

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtResetToken = findViewById(R.id.edtResetToken);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        imgToggleNewPassword = findViewById(R.id.imgToggleNewPassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvError = findViewById(R.id.tvError);
        tvMessage = findViewById(R.id.tvMessage);
        groupReset = findViewById(R.id.groupReset);
        viewDivider = findViewById(R.id.viewDivider);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnSendCode.setOnClickListener(v -> sendResetCode());
        btnResetPassword.setOnClickListener(v -> performResetPassword());
        imgToggleNewPassword.setOnClickListener(v -> togglePasswordVisibility(edtNewPassword, imgToggleNewPassword));
        imgToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(edtConfirmPassword, imgToggleConfirmPassword));
        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }

    private void togglePasswordVisibility(EditText edt, ImageView img) {
        int selection = edt.getSelectionStart();
        boolean hidden = (edt.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD;
        int variation = hidden ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD;
        edt.setInputType(InputType.TYPE_CLASS_TEXT | variation);
        edt.setSelection(Math.max(0, selection));
    }

    private void sendResetCode() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            showError("Vui lòng nhập email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Email không hợp lệ");
            return;
        }

        hideError();
        showProgress(true, btnSendCode);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        Call<ApiResponse<Void>> call = apiService.forgotPassword(body);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showProgress(false, btnSendCode);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    String serverMessage = apiResponse.getMessage();
                    String token = apiResponse.getResetToken();
                    if (token != null && !token.isEmpty()) {
                        showMessage("Mã đặt lại mật khẩu đã được tạo. Hãy nhập mã và mật khẩu mới bên dưới.");
                        edtResetToken.setText(token);
                        groupReset.setVisibility(View.VISIBLE);
                        viewDivider.setVisibility(View.VISIBLE);
                    } else {
                        showMessage(serverMessage != null
                                ? serverMessage
                                : "Nếu email tồn tại, bạn sẽ nhận được hướng dẫn đặt lại mật khẩu.");
                    }
                } else {
                    String errorMsg = "Không thể gửi yêu cầu";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showProgress(false, btnSendCode);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void performResetPassword() {
        String token = edtResetToken.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (token.isEmpty()) {
            showError("Vui lòng nhập mã đặt lại");
            return;
        }
        if (newPassword.isEmpty()) {
            showError("Vui lòng nhập mật khẩu mới");
            return;
        }
        if (newPassword.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showError("Mật khẩu xác nhận không khớp");
            return;
        }

        hideError();
        showProgress(true, btnResetPassword);

        Map<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("newPassword", newPassword);

        Call<ApiResponse<Void>> call = apiService.resetPassword(body);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showProgress(false, btnResetPassword);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ForgotPasswordActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Đặt lại mật khẩu thất bại";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showProgress(false, btnResetPassword);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void showMessage(String message) {
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }

    private void showProgress(boolean show, Button button) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        button.setEnabled(!show);
    }
}
