package usernameharshakota.harshakotanotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import usernameharshakota.harshakotanotes.password.CreatePasswordActivity;
import usernameharshakota.harshakotanotes.password.EnterPasswordActivity;

/*
This activity is not loaded when is the first activity that starts up when the application is
launched. This activity performs a check to see if a password has been set for the application or
not. Depending on the result from checking the SharedPreferences for a password,
CreatePasswordActivity or EnterPasswordActivity is called.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //load the password
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        String password = settings.getString("password", "");

        if (password.equals("")) {
            //if there is no password
            Intent intent = new Intent(SplashActivity.this, CreatePasswordActivity.class);
            startActivity(intent);
            finish();
        } else {
            //if there is a password
            Intent intent = new Intent(SplashActivity.this, EnterPasswordActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
