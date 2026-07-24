package an.ph69924.bansach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fpolcom.example.foly.ph37745.bookstore.R;
import an.ph69924.bansach.models.Book;
import an.ph69924.bansach.models.OrderItem;
import an.ph69924.bansach.utils.PriceFormatter;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private List<OrderItem> orderItems;

    public OrderItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        Book book = item.getBook();
        holder.tvTitle.setText(book != null ? book.getTitle() : "");
        holder.tvAuthor.setText(book != null ? book.getAuthor() : "");
        holder.tvPrice.setText(PriceFormatter.formatVnd(item.getPrice()));
        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvSubtotal.setText(PriceFormatter.formatVnd(item.getSubtotal()));
        loadImage(holder.imgBookCover, book != null ? book.getCoverImage() : null);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void updateOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
            imageUrl = "http://10.0.2.2:3000/uploads/" + imageUrl;
        }
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView tvTitle, tvAuthor, tvPrice, tvQuantity, tvSubtotal;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
