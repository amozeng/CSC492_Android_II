package com.amozeng.a2_newsaggregator.APIs;

import android.content.Context;
import android.util.Log;

import com.amozeng.a2_newsaggregator.MainActivity;
import com.amozeng.a2_newsaggregator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DataContainer {
    private static final String TAG = "DataContainer";
    private static final HashMap<String, String> countryMap = new HashMap<>();
    private static final HashMap<String, String> languageMap = new HashMap<>();


    public static HashMap<String, String> getCountryMap() { return countryMap; }
    public static HashMap<String, String> getLanguageMap() { return languageMap; }

    public static void loadData(MainActivity context) {
        try {
            String countryJSONStr = loadCountryJSONData(context);
            processCountryJSON(countryJSONStr);

            String languageJSONStr = loadLanguageJSONData(context);
            processLanguageJSON(languageJSONStr);

        }catch (Exception e) {
            Log.d(TAG, "laodData: " + e.getMessage());
        }
    }

     static String loadCountryJSONData(Context context) throws IOException, JSONException {
        // for country
        InputStream is = context.getResources().openRawResource(R.raw.country_codes);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return sb.toString();

    }

    static String loadLanguageJSONData(Context context) throws IOException, JSONException {
        // for country
        InputStream is = context.getResources().openRawResource(R.raw.language_codes);

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();

        return sb.toString();

    }

    private static void processCountryJSON(String s) {
        try
        {
            JSONObject jObject = new JSONObject(s);
            JSONArray countryArray = jObject.getJSONArray("countries");
            for(int i = 0; i < countryArray.length(); i++) {
                JSONObject country = (JSONObject) countryArray.get(i);
                String code = country.getString("code");
                String name = country.getString("name");
                // if country code is not repeated, add it to map
                if(!countryMap.containsKey(code)) {
                    countryMap.put(code, name);
                }
            }
        }catch (Exception e) {
            Log.d(TAG, "processCountryJSON: " + e.getMessage());
        }
    }

    private static void processLanguageJSON(String s) {
        try
        {
            JSONObject jObject = new JSONObject(s);
            JSONArray languageArray = jObject.getJSONArray("languages");
            for(int i = 0; i < languageArray.length(); i++) {
                JSONObject language = (JSONObject) languageArray.get(i);
                String code = language.getString("code");
                String name = language.getString("name");
                // if country code is not repeated, add it to map
                if(!languageMap.containsKey(code)) {
                    languageMap.put(code, name);
                }
            }
        }catch (Exception e) {
            Log.d(TAG, "processCountryJSON: " + e.getMessage());
        }
    }

}
