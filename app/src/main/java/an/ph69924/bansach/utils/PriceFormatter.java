package an.ph69924.bansach.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class PriceFormatter {
    private static final NumberFormat VND_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    static {
        VND_FORMATTER.setCurrency(Currency.getInstance("VND"));
        VND_FORMATTER.setMaximumFractionDigits(0);
    }

    private PriceFormatter() {
    }

    public static String formatVnd(double amount) {
        return VND_FORMATTER.format(amount);
    }
}
