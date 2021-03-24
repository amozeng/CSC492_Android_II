package com.amozeng.a2_newsaggregator.Loader;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a2_newsaggregator.APIs.Source;
import com.amozeng.a2_newsaggregator.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class SourceLoader implements Runnable{

    private static final String TAG = "SourceLoader";
    private final MainActivity mainActivity;
    private static final String dataURL = "https://newsapi.org/v2/sources?apiKey=fe1872653e3c44a3b5bbc12343fa9c0c";

    private List<Source> sources = new ArrayList<>();
    private List<String> topicsList = new ArrayList<>();
    private List<String> countryList = new ArrayList<>();
    private List<String> languageList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();



    public SourceLoader(MainActivity m) {
        mainActivity = m;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent","");
            conn.connect();

            StringBuilder sb = new StringBuilder();
            String line;

            int respondCode = conn.getResponseCode();

            if (conn.getResponseCode() == HTTP_OK) {
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getInputStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();

            } else {
                BufferedReader reader =
                        new BufferedReader((new InputStreamReader(conn.getErrorStream())));

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                conn.disconnect();
                Log.d(TAG, "run: " + sb.toString());
            }

            String result = sb.toString();
            processJSON(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray sourcesJArray = jsonObject.getJSONArray("sources");
            for(int i = 0; i < sourcesJArray.length(); i++) {
                JSONObject jSource = (JSONObject) sourcesJArray.get(i);
                String id = jSource.getString("id");
                String name = jSource.getString("name");
                String category = jSource.getString("category");
                String language = jSource.getString("language");
                String country = jSource.getString("country");

                Source source = new Source(id, name, category,language,country);
                sources.add(source);

                // get non-repeated topics
                if(!topicsList.contains(category)) {
                    topicsList.add(category);
                }

                // get non-repeated countries
                if(!countryList.contains(country)) {
                    countryList.add(country);
                }
                // get non-repeated languages
                if(!languageList.contains(language)) {
                    languageList.add(language);
                }

                // get non-repeated media name
                if(!nameList.contains(name)) {
                    nameList.add(name);
                }
            }

            mainActivity.runOnUiThread(() -> {
                mainActivity.setupNameList(nameList);
                mainActivity.setupTopicList(topicsList);

                mainActivity.getSources(sources);

                mainActivity.setupCountryList(countryList);
                mainActivity.setupLanguageList(languageList);
                mainActivity.setupDrawerItemColor();

            });


        } catch (Exception e) {
            Log.d(TAG, "processJSON: " + e.getMessage());
        }



    }
}
