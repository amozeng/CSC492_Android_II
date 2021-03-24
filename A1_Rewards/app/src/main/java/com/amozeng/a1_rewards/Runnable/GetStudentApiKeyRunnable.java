package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a1_rewards.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;


public class GetStudentApiKeyRunnable implements Runnable{

    private static final String TAG = "RestGetRunnable";
    public static final int GET_API_KEY = 0;
    public static final int GET_ALL_PROFILES = 1;

    private MainActivity mainActivity;
    private String fName, lName, email, id;
    private int TASK;

    public GetStudentApiKeyRunnable(MainActivity mainActivity, String fName, String lName, String email, String id) {
        this.mainActivity = mainActivity;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.id = id;
    }


    @Override
    public void run() {
        JSONObject jsonObject = new JSONObject();

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

                //Example Call:
                //http://christopherhield.org/api/Profile/GetStudentApiKey?firstName=Chris&lastName=Hield&studentId=Chris.Hield&email=chield@domain.edu
                String baseUrl = "http://christopherhield.org/api/";
                String urlString = baseUrl + "Profile/GetStudentApiKey?firstName=" + this.fName + "&lastName=" + this.lName + "&studentId=" + this.id + "&email=" + this.email;

                Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
                String urlToUse = buildURL.build().toString();
                URL url = new URL(urlToUse);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();


            int requestCode = connection.getResponseCode();
            StringBuilder result = new StringBuilder();

            if (requestCode == HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }else{
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }
                getAPIKey(result.toString());

        } catch (Exception e){
            Log.d(TAG, "run: " + e.getMessage());
        }

    }

    private void getAPIKey(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String APIKey = jsonObject.getString("apiKey");
            Log.d(TAG, "getApiKey: " + APIKey);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.saveAPIKey(APIKey);
                }
            });
            //mainActivity.saveAPIKey(APIKey);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getApiKey: " + e.getMessage());
        }

    }
}
