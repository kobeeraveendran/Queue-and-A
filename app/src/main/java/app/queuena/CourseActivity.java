package app.queuena;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);

        String session = getIntent().getStringExtra("PHP_SESSION");



        String url = "http://cop4331-2.com/API/GetStudentClass.php";
        RequestQueue requestQueue = Volley.newRequestQueue(CourseActivity.this);

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
                    String result = "";
                    String error = "";
                    result = response.getString("result");
                    error = response.getString("error");

                    Log.w("RESULTYRES", result);

                    if(error.equals("No classes found") || result.equals("")) {
                        Toast.makeText(CourseActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    }
                    else if(!result.equals("") && error.equals("")) {
                        Toast.makeText(CourseActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        courseListWithID = displayClasses(result);

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
    }

    public ArrayList<String[]> displayClasses(String results){
        ArrayList<String[]> tempList = new ArrayList<>();
        String noPipes[] = results.trim().split("\\|");
        Log.w("PIPEYPIPE", noPipes[1]);
        //Log.w("PIPEYPIPEV2", noPipes[3]);

        for(int i=0; i<noPipes.length; i++ ){
            String temp[];
            temp = noPipes[i].split(":");
            Log.w("TEMPYTEMP", temp.length + "");
            Log.w("TEMPYVALUE", temp[0]);
            tempList.add(temp);
        }

        return tempList;
    }

    private void populateListView(ArrayList<String> classList) {
        // list of items
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classList);

        // configure listview
        ListView list = (ListView) findViewById(R.id.lvClasses);
        list.setAdapter(adapter);
    }

    private void addItems(String courseName) {
        courseList.add(courseName);
        adapter.notifyDataSetChanged();
    }


}