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
This activity is displayed during the initial first launch of the application, where the application
does not have an password set, this password will allow the user to login to the app and also the
same password is used to encrypt and decrypt all the notes in the application.
 */
public class CreatePasswordActivity extends AppCompatActivity {

    private EditText password_1;
    private EditText password_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        password_1 = findViewById(R.id.password_1);
        password_2 = findViewById(R.id.password_2);
        Button button_confirm = findViewById(R.id.button_confirm);

        button_confirm.setOnClickListener(v -> {
            String pass1 = password_1.getText().toString();
            String pass2 = password_2.getText().toString();

            if (pass1.equals("") || pass2.equals("")) {
                //there is no password entered
                Toast.makeText(CreatePasswordActivity.this, "No Password Entered", Toast.LENGTH_SHORT).show();
            } else {
                if (pass1.equals(pass2)) {
                    SharedPreferences settings = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("password", pass1);
                    editor.apply();

                    //enter the app
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //there is no match on the passwords
                    Toast.makeText(CreatePasswordActivity.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
