package com.example.investigacion2;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConsumeAPI extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            // Create URL
            URL githubEndpoint = new URL("https://www.radiopanama.com.pa/feed.aspx?id=INICIO");

            // Create connection
            HttpsURLConnection myConnection =
                    (HttpsURLConnection) githubEndpoint.openConnection();

            if (myConnection.getResponseCode() == 200) {
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader =
                        new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                return jsonReader.toString();
            } else {
                return "";
            }
        }
        catch (IOException ioerror)
        {
            return "";
        }
    }
}
