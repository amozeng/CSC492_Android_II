package com.amozeng.a1_rewards;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amozeng.a1_rewards.Runnable.GetAllProfilesAPIRunnable;
import com.amozeng.a1_rewards.api.Profile;
import com.amozeng.a1_rewards.api.ProfileAdaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LeaderboardActivity";

    private List<Profile> profileList = new ArrayList<>();
    private ProfileAdaptor mAdaptor;
    private RecyclerView recyclerView;

    //private static int OPEN_PROFILE_REQUEST = 0;
    private static int ADD_REWARD_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Utilities.setupHomeIndicator(getSupportActionBar());

        setTitle("Leaderboard");

        recyclerView = findViewById(R.id.recyclerView);
        mAdaptor = new ProfileAdaptor(profileList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllProfile();
    }


    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Profile p = profileList.get(pos);

        Intent intent = new Intent(this, RewardActivity.class);
        intent.putExtra("ADD_REWARD", p);
        startActivityForResult(intent, ADD_REWARD_REQUEST);
    }

    private void getAllProfile() {
        new Thread(new GetAllProfilesAPIRunnable(this, MainActivity.APIKey)).start();
    }

    public void addProfile(Profile newProfile) {
        if(newProfile == null) Log.d(TAG, "addProfile: null new profile to add");

        profileList.add(newProfile);
        mAdaptor.notifyDataSetChanged();
        Collections.sort(profileList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_REWARD_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                profileList.clear();
                getAllProfile();
                Collections.sort(profileList);
            }
        }
    }
}