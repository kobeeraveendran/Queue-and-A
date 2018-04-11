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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
            String loginresult;

            @Override
            public void onClick(View v) {
                validate(emailET.getText().toString(), passwordET.getText().toString());
                //loginresult = POST(emailET.getText().toString(), passwordET.getText().toString());
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

    /*
    // VERSION 1 (UNTESTED)
    public static String POST (String email, String password) {
        InputStream inputStream = null;
        String result = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://cop4331-2.com/API/StudentLogin.php");

            String jsonpayload = "";

            JSONObject jsonobject = new JSONObject();
            jsonobject.put("email", email);
            jsonobject.put("password", password);

            jsonpayload = jsonobject.toString();

            StringEntity se = new StringEntity(jsonpayload);

            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }
            else {
                result = "Failed";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
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
    */
    // VERSION 2 (UNTESTED)

    public void login(String email, String password) {
        JSONObject postData = new JSONObject();
        Background b = new Background();

        try {
            postData.put("email", email);
            postData.put("password", password);

            b.execute(postData.toString());
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public class Background extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL("http://cop4331-2.com/API/StudentLogin.php");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                int response = httpURLConnection.getResponseCode();

                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes("PostData=" + params[0]);
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(is);

                int inputStreamData = isReader.read();

                while(inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = isReader.read();
                    data += current;
                }
                is.close();

            } catch(MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch(IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            finally {
                if(httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result);
        }
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
