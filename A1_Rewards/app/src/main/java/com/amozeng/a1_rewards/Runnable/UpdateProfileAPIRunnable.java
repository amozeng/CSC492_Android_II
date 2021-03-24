package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.amozeng.a1_rewards.EditProfileActivity;
import com.amozeng.a1_rewards.MainActivity;
import com.amozeng.a1_rewards.api.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateProfileAPIRunnable implements Runnable{

    private static final String TAG = "UpdateProfileAPIRunnabl";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/UpdateProfile";
    private EditProfileActivity editProfileActivity;

    private Profile p;
    private String firstName;
    private String lastName;
    private String username;
    private String department;
    private String story;
    private String position;
    private String password;
    private String location;
    private String imageBase64;



    public UpdateProfileAPIRunnable(EditProfileActivity e, Profile profile) {
        this.editProfileActivity = e;
        this.p = profile;
        firstName = p.getFirstName();
        lastName = p.getLastName();
        username = p.getUsername();
        department = p.getDepartment();
        story = p.getStory();
        position = p.getPosition();
        password = p.getPassword();
        location = p.getLocation();
        imageBase64 = p.getImageBytes();
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Example Call:
            // http://christopherhield.org/api/Profile/UpdateProfile?firstName=Gabriel &lastName=Anderson&userName=Gabriel.Anderson&department=Marketing&story=This is a story&position=Director&password=GabAnd123&location=Boise, Idaho

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
            buildURL.appendQueryParameter("location", location);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", MainActivity.APIKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageBase64);
            out.close();

            int requestCode = connection.getResponseCode();
            StringBuilder sb = new StringBuilder();

            if (requestCode == HttpURLConnection.HTTP_OK) {
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

            backToDisplay(resultStr);

        } catch (Exception e){
            Log.d(TAG, "run: " + e.getMessage());
        }
    }

    private void backToDisplay(String s) {


        try{
            JSONObject jsonObject = new JSONObject(s);
            String points = jsonObject.getString("remainingPointsToAward");

            editProfileActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editProfileActivity.getUpdatedProfile(points);
                    //editProfileActivity.backToDisplayActivity();
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "backToDisplay: " + e.getMessage());
        }



    }

}
