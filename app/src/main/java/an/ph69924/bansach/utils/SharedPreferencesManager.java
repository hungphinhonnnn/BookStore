package an.ph69924.bansach.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "bookstore_prefs";
    private final SharedPreferences prefs;

    public SharedPreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) { prefs.edit().putString("token", token).apply(); }
    public String getToken() { return prefs.getString("token", null); }
    public boolean isLoggedIn() { return getToken() != null; }

    public void saveUserInfo(String id, String username, String role, String avatar) {
        prefs.edit()
                .putString("user_id", id)
                .putString("username", username)
                .putString("role", role)
                .putString("avatar", avatar)
                .apply();
    }

    public String getUsername() { return prefs.getString("username", null); }
    public String getRole() { return prefs.getString("role", null); }
    public String getAvatar() { return prefs.getString("avatar", null); }
    public void clear() { prefs.edit().clear().apply(); }
}
