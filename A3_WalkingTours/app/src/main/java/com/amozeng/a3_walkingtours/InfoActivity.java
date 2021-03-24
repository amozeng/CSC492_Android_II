package com.amozeng.a3_walkingtours;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class InfoActivity extends AppCompatActivity {

    private Typeface myFont;


    private static final String TAG = "InfoActivity";

    private TextView buildingName;
    private TextView buildingAddr;
    private TextView buildingDscp;
    private ImageView buildingImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setTitle("");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.home_image);
        }

        myFont = Typeface.createFromAsset(getAssets(), "fonts/Acme-Regular.ttf");


        ConstraintLayout layout = findViewById(R.id.layout);
        buildingName = findViewById(R.id.building_name);
        buildingAddr = findViewById(R.id.building_address);
        buildingDscp = findViewById(R.id.building_description);
        buildingImg = findViewById(R.id.imageView);

        buildingName.setTypeface(myFont);
        buildingAddr.setTypeface(myFont);
        buildingDscp.setTypeface(myFont);


        GeoFenceData fd = (GeoFenceData) getIntent().getSerializableExtra("DATA");

        if (fd != null) {
            buildingName.setText(fd.getId());
            buildingAddr.setText(fd.getAddress());
            String description = fd.getDescription();
            buildingDscp.setText(fd.getDescription());
            loadImagePicasso(buildingImg, fd.getImageURL());
        }

        buildingDscp.setMovementMethod(new ScrollingMovementMethod());

    }

    private void loadImagePicasso(ImageView imageView, String imageURL){

        Picasso.get().load(imageURL).into(imageView,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: Size:" + ((BitmapDrawable) imageView.getDrawable()).getBitmap().getByteCount());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }
}