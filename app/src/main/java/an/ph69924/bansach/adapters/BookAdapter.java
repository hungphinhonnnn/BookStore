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
import an.ph69924.bansach.utils.PriceFormatter;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public interface FavoriteStateProvider {
        boolean isFavorite(Book book);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Book book);
    }

    private List<Book> books;
    private final OnBookClickListener listener;
    private final FavoriteStateProvider favoriteStateProvider;
    private final OnFavoriteClickListener favoriteClickListener;

    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        this(books, listener, null, null);
    }

    public BookAdapter(
            List<Book> books,
            OnBookClickListener listener,
            FavoriteStateProvider favoriteStateProvider,
            OnFavoriteClickListener favoriteClickListener
    ) {
        this.books = books != null ? books : new ArrayList<>();
        this.listener = listener;
        this.favoriteStateProvider = favoriteStateProvider;
        this.favoriteClickListener = favoriteClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvPrice.setText(PriceFormatter.formatVnd(book.getPrice()));
        holder.tvCategory.setText(book.getCategory() != null ? book.getCategory().getName() : "Khong phan loai");
        loadImage(holder.imgBookCover, book.getCoverImage());
        bindFavorite(holder, book);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateBooks(List<Book> books) {
        this.books = books != null ? books : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void refreshFavoriteStates() {
        notifyDataSetChanged();
    }

    private void bindFavorite(BookViewHolder holder, Book book) {
        if (favoriteStateProvider == null || favoriteClickListener == null) {
            holder.btnFavorite.setVisibility(View.GONE);
            return;
        }

        holder.btnFavorite.setVisibility(View.VISIBLE);
        boolean favorite = favoriteStateProvider.isFavorite(book);
        holder.btnFavorite.setImageResource(favorite ? R.drawable.ic_heart_filled_24 : R.drawable.ic_heart_24);
        holder.btnFavorite.setOnClickListener(v -> favoriteClickListener.onFavoriteClick(book));
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

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        ImageView btnFavorite;
        TextView tvTitle, tvAuthor, tvCategory, tvPrice;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
