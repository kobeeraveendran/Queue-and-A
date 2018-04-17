package app.queuena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
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

    private ImageButton sendBtn;
    private EditText questionText;
    private ListView questionListView;
    private TextView chatElement;
    private ArrayList<String> sessionGlobal;
    private ArrayAdapter<String> adapter;
    private ScrollView scrollView;
    private String tempKey;
    private boolean emptyFlag = true;

    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendBtn = findViewById(R.id.imgbtnSendMessage);
        questionText = findViewById(R.id.etMessageContent);
        chatElement = findViewById(R.id.tvMessage);
        scrollView = findViewById(R.id.svQuestions);

        setTitle("Question Queue");

        //questionListView = findViewById(R.id.lvQuestionList);

        ArrayList<String> sessionLocal = getIntent().getStringArrayListExtra("SESSION_INFO");
        sessionGlobal = sessionLocal;

        root = FirebaseDatabase.getInstance().getReference().child(sessionLocal.get(3));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                tempKey = root.push().getKey();

                root.updateChildren(map);

                DatabaseReference message_root = root.child(tempKey);
                Map<String, Object>  map2 = new HashMap<>();
                map2.put("msg", questionText.getText().toString());

                message_root.updateChildren(map2);

                sessionIDFix();
                askQuestion(questionText.getText().toString());
                questionText.setText("");
            }
        });


        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String chat_msg;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()) {
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chatElement.append(chat_msg + "\n");
        }
    }

    private void sessionIDFix(){
        String url = "http://cop4331-2.com/API/SetSessionID.php";

        RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);

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
                        Toast.makeText(MessageActivity.this, "set SessionID failure", Toast.LENGTH_SHORT).show();
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

    private void askQuestion(String text) {
        if(validate(text)) {
            String url = "http://cop4331-2.com/API/AskQuestion.php";

            RequestQueue requestQueue = Volley.newRequestQueue(MessageActivity.this);

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

    private Boolean validate(String question) {
        Boolean flag;
        flag = true;

        if(question.equals("")) {
            flag = false;
            Toast.makeText(MessageActivity.this, "Please enter a question.", Toast.LENGTH_SHORT).show();
        }

        return flag;
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
