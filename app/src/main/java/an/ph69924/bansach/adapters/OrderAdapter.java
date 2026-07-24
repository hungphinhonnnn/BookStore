package an.ph69924.bansach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpolcom.example.foly.ph37745.bookstore.R;
import an.ph69924.bansach.models.Order;
import an.ph69924.bansach.utils.PriceFormatter;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private List<Order> orders;
    private final OnOrderClickListener listener;

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders != null ? orders : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        String id = order.getId() != null ? order.getId() : "";
        holder.tvOrderId.setText("Don hang #" + (id.length() > 8 ? id.substring(id.length() - 8) : id));
        holder.tvStatus.setText(order.getStatusDisplayName());
        holder.tvItemCount.setText(order.getItems() != null ? order.getItems().size() + " san pham" : "0 san pham");
        holder.tvTotalAmount.setText(PriceFormatter.formatVnd(order.getTotalAmount()));
        holder.tvCreatedAt.setText(order.getCreatedAt() != null ? order.getCreatedAt() : "");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvItemCount, tvTotalAmount, tvCreatedAt;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }
    }
}
