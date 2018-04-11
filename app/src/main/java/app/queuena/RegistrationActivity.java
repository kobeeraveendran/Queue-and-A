package app.queuena;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RegistrationActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button submit;
    private TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        confirmPassword = findViewById(R.id.etConfirmPassword);
        submit = findViewById(R.id.btnSubmit);
        signin = findViewById(R.id.tvSignin);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = name.getText().toString().trim();
                String user_email = email.getText().toString().trim();
                String user_password = password.getText().toString().trim();
                String confirm_password = confirmPassword.getText().toString().trim();

                if(!validate(user_name, user_email, user_password, confirm_password)) {
                    //Toast.makeText(RegistrationActivity.this, "Error registering", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent goToLogin = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(goToLogin);
                }
                /*
                firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            Intent goToLogin = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(goToLogin);
                        }
                        else {
                            Toast.makeText(RegistrationActivity.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                */
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLogin =  new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(goToLogin);
            }
        });
    }

    private Boolean validate(String name, String email, String password, String confirmPassword) {
        Boolean flag;
        flag = true;

        if(name.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
            flag = false;
            Toast.makeText(RegistrationActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        }
        else if(email.indexOf('@') == -1  || email.indexOf('.') == -1) {
            flag = false;
            Toast.makeText(RegistrationActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword)) {
            flag = false;
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }
}