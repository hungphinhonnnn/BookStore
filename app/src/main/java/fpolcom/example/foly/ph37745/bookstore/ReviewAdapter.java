package fpolcom.example.foly.ph37745.bookstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import an.ph69924.bansach.models.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    private String currentUserId;
    private OnReviewActionListener listener;

    public interface OnReviewActionListener {
        void onEditReview(Review review);
        void onDeleteReview(Review review);
    }

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    public void setOnReviewActionListener(OnReviewActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvUserName.setText(review.getUserName());
        holder.ratingBar.setRating(review.getRating());
        holder.tvReviewComment.setText(review.getComment());

        if (review.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvReviewDate.setText(sdf.format(review.getCreatedAt()));
        }

        String avatarUrl = review.getUserAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            if (avatarUrl.startsWith("/")) {
                avatarUrl = "http://10.0.2.2:3000" + avatarUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(holder.imgUserAvatar);
        }

        boolean isMyReview = currentUserId != null
                && review.getUserId() != null
                && currentUserId.equals(review.getUserId().getId());

        if (isMyReview && listener != null) {
            holder.layoutMyReviewActions.setVisibility(View.VISIBLE);
            holder.tvMyLabel.setVisibility(View.VISIBLE);
            holder.btnEditReview.setOnClickListener(v -> listener.onEditReview(review));
            holder.btnDeleteReview.setOnClickListener(v -> listener.onDeleteReview(review));
        } else {
            holder.layoutMyReviewActions.setVisibility(View.GONE);
            holder.tvMyLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserAvatar;
        TextView tvUserName, tvReviewDate, tvReviewComment, tvMyLabel;
        RatingBar ratingBar;
        LinearLayout layoutMyReviewActions;
        TextView btnEditReview, btnDeleteReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            layoutMyReviewActions = itemView.findViewById(R.id.layoutMyReviewActions);
            btnEditReview = itemView.findViewById(R.id.btnEditReview);
            btnDeleteReview = itemView.findViewById(R.id.btnDeleteReview);
            tvMyLabel = itemView.findViewById(R.id.tvMyLabel);
        }
    }
}
