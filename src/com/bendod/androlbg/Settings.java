package com.bendod.androlbg;

import com.bendod.androlbg.utils.Utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Pair;

public final class Settings {

	private static final String KEY_PASSWORD = "password";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_SKIN = "skin";
    private static final String KEY_USE_TWITTER = "twitter";
    private static final String KEY_TWITTER_TOKEN_SECRET = "tokensecret";
    private static final String KEY_TWITTER_TOKEN_PUBLIC = "tokenpublic";
    private static final String KEY_TEMP_TOKEN_SECRET = "temp-token-secret";
    private static final String KEY_TEMP_TOKEN_PUBLIC = "temp-token-public";
    
    // twitter api keys
    private final static String keyConsumerPublic = Utils.rot13("TODO");
    private final static String keyConsumerSecret = Utils.rot13("TODO");

    private interface PrefRunnable {
        void edit(final Editor edit);
    }
    
    private static String username = null;
    private static String password = null;

    private static final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(AndrOLBGApplication.getInstance().getBaseContext());
    
    private Settings() {
        // this class is not to be instantiated;
    }
    
    public static boolean isLogin() {
        final String preUsername = sharedPrefs.getString(KEY_USERNAME, null);
        final String prePassword = sharedPrefs.getString(KEY_PASSWORD, null);

        return !Utils.isBlank(preUsername) && !Utils.isBlank(prePassword);
    }

    /**
     * Get login and password information.
     *
     * @return a pair (login, password) or null if no valid information is stored
     */
    public static Pair<String, String> getLogin() {
        if (username == null || password == null) {
            final String preUsername = sharedPrefs.getString(KEY_USERNAME, null);
            final String prePassword = sharedPrefs.getString(KEY_PASSWORD, null);

            if (preUsername == null || prePassword == null) {
                return null;
            }

            username = preUsername;
            password = prePassword;
        }
        return new Pair<String, String>(username, password);
    }

    public static String getUsername() {
        return username != null ? username : sharedPrefs.getString(KEY_USERNAME, null);
    }

    public static boolean setLogin(final String username, final String password) {
        Settings.username = username;
        Settings.password = password;
        return editSharedSettings(new PrefRunnable() {

            //@Override
            public void edit(Editor edit) {
                if(Utils.isBlank(username) || Utils.isBlank(password)){
                    // erase username and password
                    edit.remove(KEY_USERNAME);
                    edit.remove(KEY_PASSWORD);
                }else{
                    // save username and password
                    edit.putString(KEY_USERNAME, username);
                    edit.putString(KEY_PASSWORD, password);
                }
            }
        });
    }

    public static boolean isLightSkin() {
        return sharedPrefs.getBoolean(KEY_SKIN, false);
    }

    public static void setLightSkin(final boolean lightSkin) {
        editSharedSettings(new PrefRunnable() {

            @Override
            public void edit(Editor edit) {
                edit.putBoolean(KEY_SKIN, lightSkin);
            }
        });
    }

    public static String getKeyConsumerPublic() {
        return keyConsumerPublic;
    }

    public static String getKeyConsumerSecret() {
        return keyConsumerSecret;
    }

    public static boolean isUseTwitter() {
        return sharedPrefs.getBoolean(KEY_USE_TWITTER, false);
    }

    public static void setUseTwitter(final boolean useTwitter) {
        editSharedSettings(new PrefRunnable() {

            @Override
            public void edit(Editor edit) {
                edit.putBoolean(KEY_USE_TWITTER, useTwitter);
            }
        });
    }

    public static boolean isTwitterLoginValid() {
        return !Utils.isBlank(getTokenPublic()) && !Utils.isBlank(getTokenSecret());
    }

    public static String getTokenPublic() {
        return sharedPrefs.getString(KEY_TWITTER_TOKEN_PUBLIC, null);
    }

    public static String getTokenSecret() {
        return sharedPrefs.getString(KEY_TWITTER_TOKEN_SECRET, null);

    }

    public static void setTwitterTokens(final String tokenPublic, final String tokenSecret, boolean enableTwitter) {
        editSharedSettings(new PrefRunnable() {

            @Override
            public void edit(Editor edit) {
                edit.putString(KEY_TWITTER_TOKEN_PUBLIC, tokenPublic);
                edit.putString(KEY_TWITTER_TOKEN_SECRET, tokenSecret);
                if (tokenPublic != null) {
                    edit.remove(KEY_TEMP_TOKEN_PUBLIC);
                    edit.remove(KEY_TEMP_TOKEN_SECRET);
                }
            }
        });
        setUseTwitter(enableTwitter);
    }

    public static void setTwitterTempTokens(final String tokenPublic, final String tokenSecret) {
        editSharedSettings(new PrefRunnable() {
            @Override
            public void edit(Editor edit) {
                edit.putString(KEY_TEMP_TOKEN_PUBLIC, tokenPublic);
                edit.putString(KEY_TEMP_TOKEN_SECRET, tokenSecret);
            }
        });
    }

    /**
     * edit some settings without knowing how to get the settings editor or how to commit
     *
     * @param runnable
     * @return
     */
    private static boolean editSharedSettings(final PrefRunnable runnable) {
        final SharedPreferences.Editor prefsEdit = sharedPrefs.edit();
        runnable.edit(prefsEdit);
        return prefsEdit.commit();
    }

}
