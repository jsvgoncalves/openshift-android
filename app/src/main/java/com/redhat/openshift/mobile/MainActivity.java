package com.redhat.openshift.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redhat.openshift.mobile.tools.ComService;
import com.redhat.openshift.mobile.tools.JSONHelper;
import com.redhat.openshift.mobile.tools.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // List of Apps <app-name, appObject>
    HashMap<String, App> apps_obj = new HashMap<String, App>();
    // The representation of the apps_objs for ListView
    ArrayList<Map<String,String>> apps_maps = new ArrayList<Map<String,String>>();
    // Adapter for the ListView of the apps
    private SimpleAdapter simpleAdpt;
    // True for connected; False for disconnected
    boolean networkStatus = true;
    // If it's the first check for app connectivity
    boolean firstRun = true;


    /**
     * Broadcast receiver for connectivity status update.
     */
    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            updateNetworkStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

        Log.v("os-mobile", "onCreate()");

        new ComService(
                "/oapi/v1/namespaces/mobile/buildconfigs",
                MainActivity.this, // this context
                "buildConfigsDone", // callback
                false // show progress bar
        );

        new ComService(
                "/oapi/v1/users/~",
                MainActivity.this, // this context
                "usersDone", // callback
                false // show progress bar
        );


    }

    /**
     * pods/ callback
     * <p>
     *
     * <p>
     * @param str The JSON string
     */
    public void podsDone(String str){


        // 1. Get pods info into a JSON object
        JSONObject json = JSONHelper.string2JSON(str);
        ArrayList<String> pods = JSONHelper.getArray(json, "items");

        // Go over each pod and increment the counter for the given app
        for (String p : pods) {
            json = JSONHelper.string2JSON(p);
            try {
                String app_name = JSONHelper.getValue(json, "metadata", "labels", "app");
                // Increment counter of pods for the current app
                this.apps_obj.get(app_name).pods++;
            } catch (Exception e) {
                Log.v("os-mobile", e.toString());
            }
        }
        processListView();


    }

    /**
     * Goes through all of the apps and adds them to the list view
     * <p>
     *
     * <p>
     */
    private void processListView() {
        // Add each app in the apps_objs hashmap to the apps_maps ArrayList
        for (App app : apps_obj.values()) {
            HashMap<String, String> appMap = new HashMap<>();
            appMap.put("first-line", app.name);
            appMap.put("second-line", "Pods: " + app.pods);
            appMap.put("id", "appId");
            apps_maps.add(appMap);
        }

        // Find the ListView and pass it the adapter
        ListView lv = (ListView) findViewById(R.id.listview);
        simpleAdpt = new SimpleAdapter(this, apps_maps, android.R.layout.simple_list_item_2,
                new String[] {"first-line", "second-line"}, new int[] {android.R.id.text1,android.R.id.text2});
        lv.setAdapter(simpleAdpt);
    }

    /**
     * users/~ callback
     * <p>
     *
     * <p>
     * @param str The JSON string
     */
    public void usersDone(String str){
        JSONObject json = JSONHelper.string2JSON(str);
        String name = JSONHelper.getValue(json, "fullName");
        String mail = JSONHelper.getValue(json, "metadata", "name");
        TextView tvName = (TextView) findViewById(R.id.username);
        TextView tvMail = (TextView) findViewById(R.id.usermail);
        tvName.setText(name);
        tvMail.setText(mail);
    }

    /**
     * buildConfigs callback
     * <p>
     *
     * <p>
     * @param str The JSON string
     */
    public void buildConfigsDone(String str) {
        // Convert the string to JSON
        JSONObject json = JSONHelper.string2JSON(str);
        // Get the array with the apps
        ArrayList<String> apps = JSONHelper.getArray(json, "items");
        // Parse it
        parseBuildConfigJSON(apps);

        // Get the current Pods and update the app HashMap
        new ComService(
                "/api/v1/namespaces/mobile/pods",
                MainActivity.this, // this context
                "podsDone", // callback
                false // show progress bar
        );
    }

    /**
     * Parses the JSON array from the build config and creates the HashMap<String, App>
     * @param data The JSON string
     */
    private void parseBuildConfigJSON(ArrayList<String> data) {
        JSONObject json;
        // For each item in the ArrayList, parse a new JSON for the givne app.
        for (String d : data) {
            json = JSONHelper.string2JSON(d);
            try {
                String app_name = JSONHelper.getValue(json, "metadata", "name");
                App app = new App(app_name);
                apps_obj.put(app_name, app);
            } catch (Exception e) {
                Log.v("os-mobile", e.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * Need to unregister the network watcher
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        unregisterReceiver(networkStateReceiver);
    }

    /**
     * Shows a Toast if the Network status has changed.
     */
    public void updateNetworkStatus() {
        Context context = getApplicationContext();
        // Get the network status
        boolean newNetworkStatus = NetworkUtils.checkNetwork(context);
        CharSequence text = "Please check your connection";
        int duration = Toast.LENGTH_SHORT;
        // If it's connected now and wasn't before, show reconnected toast
        if(newNetworkStatus && !firstRun) {
            text = "Connected";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if(!firstRun) {
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        firstRun = false;
        networkStatus = newNetworkStatus;
    }


    /**
     * !TODO: Extract to Database and App Class
     */
    protected class App {
        public String name;
        public int pods = 0;
        public App(String name){
            this.name = name;
            this.pods = 0;
        }
    }

}
