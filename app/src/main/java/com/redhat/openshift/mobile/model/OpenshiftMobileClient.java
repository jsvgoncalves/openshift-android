package com.redhat.openshift.mobile.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.redhat.openshift.mobile.R;

/**
 * Created by jgoncalv on 3/14/16.
 */
public class OpenshiftMobileClient extends Application {
    private static OpenshiftMobileClient instance;
    private static boolean hasLogin = false;

    private String userID;
    private String token;
    private String name;
    private String email;

    private boolean loggedOut = true;
    private boolean loadedPrefs = false;
    private String lastUpdate;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Retrieve basic user (persistent) data
        // True if loaded, False otherwise
        this.loadedPrefs = checkSharedPrefs();
    }

    /**
     * Loads the sharedPreferences and returns if it was successful
     * http://developer.android.com/guide/topics/data/data-storage.html#pref
     * @return boolean - whether or not the sharedprefs have been loaded
     */
    private boolean checkSharedPrefs() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        String userid = settings.getString("userid", "notset");
        String name = settings.getString("name", "notset");
        String email = settings.getString("email", "notset");
        String realEmail = settings.getString("realEmail", "notset");
        String pw = settings.getString("pw", "notset");
        String token = settings.getString("token", "notset");
        String lastUpdate = settings.getString("lastUpdate", "notset");
        String date = settings.getString("expirationDate", "1999-12-12 00:00:00");
        boolean loggedOut = settings.getBoolean("loggedOut", true);
//		date = "1999-12-12 00:00:01";

        try {
            if( name.equals("notset") || email.equals("notset") || pw.equals("notset") ||
                    userid.equals("notset") || token.equals("notset") || date.equals("1999-12-12 00:00:00") ) {
                throw new ParseException("Parse exception", 0);
            }
            Date sharedDate = new SimpleDateFormat(getString(R.string.time_format), Locale.ENGLISH).parse(date);

            setName(name);
            setEmail(email);
            //setRealEmail(realEmail);
            //setUser_id(userid);
            //setPw(pw);
            //setExpirationDate(sharedDate);
            setToken(token);
            //setLoggedOut(loggedOut);
            this.lastUpdate = lastUpdate;

            // Preferences loaded
            return true;
        } catch (ParseException e) {
            // Preferences not
            return false;
        }
    }

    /**
     * Saves the shared preferences
     */
    public void saveSharedPrefs() {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("userid", userID);
        editor.putString("name", name);
        editor.putString("email", email);
        //editor.putString("realEmail", realEmail);
        //editor.putString("pw", pw);
        editor.putString("token", token);
        editor.putBoolean("loggedOut", loggedOut);
        SimpleDateFormat dFormat = new SimpleDateFormat(getString(R.string.time_format), Locale.getDefault());

        // Commit the edits!
        editor.commit();
    }

    public static Context context() {
        return instance.getApplicationContext();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static boolean isHasLogin() {
        return hasLogin;
    }

    public static void setHasLogin(boolean hasLogin) {
        OpenshiftMobileClient.hasLogin = hasLogin;
    }

    public boolean hasLoadedPrefs() {
        return loadedPrefs;
    }

    public boolean isLoggedOut() {
        return loggedOut;
    }

}
