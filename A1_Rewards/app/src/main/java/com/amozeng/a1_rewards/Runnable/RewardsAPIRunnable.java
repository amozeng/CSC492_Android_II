package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a1_rewards.MainActivity;
import com.amozeng.a1_rewards.RewardActivity;
import com.amozeng.a1_rewards.api.Reward;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RewardsAPIRunnable implements Runnable {

    private static final String TAG = "RewardsAPIRunnable";
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Rewards/AddRewardRecord";

    private RewardActivity rewardActivity;

    private String receiverUser;
    private String giverUser;
    private String giverName;
    private String amount;
    private String note;

    public RewardsAPIRunnable(RewardActivity ra, Reward r) {
        this.rewardActivity = ra;
        this.receiverUser = r.getReceiverUser();
        this.giverUser = r.getGiverUser();
        this.giverName = r.getGiverName();
        this.amount = r.getAmount();
        this.note = r.getNote();
    }


    @Override
    public void run() {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Log.d(TAG, "run: Initial URL: " + urlString);
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            buildURL.appendQueryParameter("receiverUser", receiverUser);
            buildURL.appendQueryParameter("giverUser", giverUser);
            buildURL.appendQueryParameter("giverName", giverName);
            buildURL.appendQueryParameter("amount", amount);
            buildURL.appendQueryParameter("note", note);

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);
            Log.d(TAG, "run: Full URL: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", MainActivity.APIKey);
            connection.connect();

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


        } catch (Exception e) {
            Log.d(TAG, "run: " + e.getMessage());
        }
    }

    private void processJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String awardDate = jsonObject.getString("awardDate");
            rewardActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rewardActivity.saveAwardDate(awardDate);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "processJSON: " + e.getMessage());
        }
    }
}
