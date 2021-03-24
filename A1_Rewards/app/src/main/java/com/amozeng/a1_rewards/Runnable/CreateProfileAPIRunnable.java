package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a1_rewards.CreateProfileActivity;
import com.amozeng.a1_rewards.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateProfileAPIRunnable implements Runnable{

    private static final String TAG = "CreateProfileAPIRunnabl";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/CreateProfile";

    private CreateProfileActivity createProfileActivity;

    private String firstName, lastName, username, password, department, position, story, remainingPointsToAward, location;
    private String imageBase64;

    public CreateProfileAPIRunnable(CreateProfileActivity createProfileActivity, String fn, String ln,
                                    String user, String password, String dp, String pos,
                                    String story, String remainingPointsToAward, String location,
                                    String imageBase64) {
        this.createProfileActivity = createProfileActivity;
        this.firstName = fn;
        this.lastName = ln;
        this.username = user;
        this.password = password;
        this.department = dp;
        this.position = pos;
        this.story = story;
        this.remainingPointsToAward = "1000";
        this.location = location;

        //this.image = CreateProfile.selectedImage;
        this.imageBase64 = imageBase64;
    }


    @Override
    public void run() {

        // Here I delete he user since this is a sample.
        // You do NOT need to do this.

        //deleteUser();

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            // Example Call:
            // http://christopherhield.org/api/Profile/CreateProfile?firstName=John&lastName=Smith&userName=jsnith123&department=Finance &story=Tell other users about yourself...&position=Sr. Accountant &password=MyAwesomePassword123&remainingPointsToAward=1000&location=Chicago, IL
            String urlString = baseURL + endPoint;
            Log.d(TAG, "run: Initial URL: " + urlString);
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            buildURL.appendQueryParameter("firstName", firstName);
            buildURL.appendQueryParameter("lastName", lastName);
            buildURL.appendQueryParameter("userName", username);
            buildURL.appendQueryParameter("department", department);
            buildURL.appendQueryParameter("story", story);
            buildURL.appendQueryParameter("position", position);
            buildURL.appendQueryParameter("password", password);
            buildURL.appendQueryParameter("remainingPointsToAward", remainingPointsToAward);
            buildURL.appendQueryParameter("location", location);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", MainActivity.APIKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageBase64);
            out.close();


            int requestCode = connection.getResponseCode();
            StringBuilder sb = new StringBuilder();

            if (requestCode == HttpURLConnection.HTTP_CREATED) {
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line).append("\n");
                }
            }else{
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    sb.append(line).append("\n");
                }
            }

            String resultStr = sb.toString();
            Log.d(TAG, "run: created successfully" + resultStr);
            //processJSONFile(resultStr);

        } catch (Exception e){
            Log.d(TAG, "run: " + e.getMessage());
        }
    }

//    private void processJSONFile(String s) {
//        try {
//            JSONObject jsonObject = new JSONObject(s);
//
//            String firstName = jsonObject.getString("firstName");
//            String lastName = jsonObject.getString("lastName");
//            String userName = jsonObject.getString("userName");
//            String department = jsonObject.getString("department");
//            String story = jsonObject.getString("story");
//            String position = jsonObject.getString("position");
//            String password = jsonObject.getString("password");
//            String remainingPointsToAward = jsonObject.getString("remainingPointsToAward");
//            String location = jsonObject.getString("location");
//            String imageBytes = jsonObject.getString("imageBytes");
//
//            createProfileActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    CreateProfile.
//                }
//            });
//            //mainActivity.saveAPIKey(APIKey);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.d(TAG, "getApiKey: " + e.getMessage());
//        }
//    }

    //////////////////////////////////////////////////////
    // This deleteUser method is only for sample purposes
    //////////////////////////////////////////////////////
    private void deleteUser() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + "Profile/DeleteProfile";
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            buildURL.appendQueryParameter("userName", username);
            String urlToUse = buildURL.build().toString();

            URL url = new URL(urlToUse);
            Log.d(TAG, "deleteUser: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", MainActivity.APIKey);
            connection.connect();

            final StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
