package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a1_rewards.LeaderboardActivity;
import com.amozeng.a1_rewards.api.Profile;
import com.amozeng.a1_rewards.api.Reward;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetAllProfilesAPIRunnable implements Runnable{

    private static final String TAG = "RestGetAllRunnable";
    private LeaderboardActivity leaderboardActivity;
    private String APIKey;

    private List<Profile> profileList = new ArrayList<>();

    public GetAllProfilesAPIRunnable(LeaderboardActivity l, String apiKey){
        this.leaderboardActivity = l;
        this.APIKey = apiKey;
    }
    @Override
    public void run () {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            // Example Call:
            // http://christopherhield.org/api/Profile/GetAllProfiles
            String baseUrl = "http://christopherhield.org/api/";
            String urlString = baseUrl + "Profile/GetAllProfiles";

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", APIKey);

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
            // clean later
            String resultStr = result.toString();
            processAllProfile(result.toString());

        } catch (Exception e){
            Log.d(TAG, "run: " + e.getMessage());
        }
    }

    private void processAllProfile(String s) {
        try {
            JSONArray profileArray = new JSONArray(s);

            Profile newProfile;

            // for debugging
            int arrayLength = profileArray.length();

            for(int i = 0; i < profileArray.length(); i++){


                JSONObject profile = (JSONObject) profileArray.get(i);
                String firstName = profile.getString("firstName");
                String lastName = profile.getString("lastName");
                String userName = profile.getString("userName");
                String department = profile.getString("department");
                String story = profile.getString("story");
                String position = profile.getString("position");
                String imageBytes = profile.getString("imageBytes");

                newProfile = new Profile(userName);
                newProfile.setFirstName(firstName);
                newProfile.setLastName(lastName);
                newProfile.setDepartment(department);
                newProfile.setPosition(position);
                newProfile.setStory(story);
                newProfile.setImageBytes(imageBytes);

                JSONArray reviewArray = profile.getJSONArray("rewardRecordViews");

                List<Reward> rewardList = new ArrayList<Reward>();

                int totalPoints = 0;

                for(int j = 0; j < reviewArray.length(); j++){
                    JSONObject review = (JSONObject)reviewArray.get(j);
                    String giverName = review.getString("giverName");
                    String amount = review.getString("amount");
                    totalPoints += Integer.parseInt(amount);
                    String note = review.getString("note");
                    String awardDate = review.getString("awardDate");

                    Reward newReward = new Reward();
                    newReward.setAmount(amount);
                    newReward.setGiverName(giverName);
                    newReward.setNote(note);
                    newReward.setAwardDate(awardDate);

                    rewardList.add(newReward);
                }
                newProfile.setPoints(String.valueOf(totalPoints));
                newProfile.setReviewList(rewardList);

                profileList.add(newProfile);

                final Profile finalNewProfile = newProfile;
                leaderboardActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leaderboardActivity.addProfile(finalNewProfile);
                    }
                });
            }





        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getApiKey: " + e.getMessage());
        }
    }
}
