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
import an.ph69924.bansach.models.CartItem;
import an.ph69924.bansach.utils.PriceFormatter;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public interface OnCartItemClickListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    private List<CartItem> cartItems;
    private final OnCartItemClickListener listener;

    public CartAdapter(List<CartItem> cartItems, OnCartItemClickListener listener) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Book book = item.getBook();
        holder.tvTitle.setText(book != null ? book.getTitle() : "");
        holder.tvAuthor.setText(book != null ? book.getAuthor() : "");
        holder.tvPrice.setText(PriceFormatter.formatVnd(item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(PriceFormatter.formatVnd(item.getSubtotal()));
        loadImage(holder.imgBookCover, book != null ? book.getCoverImage() : null);

        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = Math.max(1, item.getQuantity() - 1);
            if (listener != null) listener.onQuantityChanged(item, quantity);
        });
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onQuantityChanged(item, item.getQuantity() + 1);
        });
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onItemRemoved(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
            imageUrl = imageUrl.startsWith("/")
                    ? "http://10.0.2.2:3000" + imageUrl
                    : "http://10.0.2.2:3000/uploads/" + imageUrl;
        }
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView tvTitle, tvAuthor, tvPrice, tvQuantity, tvSubtotal, btnDecrease, btnIncrease, btnRemove;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
