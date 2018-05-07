package usernameharshakota.harshakotanotes.password;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import usernameharshakota.harshakotanotes.activities.MainActivity;
import usernameharshakota.harshakotanotes.R;

/*
This activity is displayed when the application has a password already set and authenticates the user
using the password provided with the password in the SharedPreferences.
 */
public class EnterPasswordActivity extends AppCompatActivity {

    private EditText xml_password;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        //load the password
        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        password = settings.getString("password", "");

        xml_password = findViewById(R.id.enter_password);
        Button xml_enterButton = findViewById(R.id.enter_button);

        xml_enterButton.setOnClickListener(v -> {
            String text = xml_password.getText().toString();

            if (text.equals(password)) {
                //enter the app
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else if (text.isEmpty()) {
                Toast.makeText(EnterPasswordActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(EnterPasswordActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
