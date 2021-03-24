package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a1_rewards.MainActivity;
import com.amozeng.a1_rewards.api.Profile;
import com.amozeng.a1_rewards.api.Reward;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginAPIRunnable implements Runnable{

    private static final String TAG = "LoginAPIRunnable";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/Login";

    private MainActivity mainActivity;
    private String username;
    private String password;

    public LoginAPIRunnable(MainActivity m, String username, String password) {
        this.mainActivity = m;
        this.username = username;
        this.password = password;
    }

    public void run () {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseURL + endPoint;
            Log.d(TAG, "run: Initial URL: " + urlString);
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            buildURL.appendQueryParameter("username", username);
            buildURL.appendQueryParameter("password", password);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", MainActivity.APIKey);
            connection.connect();

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
            processJSON(resultStr);
            Log.d(TAG, "run: login successfully" + resultStr);

        } catch (Exception e) {
            Log.d(TAG, "run: " + e.getMessage());
        }
    }

    private void processJSON(String s) {
        try{
            JSONObject jsonObject = new JSONObject(s);
            String firstNameStr = jsonObject.getString("firstName");
            String lastNameStr = jsonObject.getString("lastName");
            String usernameStr = jsonObject.getString("userName");
            String dpStr = jsonObject.getString("department");
            String storyStr = jsonObject.getString("story");
            String posStr = jsonObject.getString("position");
            String passwordStr = jsonObject.getString("password");
            String remainingPointsToAward = jsonObject.getString("remainingPointsToAward");
            String locationStr = jsonObject.getString("location");
            String imageBytes = jsonObject.getString("imageBytes");

            Profile newProfile = new Profile(usernameStr);
            newProfile.setFirstName(firstNameStr);
            newProfile.setLastName(lastNameStr);
            newProfile.setDepartment(dpStr);
            newProfile.setStory(storyStr);
            newProfile.setPosition(posStr);
            newProfile.setPassword(passwordStr);
            newProfile.setPointsToAward(remainingPointsToAward);
            newProfile.setLocation(locationStr);
            newProfile.setImageBytes(imageBytes);



            List<Reward> rewardList = new ArrayList<Reward>();
            JSONArray reviewArray = jsonObject.getJSONArray("rewardRecordViews");

            int totalPoints = 0;


            for(int i = 0; i < reviewArray.length(); i++) {
                JSONObject oneReview = (JSONObject) reviewArray.get(i);
                String giverName = oneReview.getString("giverName");
                String amount = oneReview.getString("amount");
                totalPoints += Integer.parseInt(amount);

                String note = oneReview.getString("note");
                String awardDate = oneReview.getString("awardDate");

                Reward newReward = new Reward();
                newReward.setAmount(amount);
                newReward.setGiverName(giverName);
                newReward.setNote(note);
                newReward.setAwardDate(awardDate);

                rewardList.add(newReward);
            }
            newProfile.setPoints(String.valueOf(totalPoints));
            newProfile.setReviewList(rewardList);
            final Profile p = newProfile;

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.getProfileFromLogin(p);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "processJSON: " + e.getMessage());
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.invalidLoginInfo();
                }
            });
        }
    }
}
