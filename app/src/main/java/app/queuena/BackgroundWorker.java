package app.queuena;

import android.content.Context;
import android.os.AsyncTask;

public class BackgroundWorker extends AsyncTask<String, String, String> {

    Context context;

    BackgroundWorker (Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}
