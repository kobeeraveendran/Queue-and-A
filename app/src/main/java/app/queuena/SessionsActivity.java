package app.queuena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class SessionsActivity extends AppCompatActivity {

    private ArrayList<String[]> activeSessions = new ArrayList<>();
    private ArrayList<String[]> archivedSessions = new ArrayList<>();
    private ArrayList<String> sessionGlobal;
    private ArrayAdapter<String> activeAdapter;
    private ArrayAdapter<String> archivedAdapter;
    private String session;
    private ImageButton refresh;
    private boolean emptyFlag = true;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        refresh = findViewById(R.id.imgbtnRefresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionsActivity.this.recreate();
            }
        });

        ArrayList<String> sessionLocal = getIntent().getStringArrayListExtra("SESSION_INFO");
        session = sessionLocal.get(2);
        sessionGlobal = sessionLocal;

        String url = "http://cop4331-2.com/API/GetSession.php";
        RequestQueue requestQueue = Volley.newRequestQueue(SessionsActivity.this);

        JSONObject payload = new JSONObject();

        try {
            payload.put("session", session);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String active = response.getString("active");
                    String archived = response.getString("archived");
                    String error = response.getString("error");

                    activeSessions = displayActiveSessions(active);
                    Log.w("ACTIVE SESSIONS FIELD 1", activeSessions.get(0)[0]);
                    archivedSessions = displayArchivedSessions(archived);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                populateActiveList(activeSessions);
                populateArchivedList(archivedSessions);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        requestQueue.add(jsonRequest);
    }

    private ArrayList<String[]> displayActiveSessions(String active) {
        ArrayList<String[]> tempList = new ArrayList<>();
        String activeNoPipes[] = active.trim().split("\\|");

        // split active session string into separate fields
        for( int i = 0; i < activeNoPipes.length; i++ ){
            String temp[] = activeNoPipes[i].split(":");
            tempList.add(temp);
        }

        return tempList;
    }

    private ArrayList<String[]> displayArchivedSessions(String archived) {
        ArrayList<String[]> tempList = new ArrayList<>();
        String archivedNoPipes[] = archived.trim().split("\\|");

        // split archived sessions into separate fields
        for( int i = 0; i < archivedNoPipes.length; i++ ) {
            String temp[] = archivedNoPipes[i].split(":");
            tempList.add(temp);
        }

        return tempList;
    }

    private void populateActiveList(final ArrayList<String[]> activeList) {
        ArrayList<String> sessionNameList = new ArrayList<>();
        Log.w("ACTIVE LIST SIZE", Integer.toString(activeList.get(0).length));

        if(activeList.get(0).length >= 2) {
            for( int i = 0; i < activeList.size(); i++ ) {
                sessionNameList.add(activeList.get(i)[1]);
            }
        }

        activeAdapter = new ArrayAdapter<>(this, R.layout.listrow, R.id.tvItem, sessionNameList);

        ListView activeListView = findViewById(R.id.lvActive);
        activeListView.setEmptyView(findViewById(R.id.tvEmptyActive));
        activeListView.setAdapter(activeAdapter);

        activeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*
                if(emptyFlag) {
                    isEmptyQ();
                }
                */
                // check if first question
                // create firebase child

                /*
                if(emptyFlag) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(activeSessions.get(position)[0], "");
                    root.updateChildren(map);

                    emptyFlag = false;



                    root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Set<String> set = new HashSet<String>();
                            Iterator i = dataSnapshot.getChildren().iterator();

                            while (i.hasNext()) {
                                set.add(((DataSnapshot) i.next()).getKey());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                */

                Intent goToQuestions = new Intent(SessionsActivity.this, QuestionActivity.class);
                sessionGlobal.add(activeSessions.get(position)[0]);
                goToQuestions.putExtra("SESSION_INFO", sessionGlobal);
                startActivity(goToQuestions);
            }
        });
    }

    private void populateArchivedList(ArrayList<String[]> archivedList) {
        ArrayList<String> sessionNameList = new ArrayList<>();

        if(archivedList.get(0).length >= 2) {
            for( int i = 0; i < archivedList.size(); i++ ) {
                sessionNameList.add(archivedList.get(i)[1]);
            }
        }

        archivedAdapter = new ArrayAdapter<>(this, R.layout.listrow, R.id.tvItem, sessionNameList);

        ListView archivedListVIew = findViewById(R.id.lvArchived);
        archivedListVIew.setEmptyView(findViewById(R.id.tvEmptyArchived));
        archivedListVIew.setAdapter(archivedAdapter);

        archivedListVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToQuestions = new Intent(SessionsActivity.this, QuestionActivity.class);
                // send info in intent
                startActivity(goToQuestions);
            }
        });
    }

    private void isEmptyQ(){
        // no questions

        String url = "http://cop4331-2.com/API/ListQuestions.php";
        RequestQueue requestQueue = Volley.newRequestQueue(SessionsActivity.this);

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
                        Toast.makeText(SessionsActivity.this, "No questions found.", Toast.LENGTH_SHORT).show();
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

}
