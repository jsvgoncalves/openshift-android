package com.redhat.openshift.mobile.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.redhat.openshift.mobile.R;

import java.lang.reflect.Method;

/**
 * Created by jgoncalv on 3/10/16.
 */
public class ComService extends AsyncTask<String, String, String> {

    //public static String serverURL = "http://ticket.algumavez.com/";
    public static String serverURL = "https://api.hackathon.openshift.com/";
    //public static String serverURL = "http://192.168.102.240/tdin-webservice/";
    public static String extensionURL = ".json";
    ProgressDialog dialog;
    String methodName;
    Object object;
    boolean showProgress;

    @Override
    protected void onPreExecute(){}

    public ComService(String url, Object object, String methodName, boolean showProgress){
        this(url, object, methodName, showProgress, ((Context) object).getString(R.string.fetching_data));
    }

    /**
     * Constructor with dialog message.
     * @param url The url to access
     * @param object The activity to callback
     * @param methodName The callback method
     * @param showProgress The boolean to indicate if a progress message should be displayed
     * @param dialogMessage The dialog message string
     */
    public ComService(String url, Object object, String methodName, boolean showProgress, String dialogMessage) {
        String full_url = serverURL + url;
        this.methodName = methodName;
        this.object = object;
        this.execute(full_url, "A1jnvxl3BG6q9C1K9Y1yjommQi-X2zgSvb0H-ftFvQ8");
        this.showProgress = showProgress;


        //set message of the dialog
        if (showProgress) {
            dialog = new ProgressDialog((Context) object);
            dialog.setMessage(dialogMessage);
            dialog.setCancelable(false);
            dialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        // return ComHelper.httpGet(params);
        //Log.v("os-mobile", "doInBackground()");

        return ComHelper.getHTTP(params);
    }

    @Override
    protected void onPostExecute (String response){
//		System.out.println(result);
//		Log.e("mylog", "result " + result);
        // JSONObject json = JSONHelper.string2JSON(response);
        // String status = JSONHelper.getValue(json, "status");

        if (showProgress) {
            dialog.dismiss();
        }
        try {
            Method method = object.getClass().getMethod(this.methodName, String.class);
            method.invoke(object, response);
        } catch (Exception e) {
            Log.v("os-mobile",  e.toString());
            e.printStackTrace();
        }
    }
}