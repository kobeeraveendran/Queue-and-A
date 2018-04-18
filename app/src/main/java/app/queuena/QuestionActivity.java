package app.queuena;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    private Button pollButton;
    private ImageButton sendButton;
    private EditText questionText;
    private ListView questionListView;
    private ArrayList<String> sessionGlobal;
    private ArrayAdapter<String> adapter;
    private boolean emptyFlag = true;

    private int numOptions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ArrayList<String> sessionLocal = getIntent().getStringArrayListExtra("SESSION_INFO");
        sessionGlobal = sessionLocal;

        questionText = findViewById(R.id.etMessageContent);

        sendButton = findViewById(R.id.imgbtnSendMessage);
        pollButton = findViewById(R.id.btnPoll);

        // send message button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // check poll button
        pollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(QuestionActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_poll, null);

                alertBuilder.setCancelable(true);
                alertBuilder.setView(mView);

                LinearLayout linearLayout = findViewById(R.id.llPoll);
                RadioGroup radioGroup = new RadioGroup(QuestionActivity.this);

                linearLayout.addView(radioGroup);

                for(int i = 0; i < numOptions; i++) {
                    RadioButton radioButtonView = new RadioButton(QuestionActivity.this);
                    if(i == 0) {
                        radioButtonView.setText("A");
                    }
                    else if(i == 1) {
                        radioButtonView.setText("B");
                    }
                    else if(i == 2) {
                        radioButtonView.setText("C");
                    }
                    else if(i == 3) {
                        radioButtonView.setText("D");
                    }
                    else if(i == 4) {
                        radioButtonView.setText("E");
                    }

                    linearLayout.addView(radioButtonView);
                }
            }
        });
    }

    private void getPoll() {
        String url = "http://cop4331-2.com/API/ListPolls.php";

        RequestQueue requestQueue = Volley.newRequestQueue(QuestionActivity.this);

        JSONObject payload = new JSONObject();

        try {
            payload.put("session", sessionGlobal.get(2));
        } catch(JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String active;
                    String archived;
                    String error;

                    active = response.getString("active");
                    archived = response.getString("archived");
                    error = response.getString("error");

                    // split active polls
                    String[] activeSplit = active.split("\\|");
                    numOptions = Integer.parseInt(activeSplit[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(jsonRequest);
    }

    private void sessionIDFix(){
        String url = "http://cop4331-2.com/API/SetSessionID.php";

        RequestQueue requestQueue = Volley.newRequestQueue(QuestionActivity.this);

        JSONObject payload = new JSONObject();


        try {
            payload.put("session", sessionGlobal.get(2));
            payload.put("sessionID", sessionGlobal.get(3));
            payload.put("sessionName", "");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String errorMsg = response.getString("error");

                    if(!errorMsg.equals("")) {
                        Toast.makeText(QuestionActivity.this, "set SessionID failure", Toast.LENGTH_SHORT).show();
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

    private Boolean validate(String question) {
        Boolean flag;
        flag = true;

        if(question.equals("")) {
            flag = false;
            Toast.makeText(QuestionActivity.this, "Please enter a question.", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    private void askQuestion(String text) {
        if(validate(text)) {
            String url = "http://cop4331-2.com/API/AskQuestion.php";

            RequestQueue requestQueue = Volley.newRequestQueue(QuestionActivity.this);

            JSONObject payload = new JSONObject();
            try {
                payload.put("session", sessionGlobal.get(2));
                payload.put("text", text);
                Log.w("TAG", payload.getString("session")+payload.getString("text"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String error;
                        error = response.getString("error");
                        Log.w("TAG", "error : "+ error );
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
}
