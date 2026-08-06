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
import an.ph69924.bansach.models.Voucher;
import an.ph69924.bansach.utils.PriceFormatter;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    public interface OnVoucherClickListener {
        void onVoucherClick(Voucher voucher);
    }

    private List<Voucher> vouchers;
    private final OnVoucherClickListener listener;

    public VoucherAdapter(List<Voucher> vouchers, OnVoucherClickListener listener) {
        this.vouchers = vouchers != null ? vouchers : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        holder.tvCode.setText(voucher.getCode());
        holder.tvDescription.setText(voucher.getDescription() != null ? voucher.getDescription() : "");

        if ("percentage".equals(voucher.getDiscountType()) || "percent".equals(voucher.getDiscountType())) {
            holder.tvDiscount.setText("Giam " + (int) voucher.getDiscount() + "%");
        } else {
            holder.tvDiscount.setText("Giam " + PriceFormatter.formatVnd(voucher.getDiscount()));
        }

        if (voucher.getMinOrderValue() > 0) {
            holder.tvMinOrder.setText("Don toi thieu: " + PriceFormatter.formatVnd(voucher.getMinOrderValue()));
        } else {
            holder.tvMinOrder.setText("");
        }

        if (voucher.getMaxDiscount() > 0) {
            holder.tvMaxDiscount.setText("Toi da: " + PriceFormatter.formatVnd(voucher.getMaxDiscount()));
        } else {
            holder.tvMaxDiscount.setText("");
        }

        boolean isInvalid = "invalid".equals(voucher.getStatus());
        if (isInvalid) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        holder.itemView.setBackgroundResource(
            voucher.isSelected() ? R.drawable.bg_voucher_selected : R.drawable.bg_voucher
        );

        holder.itemView.setOnClickListener(v -> {
            if (isInvalid) return;
            if (listener != null) listener.onVoucherClick(voucher);
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    public void updateVouchers(List<Voucher> vouchers) {
        this.vouchers = vouchers != null ? vouchers : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvDiscount, tvDescription, tvMinOrder, tvMaxDiscount;

        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvVoucherCode);
            tvDiscount = itemView.findViewById(R.id.tvVoucherDiscount);
            tvDescription = itemView.findViewById(R.id.tvVoucherDescription);
            tvMinOrder = itemView.findViewById(R.id.tvVoucherMinOrder);
            tvMaxDiscount = itemView.findViewById(R.id.tvVoucherMaxDiscount);
        }
    }
}
