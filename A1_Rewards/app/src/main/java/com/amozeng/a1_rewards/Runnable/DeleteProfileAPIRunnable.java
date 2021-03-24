package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a1_rewards.DisplayProfile;
import com.amozeng.a1_rewards.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteProfileAPIRunnable implements Runnable{

    private static final String TAG = "DeleteProfileAPIRunnabl";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/DeleteProfile";

    private DisplayProfile displayProfile;

    private String username;

    public DeleteProfileAPIRunnable(DisplayProfile d, String username) {
        this.displayProfile = d;
        this.username = username;
    }

    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Log.d(TAG, "run: Initial URL: " + urlString);
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            buildURL.appendQueryParameter("userName", username);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", MainActivity.APIKey);
            connection.connect();

            final StringBuilder sb = new StringBuilder();
            int requestCode = connection.getResponseCode();

            if (requestCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                Log.d(TAG, "deleteUser: " + sb.toString());

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line);
                }
                Log.d(TAG, "deleteUser: " + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doInBackground: Invalid URL: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "deleteUser: Error closing stream: " + e.getMessage());
                }
            }
        }
    }
}
