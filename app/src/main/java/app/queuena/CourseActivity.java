package app.queuena;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    private ArrayList<String[]> courseListWithID = new ArrayList<>();
    private ArrayList<String> courseList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String session;
    private ImageButton addClass;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        String sessionLocal = getIntent().getStringExtra("PHP_SESSION");
        session = sessionLocal;

        String url = "http://cop4331-2.com/API/GetStudentClass.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(CourseActivity.this);

        JSONObject session_info = new JSONObject();

        try {
            session_info.put("session", session);
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

                    Log.w("RESULTYRES", result);

                    if(error.equals("No classes found") || result.equals("")) {
                        Toast.makeText(CourseActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    }
                    else if(!result.equals("") && error.equals("")) {
                        // Toast.makeText(CourseActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        courseListWithID = displayClasses(result);
                        index = courseListWithID.size() - 1;

                        for(int i = 0; i < courseListWithID.size(); i++) {
                            Log.w("COURSE_LIST", courseListWithID.get(i)[1]);
                            courseList.add(courseListWithID.get(i)[1]);
                        }

                        populateListView(courseList);
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

        // adding class to list
        addClass = findViewById(R.id.btnAddClass);

        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(CourseActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_addclass, null);

                alertBuilder.setCancelable(true);
                alertBuilder.setView(mView);

                final EditText classID = mView.findViewById(R.id.etAddClassID);
                Button addOption = mView.findViewById(R.id.btnAdd);
                //Button cancelOption = mView.findViewById(R.id.btnCancel);

                final AlertDialog dialog = alertBuilder.create();

                // positive button
                addOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        String newClassID = classID.getText().toString();

                        int hexToDec = Integer.parseInt(newClassID, 16);
                        newClassID = Integer.toString(hexToDec);

                        JSONObject payload = new JSONObject();
                        String url = "http://cop4331-2.com/API/JoinClass.php";

                        RequestQueue requestQueue1 = Volley.newRequestQueue(CourseActivity.this);

                        try {
                            payload.put("session", session);
                            payload.put("classID", newClassID);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String error;

                                    error = response.getString("error");

                                    if (error.equals("You have been banned from joining this class. See your professor for details.")) {
                                        Toast.makeText(CourseActivity.this, "User banned from joining class", Toast.LENGTH_SHORT).show();
                                    } else if (error.equals("Invalid student id")) {
                                        Toast.makeText(CourseActivity.this, "Invalid student ID", Toast.LENGTH_SHORT).show();
                                    } else if (error.equals("Invalid class id")) {
                                        Toast.makeText(CourseActivity.this, "Invalid class ID", Toast.LENGTH_SHORT).show();
                                    } else if (error.equals("You are already in this class.")) {
                                        Toast.makeText(CourseActivity.this, "You are already in this class", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // reload the courses listview

                                        requeryPHP();
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

                        requestQueue1.add(jsonRequest);
                    }
                });
                /*
                // negative button
                cancelOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                */
                dialog.show();
            }
        });
    }

    public ArrayList<String[]> displayClasses(String results){
        ArrayList<String[]> tempList = new ArrayList<>();
        String noPipes[] = results.trim().split("\\|");

        for(int i=0; i<noPipes.length; i++ ){
            String temp[];
            temp = noPipes[i].split(":");
            //Log.w("TEMPYTEMP", temp.length + "");
            //Log.w("TEMPYVALUE", temp[0]);
            tempList.add(temp);
        }

        return tempList;
    }

    private void populateListView(ArrayList<String> classList) {
        // list of items
        adapter = new ArrayAdapter<String>(this, R.layout.listrow, R.id.tvItem, classList);

        // configure listview
        ListView list = findViewById(R.id.lvClasses);
        list.setEmptyView(findViewById(R.id.tvEmptyClasses));
        list.setAdapter(adapter);


        // for moving to the selected class
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = "http://cop4331-2.com/API/SetClassID.php";
                RequestQueue requestQueue = Volley.newRequestQueue(CourseActivity.this);
                String classPosition = courseListWithID.get(position)[0];
                JSONObject payload = new JSONObject();

                try {
                    payload.put("session", session);
                    payload.put("classID", classPosition);
                } catch(JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String error = response.getString("error");
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

                Intent goToSessions = new Intent(CourseActivity.this, SessionsActivity.class);
                goToSessions.putExtra("PHP_SESSION", session);
                startActivity(goToSessions);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivity.this);
                builder.setMessage("Remove selected class?");
                builder.setCancelable(true);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tempID = courseListWithID.get(position)[0];

                        String url = "http://cop4331-2.com/API/LeaveClass.php";
                        RequestQueue tempRequestQueue = Volley.newRequestQueue(CourseActivity.this);

                        JSONObject payload = new JSONObject();

                        try {
                            payload.put("session", session);
                            payload.put("classID", tempID);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String error = response.getString("error");

                                    adapter.remove(courseListWithID.get(position)[1]);
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

                        tempRequestQueue.add(jsonRequest);
                    }
                });

                final AlertDialog dialog = builder.create();

                dialog.show();

                return true;
            }
        });
    }

    public void requeryPHP() {
        String url = "http://cop4331-2.com/API/GetStudentClass.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(CourseActivity.this);

        JSONObject session_info = new JSONObject();

        try {
            session_info.put("session", session);
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

                    Log.w("RESULTYRES", result);

                    if(error.equals("No classes found") || result.equals("")) {
                        Toast.makeText(CourseActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    }
                    else if(!result.equals("") && error.equals("")) {
                        courseListWithID = displayClasses(result);

                        adapter.add(courseListWithID.get(index + 1)[1]);

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