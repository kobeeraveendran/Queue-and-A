package app.queuena;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button submit;
    private TextView signin;
    AlertDialog.Builder builder;

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
        builder = new AlertDialog.Builder(RegistrationActivity.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = name.getText().toString().trim();
                String user_email = email.getText().toString().replaceAll("\\s", "");
                String user_password = password.getText().toString().trim();
                String confirm_password = confirmPassword.getText().toString().trim();

                if(!validate(user_name, user_email, user_password, confirm_password)) {
                    password.setText("");
                    confirmPassword.setText("");
                }
                else {

                    String url = "http://cop4331-2.com/API/AddStudent.php";

                    RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);

                    JSONObject payload = new JSONObject();
                    try {
                        payload.put("email", user_email);
                        payload.put("name", user_name);
                        payload.put("password", user_password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.e("Response: ", response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("Error ", error.getMessage());
                        }
                    });

                    requestQueue.add(jsonRequest);

                    Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent goToLogin = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(goToLogin);
                }
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