package app.queuena;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailET;
    private EditText passwordET;
    private TextView registerTV;
    private Button submitBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    // database info


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.etEmail);
        passwordET = findViewById(R.id.etPassword);
        registerTV = findViewById(R.id.tvRegister);
        submitBtn = findViewById(R.id.btnSubmit);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        if(user != null) {
            finish();
            Intent goToClasses = new Intent(MainActivity.this, CourseActivity.class);
            startActivity(goToClasses);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailET.getText().toString(), passwordET.getText().toString());
            }
        });

        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToReg = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(goToReg);
            }
        });
    }

    public void login(String email, String password) {
        //BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        //String type = "login";

        //backgroundWorker.execute(type, email, password);


    }

    /*
    private void validate(String userEmail, String userPassword) {

        progressDialog.setMessage("Logging in, please wait.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent goToCourses = new Intent(MainActivity.this, CourseActivity.class);
                    startActivity(goToCourses);
                }
                else {
                    Toast.makeText(MainActivity.this,"Username/password incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    */

    /*
    private void checkEmailVerification() {
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = user.isEmailVerified();

        if(emailFlag) {
            finish();
            Intent goToCourses = new Intent(MainActivity.this, CourseActivity.class);
            startActivity(goToCourses);
        }
        else {
            Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
    */
}
