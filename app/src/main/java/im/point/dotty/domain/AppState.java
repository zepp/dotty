package im.point.dotty.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import im.point.dotty.R;

public final class AppState {
    private final static String IS_LOGGED_IN = "is-logged-in";
    private final static String USER_NAME = "user-name";
    private final static String TOKEN = "token";
    private final static String CSRF_TOKEN = "csrf-token";
    private volatile static AppState state;
    private final SharedPreferences preferences;
    private final Resources resources;

    private AppState(Context context) {
        this.preferences = context.getSharedPreferences(this.getClass().getSimpleName(), Context.MODE_PRIVATE);
        this.resources = context.getResources();
    }

    public static AppState getInstance(Context context) {
        if (state == null) {
            synchronized (AppState.class) {
                if (state == null) {
                    state = new AppState(context.getApplicationContext());
                }
            }
        }
        return state;
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }

    public void setIsLoggedIn(boolean value) {
        preferences.edit().putBoolean(IS_LOGGED_IN, value).apply();
    }

    public String getUserName() {
        return preferences.getString(USER_NAME, resources.getString(R.string.user_name));
    }

    public void setUserName(String value) {
        preferences.edit().putString(USER_NAME, value).apply();
    }

    public String getToken() {
        return preferences.getString(TOKEN, "");
    }

    public void setToken(String value) {
        preferences.edit().putString(TOKEN, value).apply();
    }

    public String getCsrfToken() {
        return preferences.getString(CSRF_TOKEN, "");
    }

    public void setCsrfToken(String value) {
        preferences.edit().putString(CSRF_TOKEN, value).apply();
    }
}
