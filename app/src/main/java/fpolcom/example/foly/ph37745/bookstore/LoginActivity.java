package fpolcom.example.foly.ph37745.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import an.ph69924.bansach.api.ApiService;
import an.ph69924.bansach.api.RetrofitClient;
import an.ph69924.bansach.models.ApiResponse;
import an.ph69924.bansach.models.User;
import an.ph69924.bansach.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int REQ_REGISTER = 2001;
    private EditText edtUsername, edtPassword;
    private ImageView imgTogglePassword;
    private Button btnLogin;
    private TextView tvError, tvForgotPassword, tvGoToSignUp;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferencesManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = RetrofitClient.getInstance().getApiService();
        prefManager = new SharedPreferencesManager(this);

        // Nếu đã đăng nhập, đóng lại để trở về màn hình gọi
        if (prefManager.isLoggedIn()) {
            setResult(RESULT_OK);
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        imgTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        if (tvGoToSignUp != null) {
            tvGoToSignUp.setOnClickListener(v -> {
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REQ_REGISTER);
            });
        }
        tvForgotPassword.setOnClickListener(v -> {
            // TODO: Implement forgot password
            Toast.makeText(this, "Tính năng quên mật khẩu sẽ được thêm sau", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_REGISTER && resultCode == RESULT_OK) {
            // Đăng ký thành công → truyền tiếp về màn hình gọi
            setResult(RESULT_OK);
            finish();
        }
    }

    private void togglePasswordVisibility() {
        int selection = edtPassword.getSelectionStart();
        boolean hidden = (edtPassword.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD;
        int variation = hidden ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD;
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | variation);
        edtPassword.setSelection(Math.max(0, selection));
    }

    private void performLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập");
            return;
        }

        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu");
            return;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        hideError();
        showProgress(true);

        User user = new User(username, password);
        Call<ApiResponse<User>> call = apiService.login(user);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.getToken() != null && apiResponse.getUser() != null) {
                        // Lưu token và thông tin user
                        prefManager.saveToken(apiResponse.getToken());
                        User user = apiResponse.getUser();
                        prefManager.saveUserInfo(
                                user.getId(),
                                user.getUsername(),
                                user.getRole(),
                                user.getAvatar() != null ? user.getAvatar() : ""
                        );

                        // Trả kết quả thành công về màn hình gọi
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showError("Đăng nhập thất bại. Vui lòng thử lại.");
                    }
                } else {
                    String errorMsg = "Đăng nhập thất bại";
                    if (response.body() != null && response.body().getError() != null) {
                        errorMsg = response.body().getError();
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showProgress(false);
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

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }
}
