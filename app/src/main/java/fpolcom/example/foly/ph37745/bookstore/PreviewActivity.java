package fpolcom.example.foly.ph37745.bookstore;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        toolbar.setNavigationOnClickListener(v -> finish());

        String title = getIntent().getStringExtra("book_title");
        if (title != null && !title.isEmpty()) {
            getSupportActionBar().setTitle("Đọc thử: " + title);
        }

        String content = getIntent().getStringExtra("preview_content");
        TextView tvContent = findViewById(R.id.tvPreviewContent);

        if (content != null && !content.isEmpty()) {
            try {
                tvContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
            } catch (Exception e) {
                tvContent.setText(content);
            }
            tvContent.setMovementMethod(new ScrollingMovementMethod());
        } else {
            tvContent.setText("Sách này hiện chưa có nội dung đọc thử.");
        }
    }
}
