package app.queuena;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText emailET;
    private EditText passwordET;
    private TextView registerTV;
    private Button submitBtn;
    private ProgressDialog progressDialog;

    // database info


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailET = findViewById(R.id.etEmail);
        passwordET = findViewById(R.id.etPassword);
        registerTV = findViewById(R.id.tvRegister);
        submitBtn = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
/*
        if(user != null) {
            finish();
            Intent goToClasses = new Intent(MainActivity.this, CourseActivity.class);
            startActivity(goToClasses);
        }
*/
        submitBtn.setOnClickListener(new View.OnClickListener() {


            String session = "";
            String errorMsg = "";

            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if(!validate(email, password)) {
                    passwordET.setText("");
                }
                else {
                    String url = "http://cop4331-2.com/API/StudentLogin.php";

                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                    JSONObject payload = new JSONObject();

                    try {
                        payload.put("email", email);
                        payload.put("password", password);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                    Log.w("JSON PAYLOAD", payload.toString());

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                session = response.getString("session");
                                Log.w("SESSION", session);
                                errorMsg = response.getString("error");
                                Log.w("ERROR", errorMsg);

                                if(errorMsg.equals("Could not find account") || session.equals("")) {
                                    Toast.makeText(MainActivity.this, "Username/Password incorrect", Toast.LENGTH_SHORT).show();
                                    passwordET.setText("");
                                }
                                else if(!session.equals("") && errorMsg.equals("")) {
                                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent goToCourses = new Intent(MainActivity.this, CourseActivity.class);
                                    goToCourses.putExtra("PHP_SESSION", session);
                                    startActivity(goToCourses);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.e("Error ", error.getMessage());
                        }
                    });

                    requestQueue.add(jsonRequest);
                }
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

    public Boolean validate(String email, String password) {
        Boolean flag;
        flag = true;

        if (email.equals("") || password.equals("")) {
            flag = false;
            Toast.makeText(MainActivity.this, "Fill out all fields", Toast.LENGTH_SHORT).show();
        }
        else if(email.indexOf('@') == -1) {
            flag = false;
            Toast.makeText(MainActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }



    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        inputStream.close();
        return result;
    }
}
