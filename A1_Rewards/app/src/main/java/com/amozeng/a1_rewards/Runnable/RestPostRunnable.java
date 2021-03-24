package com.amozeng.a1_rewards.Runnable;

import android.net.Uri;

import com.amozeng.a1_rewards.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RestPostRunnable implements Runnable{

    private final MainActivity mainActivity;
    private String firstName, lastName, username, department, story, position, password, remainingPointsToAward, location;


    public RestPostRunnable(MainActivity mainActivity, String firstName, String lastName,
                            String username, String dp, String story, String position,
                            String password, String remainingPointsToAward, String location) {
        this.mainActivity = mainActivity;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = dp;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPointsToAward = remainingPointsToAward;
        this.location = location;
    }


    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // example call
            //String urlString = "http://christopherhield.org/api/Profile/CreateProfile ?firstName=John&lastName=Smith&userName=jsnith123&department=Finance &story=Tell other users about yourself...&position=Sr. Accountant &password=MyAwesomePassword123&remainingPointsToAward=1000&location=Chicago, IL"

            String baseUrl = "http://christopherhield.org/api/";

            String urlString = baseUrl + "/Profile/CreateProfile" + "?firstName=" + firstName
                    + "&lastName=" + lastName + "&userName=" + username + "&department=" + department
                    + "&story=" + story + "&position=" + position + "&password=" + password
                    + "&remainingPointsToAward=" + remainingPointsToAward + "&location=" + location;

            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Apikey", mainActivity.APIKey);
            connection.connect();

//            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
//            out.write(jsonObject.toString());
//            out.close();



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
