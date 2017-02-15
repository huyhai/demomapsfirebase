package g8.com.android.mapsfirebasedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Wiim";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASS = "pass";
    public static final String KEY_ISLOGIN = "log";
    public static final String KEY_TINH = "tinhthanh";
    public static final String KEY_LOCATIONID = "locationid";
    public static final String KEY_LOCATIONIDNAME = "locationidname";
    public static final String KEY_TS = "ts";
    public static final String KEY_FID = "fID";
    public static final String KEY_ORDER_ID = "order_id";

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveLogina(String name, String pass, boolean islog) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, pass);
        editor.putBoolean(KEY_ISLOGIN, islog);

        editor.commit();
    }

    public HashMap<String, String> getUserLogin() {
        HashMap<String, String> pic = new HashMap<String, String>();
        pic.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        pic.put(KEY_PASS, pref.getString(KEY_PASS, ""));

        return pic;
    }

    public String getID_FACEBOOK() {
        return pref.getString(KEY_ORDER_ID, "");
    }

    public void setID_FACEBOOK(String tinh) {
        editor.putString(KEY_ORDER_ID, tinh);
        editor.commit();
    }

    public boolean isDownloadDB() {
        return pref.getBoolean(KEY_FID, false);
    }

    public void setIsDownloadDB(boolean tinh) {
        editor.putBoolean(KEY_FID, tinh);
        editor.commit();
    }

    public int getVerCode() {
        return pref.getInt(KEY_TINH, 3);
    }

    public void saveVerCode(int VerCode) {
        editor.putInt(KEY_TINH, VerCode);
        editor.commit();
    }
    // public boolean isLogin() {
    // return pref.getBoolean(KEY_IS_LOGIN, false);
    // }
    //
    // public void saveTinh(boolean islog) {
    // editor.putBoolean(KEY_IS_LOGIN, islog);
    // editor.commit();
    // }

    public String getLocationName() {
        return pref.getString(KEY_LOCATIONIDNAME, "");
    }

    public void setLocationName(String tinh) {
        editor.putString(KEY_LOCATIONIDNAME, tinh);
        editor.commit();
    }

    public float getTextSize() {
        return pref.getFloat(KEY_TS, 18);
    }

    public void setTextSize(float tinh) {
        editor.putFloat(KEY_TS, tinh);
        editor.commit();
    }

    public String getLocationID() {
        return pref.getString(KEY_LOCATIONID, "");
    }

    public void setLocationID(String tinh) {
        editor.putString(KEY_LOCATIONID, tinh);
        editor.commit();
    }

    public void savePushCart(boolean name) {
        editor.putBoolean("PCART", name);
        editor.commit();
    }

    public boolean isPushCart() {
        return pref.getBoolean("PCART", false);
    }

    public void saveisLoggedIn(boolean name) {
        editor.putBoolean(KEY_ISLOGIN, name);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_ISLOGIN, false);
    }

    public String getJsonCache(String Key) {
        return pref.getString(Key, "");
    }

    public void setJsonCache(String Key, String json) {
        editor.putString(Key, json);
        editor.commit();
    }

    public int getDown() {
        return pref.getInt("TimeStart", 0);
    }

    public void setDown(int json) {
        editor.putInt("TimeStart", json);
        editor.commit();
    }

    public Long getSVTime() {
        return pref.getLong("SVTime", 0);
    }

    public void setSVTime(long json) {
        editor.putLong("SVTime", json);
        editor.commit();
    }

    public void saveToken(boolean name) {
        editor.putBoolean("SAVETOKEN", name);
        editor.commit();
    }

    public boolean isSaveToken() {
        return pref.getBoolean("SAVETOKEN", false);
    }
}
