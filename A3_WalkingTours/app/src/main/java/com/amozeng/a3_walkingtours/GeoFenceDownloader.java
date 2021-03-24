package com.amozeng.a3_walkingtours;

import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GeoFenceDownloader implements Runnable {

    private static final String TAG = "GeoFenceDownloader";
    private final Geocoder geocoder;
    private final GeoFenceManager geoFenceManager;
    private static final String TOUR_URL = "http://www.christopherhield.com/data/WalkingTourContent.json";
    private final MapsActivity mapsActivity;


    public GeoFenceDownloader(MapsActivity m, GeoFenceManager manager) {
        mapsActivity = m;
        geoFenceManager = manager;
        geocoder = new Geocoder(m);
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(TOUR_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: Response code: " + connection.getResponseCode());
                return;
            }

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            processData(buffer.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processData(String result) {
        if (result == null) {
            Log.d(TAG, "processData: null data");
            return;
        }
        ArrayList<GeoFenceData> fences = new ArrayList<>();
        try {
            JSONObject jObj = new JSONObject(result);
            JSONArray fenceArr = jObj.getJSONArray("fences");
            for (int i = 0; i < fenceArr.length(); i++) {
                JSONObject fence = fenceArr.getJSONObject(i);
                String buildingName = fence.getString("id");
                String address = fence.getString("address");
                Double latitude = fence.getDouble("latitude");
                Double longitude = fence.getDouble("longitude");
                float radius = (float) fence.getDouble("radius");
                String description = fence.getString("description");
                String fenceColor = fence.getString("fenceColor");
                String image = fence.getString("image");

                GeoFenceData newFence = new GeoFenceData(buildingName, address, latitude, longitude, radius, description, fenceColor, image);
                fences.add(newFence);
            }
            Log.d(TAG, "processData: total " + fences.size() + " fences");
            geoFenceManager.addFences(fences);

            // get TOUR PATH:
            JSONArray pathArr = jObj.getJSONArray("path");
            ArrayList<LatLng> LatLngList = new ArrayList<>();
            for (int i = 0; i < pathArr.length(); i++) {
                String point = pathArr.getString(i);
                String[] stringArr = point.split("\\s*,\\s*");
                Double longitude = Double.parseDouble(stringArr[0]);
                Double latitude = Double.parseDouble(stringArr[1]);
                LatLng latLng = new LatLng(latitude, longitude);
                LatLngList.add(latLng);
            }

            mapsActivity.runOnUiThread(() -> {
                mapsActivity.getTourPath(LatLngList);
            });
        } catch (Exception e) {
            Log.d(TAG, "processData: " + e.getMessage());
        }
    }
}
