package app.queuena;

import android.os.SystemClock;
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
import java.util.concurrent.TimeUnit;

public class Queue extends AppCompatActivity {

    private Button pollButton;
    private ImageButton sendButton;
    private EditText questionText;
    private ListView questionListView;
    private ArrayList<String> sessionGlobal;
    private ArrayAdapter<String> adapter;
    private boolean emptyFlag = true;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button submitPollButton;
    private ArrayList<String[]> questionListPlus = new ArrayList<>();
    private ArrayList<String> questionList = new ArrayList<>();
    private int answer;
    private int[] retval;

    /*private int [] getPoll() {
        String url = "http://cop4331-2.com/API/ListPolls.php";

        RequestQueue requestQueue = Volley.newRequestQueue(Queue.this);

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
                    Log.w("READ THIS!!!!!", "Got here");
                    String active;
                    String archived;
                    String error;

                    active = response.getString("active");
                    archived = response.getString("archived");
                    error = response.getString("error");

                    // split active polls
                    String[] activeSplit = active.split("\\|");
                    retval = new int[]{Integer.parseInt(activeSplit[0].trim()), Integer.parseInt(activeSplit[2].trim())};
                    //pollID = Integer.parseInt(activeSplit[0].trim());
                    //numOptions = Integer.parseInt(activeSplit[2].trim());

                    Log.w("pollID","" + retval[0]);
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


        return retval;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        ArrayList<String> sessionLocal = getIntent().getStringArrayListExtra("SESSION_INFO");
        sessionGlobal = sessionLocal;

        String url = "http://cop4331-2.com/API/ListPolls.php";

        RequestQueue requestQueue = Volley.newRequestQueue(Queue.this);

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
                    String[] activeSplit = active.split("\\| ");
                    retval = new int[]{Integer.parseInt(activeSplit[0].trim()), Integer.parseInt(activeSplit[2].trim())};
                    //pollID = Integer.parseInt(activeSplit[0].trim());
                    //numOptions = Integer.parseInt(activeSplit[2].trim());

                    Log.w("pollID","" + retval[0]);
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

        questionText = findViewById(R.id.etMessageContent);

        sendButton = findViewById(R.id.imgbtnSendMessage);
        submitPollButton = findViewById(R.id.btnPollSubmit);
        pollButton = findViewById(R.id.btnPoll);

        radioGroup = findViewById(R.id.rgChoices);

        sessionIDFix();

        // send message button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionIDFix();
                askQuestion(questionText.getText().toString());
                questionList.add(questionText.getText().toString());
                adapter.add(questionText.getText().toString());
                questionText.setText("");
            }
        });

        // check poll button
        pollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int pollID;
                int numOptions;
                int [] polls;

                // display something if no polls are present
                pollID = retval[0];
                numOptions = retval[1];


                if(numOptions == 0) {
                    Toast.makeText(Queue.this, "No polls to display", Toast.LENGTH_SHORT).show();
                }
                else {

                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Queue.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_poll, null);

                    alertBuilder.setCancelable(true);
                    alertBuilder.setView(mView);

                    submitPollButton = mView.findViewById(R.id.btnPollSubmit);

                    AlertDialog dialog = alertBuilder.create();


                    submitPollButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // session is sessionLocal.get(2)

                            RequestQueue requestQueue = Volley.newRequestQueue(Queue.this);

                            String url = "http://cop4331-2.com/API/VoteOnPoll.php";

                            JSONObject payload = new JSONObject();
                            try {
                                payload.put("session", sessionGlobal.get(2));
                                payload.put("pollID", pollID);
                                payload.put("answer", answer);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String error = response.getString("error");

                                        Log.w("ERROR", error);
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

                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    public void checkButton(View v) {
        //int radioButtonId = radioGroup.getCheckedRadioButtonId();
        boolean checked = ((RadioButton) v).isChecked();

        switch(v.getId()) {
            case R.id.rbA:
                if(checked) {
                    answer = 1;
                }
                break;
            case R.id.rbB:
                if(checked) {
                    answer = 2;
                }
                break;
            case R.id.rbC:
                if(checked) {
                    answer = 3;
                }
                break;
            case R.id.rbD:
                if(checked) {
                    answer = 4;
                }
                break;
            case R.id.rbE:
                if(checked) {
                    answer = 5;
                }
                break;
        }
    }


    private void sessionIDFix(){
        String url = "http://cop4331-2.com/API/SetSessionID.php";

        RequestQueue requestQueue = Volley.newRequestQueue(Queue.this);

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
                        Toast.makeText(Queue.this, "set SessionID failure", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Queue.this, "Please enter a question.", Toast.LENGTH_SHORT).show();
        }

        return flag;
    }

    private void askQuestion(String text) {
        if(validate(text)) {
            String url = "http://cop4331-2.com/API/AskQuestion.php";

            RequestQueue requestQueue = Volley.newRequestQueue(Queue.this);

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

    private void listQuestions(){
        int isReadToggle =1;
        String url = "http://cop4331-2.com/API/ListQuestions.php";
        RequestQueue requestQueue = Volley.newRequestQueue(Queue.this);

        JSONObject session_info = new JSONObject();

        try {
            session_info.put("session", sessionGlobal.get(2));
            session_info.put("showRead", isReadToggle);
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
                        Toast.makeText(Queue.this, "No questions found.", Toast.LENGTH_SHORT).show();
                    }
                    else if(!result.equals("") && error.equals("")) {
                        Toast.makeText(Queue.this, "Question Queue found.", Toast.LENGTH_SHORT).show();
                        questionListPlus = displayQuestions(result);

                        for(int i = 0; i < questionListPlus.size(); i++) {
                            Log.w("Question", questionListPlus.get(i)[1]);
                            questionList.add(questionListPlus.get(i)[1]);
                        }

                        populateQuestionView(questionList);
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

    private ArrayList<String[]> displayQuestions(String results){
        ArrayList<String[]> tempList = new ArrayList<>();
        String noPipes[] = results.trim().split("\\|\\|");
        Log.w("PIPEYPIPE", noPipes[1]);
        //Log.w("PIPEYPIPEV2", noPipes[3]);

        for(int i=0; i<noPipes.length; i++ ){
            String temp[];
            temp = noPipes[i].split("\\|");
            Log.w("TEMPYTEMP", temp.length + "");
            Log.w("TEMPYVALUE", temp[0]);
            tempList.add(temp);
        }

        return tempList;
    }

    private void populateQuestionView(ArrayList<String> questionList) {
        adapter = new ArrayAdapter<String>(this, R.layout.message_item_self, R.id.tvSelfMessageBody, questionList);

        ListView list = findViewById(R.id.lvQuestions);
        list.setEmptyView(findViewById(R.id.tvEmptyQuestions));
        list.setAdapter(adapter);
    }

}


