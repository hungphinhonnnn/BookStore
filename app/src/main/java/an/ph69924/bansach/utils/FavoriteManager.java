package an.ph69924.bansach.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoriteManager {
    private static final String PREF_NAME = "bookstore_favorites";
    private static final String KEY_BOOK_IDS = "book_ids";
    private final SharedPreferences prefs;

    public FavoriteManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFavorite(String bookId) {
        return bookId != null && getFavoriteIds().contains(bookId);
    }

    public boolean toggleFavorite(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            return false;
        }
        Set<String> ids = getFavoriteIds();
        boolean favorite;
        if (ids.contains(bookId)) {
            ids.remove(bookId);
            favorite = false;
        } else {
            ids.add(bookId);
            favorite = true;
        }
        prefs.edit().putStringSet(KEY_BOOK_IDS, ids).apply();
        return favorite;
    }

    public Set<String> getFavoriteIds() {
        return new HashSet<>(prefs.getStringSet(KEY_BOOK_IDS, new HashSet<>()));
    }
}
