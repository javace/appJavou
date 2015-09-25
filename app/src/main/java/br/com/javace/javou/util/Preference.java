package br.com.javace.javou.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Rudsonlive on 14/07/15.
 */
public class Preference {

    private Context mContext;

    public Preference(Context context) {
        this.mContext = context;
    }

    public void setString(String key, String value) {
        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(Constant.PREFERENCES_APP, 0);
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(key, value);
            edit.apply();
        }
    }

    public String getString(String key) {
        if (mContext != null) {
            SharedPreferences settings = mContext.getSharedPreferences(Constant.PREFERENCES_APP, 0);
            return settings.getString(key, null);
        }

        return null;
    }
}
