package fpolcom.example.foly.ph37745.bookstore;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class PreviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Toolbar toolbar = findViewById(R.id.toolbarPreview);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String content = getIntent().getStringExtra("preview_content");
        TextView tvContent = findViewById(R.id.tvPreviewContent);

        if (content != null && !content.isEmpty()) {
            // Kiểm tra nếu nội dung là một liên kết (http/https)
            if (content.startsWith("http")) {
                tvContent.setText("Đang mở liên kết: " + content);
                // Bạn có thể dùng WebView ở đây nếu muốn, nhưng đơn giản nhất là hiển thị text hoặc mở Browser
            } else {
                tvContent.setText(content);
            }
        } else {
            tvContent.setText("Không có nội dung đọc thử.");
        }
    }
}
