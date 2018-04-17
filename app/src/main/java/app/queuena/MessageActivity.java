package app.queuena;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MessageActivity extends AppCompatActivity {

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    private ImageButton sendBtn;
    private EditText questionText;
    private ListView questionListView;
    private ArrayList<String> sessionGlobal;
    private ArrayAdapter<String> adapter;
    private boolean emptyFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendBtn = findViewById(R.id.imgbtnSendMessage);
        questionText = findViewById(R.id.etMessageContent);
        questionListView = findViewById(R.id.lvQuestionList);

        ArrayList<String> sessionLocal = getIntent().getStringArrayListExtra("SESSION_INFO");
        sessionGlobal = sessionLocal;

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emptyFlag) {
                    isEmptyQ();
                }

                // check if first question
                // create firebase child
                if(emptyFlag) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(sessionGlobal.get(3), "");
                    root.updateChildren(map);

                    emptyFlag = false;

                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Set<String> set = new HashSet<String>();
                            Iterator i = dataSnapshot.getChildren().iterator();

                            while(i.hasNext()) {
                                set.add(((DataSnapshot)i.next()).getKey());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    askQuestion(questionText.getText().toString());
                }
            }
        });
    }

    private void askQuestion(String text) {
        if(validate(text)) {
            String url = "http://cop4331-2.com/API/AskQuestion.php";

            RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);

            JSONObject payload = new JSONObject();
            try {
                payload.put("session", sessionGlobal.get(2));
                payload.put("text", text);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String error;
                        error = response.getString("error");
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

    private Boolean validate(String question) {
        Boolean flag;
        flag = true;

        if(question.equals("")) {
            flag = false;
            Toast.makeText(MessageActivity.this, "Please enter a question.", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    private void isEmptyQ(){
        // no questions

        String url = "http://cop4331-2.com/API/ListQuestions.php";
        RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);

        JSONObject session_info = new JSONObject();

        try {
            session_info.put("session", sessionGlobal.get(2));
            session_info.put("showRead", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.w("SESSION INFO", session_info.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, session_info, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result;
                    String error;
                    result = response.getString("result");
                    error = response.getString("error");

                    if(!error.equals("") || result.equals("")) {
                        Toast.makeText(MessageActivity.this, "No questions found.", Toast.LENGTH_SHORT).show();
                    }
                    else if(!result.equals("") && error.equals("")) {
                        emptyFlag = false;
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

    public ArrayList<String[]> displayQuestions(String results){
        ArrayList<String[]> tempList = new ArrayList<>();
        // if crash, use \\|\\|
        String noPipes[] = results.trim().split("\\||");

        for(int i=0; i<noPipes.length; i++ ){
            String temp[];
            temp = noPipes[i].split("\\|");
            tempList.add(temp);
        }

        return tempList;
    }


}
