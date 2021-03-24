package com.amozeng.a2_newsaggregator.Loader;

import android.net.Uri;
import android.util.Log;

import com.amozeng.a2_newsaggregator.APIs.Article;
import com.amozeng.a2_newsaggregator.APIs.Source;
import com.amozeng.a2_newsaggregator.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.net.HttpURLConnection.HTTP_OK;

public class ArticleLoader implements Runnable{

    private static final String TAG = "ArticleLoader";
    private final MainActivity mainActivity;
    private final String selectedMedia;

    private static final String baseURL = "https://newsapi.org/v2/top-headlines?sources=";

    public ArticleLoader(MainActivity mainActivity, String selectedMedia) {
        this.mainActivity = mainActivity;
        this.selectedMedia = selectedMedia;
    }

    public void run() {
        String dataURL = baseURL + selectedMedia + "&apiKey=fe1872653e3c44a3b5bbc12343fa9c0c";
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
            ArrayList<Article> articleList = processJSON(result);
            mainActivity.runOnUiThread(() -> mainActivity.setArticles(articleList));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Article> processJSON(String s) {
        ArrayList<Article> articleList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray articleJArray = jsonObject.getJSONArray("articles");
            for(int i = 0; i < articleJArray.length(); i++) {
                JSONObject jArticle = (JSONObject) articleJArray.get(i);
                String author = jArticle.getString("author");
                String title = jArticle.getString("title");
                String content = jArticle.getString("description");
                String url = jArticle.getString("url");
                String urlToImage = jArticle.getString("urlToImage");
                String date = jArticle.getString("publishedAt");

                Article article = new Article();
                article.setAuthor(author);
                article.setTitle(title);
                article.setDescription(content);
                article.setUrl(url);
                article.setUrlToImage(urlToImage);
                article.setDate(date);

                articleList.add(article);



            }
            return articleList;



        } catch (Exception e) {
            Log.d(TAG, "processJSON: " + e.getMessage());
        }
        return null;

    }
}
