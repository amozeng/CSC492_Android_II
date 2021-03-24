package com.amozeng.a1_rewards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amozeng.a1_rewards.Runnable.DeleteProfileAPIRunnable;
import com.amozeng.a1_rewards.api.Profile;
import com.amozeng.a1_rewards.api.Reward;
import com.amozeng.a1_rewards.api.RewardAdaptor;

import java.util.ArrayList;
import java.util.List;

public class DisplayProfile extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "DisplayProfile";

    private static int CREATE_REQUEST = 0;
    private static int EDIT_REQUEST = 1;

    public static Profile loggedInUserProfile;

    TextView name;
    TextView username;
    TextView location;
    TextView points;
    TextView department;
    TextView position;
    TextView pointsToAward;
    TextView story;
    ImageView portrait;
    TextView rewardHistoryAmount;

    private final List<Reward> rewardList = new ArrayList<>();
    private RewardAdaptor mAdaptor;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);

        setTitle("Your Profile");

        name = findViewById(R.id.tv_name);
        username = findViewById(R.id.tv_username);
        location = findViewById(R.id.tv_location);
        points = findViewById(R.id.tv_points);
        department = findViewById(R.id.tv_department);
        position = findViewById(R.id.tv_position);
        pointsToAward = findViewById(R.id.tv_pointsToAward);
        story = findViewById(R.id.tv_story);
        rewardHistoryAmount =  findViewById(R.id.tv_display_rewardHistory);

        portrait = findViewById(R.id.iv_display_portrait);

        // get profile from CreateProfile
        Intent intent = getIntent();
        if(intent.hasExtra("NEW_PROFILE")) {
            loggedInUserProfile = (Profile) intent.getSerializableExtra("NEW_PROFILE");

        } else if (intent.hasExtra("LOGIN_PROFILE")) {
            loggedInUserProfile = (Profile) intent.getSerializableExtra("LOGIN_PROFILE");
        }

        if(loggedInUserProfile != null) {
            updateProfile(loggedInUserProfile);
        }

        // Recycler View for reward history
        recyclerView = findViewById(R.id.display_recycler);
        mAdaptor = new RewardAdaptor(rewardList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {

    }


    private void updateProfile(Profile p){
        String fullName = p.getLastName() + ", " + p.getFirstName();
        name.setText(fullName);
        username.setText("("+p.getUsername()+")");
        department.setText(p.getDepartment());
        position.setText(p.getPosition());
        String storyTmp = p.getStory();
        story.setText(p.getStory());

        String pointStr = p.getPoints();
        points.setText(pointStr);
        location.setText(p.getLocation());
        pointsToAward.setText(p.getPointsToAward());
        String imgStr = p.getImageBytes();

        textToImage(p.getImageBytes());

        rewardHistoryAmount.setText("Reward History(" + p.getReviewList().size() + "): ");

        // add rewards from profile to rewardList
        for(int i = 0; i < p.getReviewList().size(); i++){
            Reward r = p.getReviewList().get(i);
            rewardList.add(r);
            //mAdaptor.notifyDataSetChanged();
        }
    }

    void displayRewardHistory() {

    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        portrait.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_edit:
                startEditProfileActivity();
                return true;
            case R.id.menu_leadboard:
                startLeaderBoard();
                return true;
            case R.id.menu_pref:
                return true;
            case R.id.menu_delete:
                deleteDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startDeleteProfile();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // just a OK button
            }
        });

        String content = "Delete Profile for " + loggedInUserProfile.getFirstName() + " " + loggedInUserProfile.getLastName()
                +"\n(The Rewards app will be closed upon deletion).";

        builder.setTitle("Delete Profile?");
        builder.setMessage(content);

        builder.setIcon(R.drawable.icon);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addReward(Reward newReward) {
        if(newReward == null) Log.d(TAG, "addReward: null reward to add");

        rewardList.add(newReward);
        mAdaptor.notifyDataSetChanged();
    }

    public void startDeleteProfile () {
        new Thread(new DeleteProfileAPIRunnable(this, loggedInUserProfile.getUsername())).start();
        finishAffinity();
    }

    public void startEditProfileActivity() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("EDIT_PROFILE", loggedInUserProfile);
        startActivityForResult(intent, EDIT_REQUEST);
    }

    public void startLeaderBoard() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                if(data != null) {
                    Profile p = (Profile)data.getSerializableExtra("EDIT_PROFILE");
                    if(p != null){
                        updateProfile(p);
                    }
                }
            }
        }
    }
}