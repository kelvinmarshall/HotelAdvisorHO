package dev.marshall.hoteladvisorho.api;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Marshall on 18/03/2018.
 */

public class StoreKey {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;

    private static final String SHARED_PREFER_FILE_NAME = "keys";

    /**
     * Retrieve data from preference:
     */

    public StoreKey(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(SHARED_PREFER_FILE_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Setting values in Preference:
     */


    public void createKey(String key_name) {
        editor.putString("key_name", key_name);
        editor.commit();
    }

    public String getTableName(){
        String table_name = pref.getString("key_name", null);
        return table_name;
    }
}
