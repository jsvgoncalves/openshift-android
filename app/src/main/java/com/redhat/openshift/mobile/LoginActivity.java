package com.redhat.openshift.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.redhat.openshift.mobile.model.OpenshiftMobileClient;

public class LoginActivity extends AppCompatActivity {

    OpenshiftMobileClient osmobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        osmobile = (OpenshiftMobileClient) getApplication();

        // Check for proper sharePreferences
        if(!osmobile.hasLoadedPrefs() || osmobile.isLoggedOut()) {
            // show login screen
            setContentView(R.layout.activity_login);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Change servers", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
//        } // If the user has saved prefs then check for expirationDate
//        else if(hasLoginExpired()){
//            // If it has, then try to login
//            doLogin();
        } else {
            // If everything is cool, then just move on to the other activity
            startHome();
        }

    }

    /**
     * Login button action handler
     */
    public void loginAction(View v){
        // Disable both Login and Create account buttons
        disableForm();

        // Get the form values
        String email = ((EditText) findViewById(R.id.form_email)).getText().toString();
        String pw = ((EditText) findViewById(R.id.form_pw)).getText().toString();

        // Check if the form is not empty
        if (!email.equals("") && !pw.equals("")) {
            //bus.setRealEmail(email);
            //bus.setEmail(md5Helper.hashMD5(email));
            //bus.setPw(md5Helper.hashMD5(pw));

            // Try to login
            doLogin();
        } else {
            enableForm();
        }
    }

    private void doLogin() {
        startHome();
    }

    public void disableForm(){
        Button blogin = (Button) findViewById(R.id.button_login);
        Button bregister = (Button) findViewById(R.id.button_register);
        blogin.setEnabled(false);
        bregister.setEnabled(false);
    }

    public void enableForm() {
        Button blogin = (Button) findViewById(R.id.button_login);
        Button bregister = (Button) findViewById(R.id.button_register);
        blogin.setEnabled(false);
        bregister.setEnabled(false);
    }

    /**
     * Launches the home activity and finishes this.
     */
    private void startHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME); // Clears the Main Activity
        startActivity(intent);
        finish();
    }

}
