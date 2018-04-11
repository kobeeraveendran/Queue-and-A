package app.queuena;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class CourseActivity extends AppCompatActivity {

    private String[] course_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);

        String session = getIntent().getStringExtra("PHP_SESSION");
        String[] class_list;



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

                    if(error.equals("No classes found") || result.equals("")) {
                        Toast.makeText(CourseActivity.this, "No classes found", Toast.LENGTH_SHORT).show();
                    }
                    else if(!result.equals("") && error.equals("")) {
                        Toast.makeText(CourseActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        course_list = displayClasses(result);
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

        public String [] displayClasses(String results){
            String[] noPipes = results.split("|");
            String[] finale = {};
            for(int i=0; i<noPipes.length; i++ ){
                finale.concat(noPipes[i].split(":"));
            }
            for(int i=0; i<finale.length; i++ ){
                if(i%2!=0)
                    finale[i].trim();
            }

            return finale;
        }
}
