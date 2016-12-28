package com.hitvardhan.project_app.utils;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Hitvardhan on 23-12-2016.
 */

public class FetchUrl extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = getRouteLatlngURL.downloadUrl(url[0]);
            Log.d("Background Task data", data.toString());
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask parserTask = new ParserTask();

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

    }
}
