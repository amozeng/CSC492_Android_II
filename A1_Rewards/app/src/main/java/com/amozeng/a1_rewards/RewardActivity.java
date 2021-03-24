package com.amozeng.a1_rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amozeng.a1_rewards.Runnable.RewardsAPIRunnable;
import com.amozeng.a1_rewards.api.Profile;
import com.amozeng.a1_rewards.api.Reward;

public class RewardActivity extends AppCompatActivity {

    private static final String TAG = "RewardActivity";

    private ImageView portrait;

    private TextView name;
    private TextView pointsAwarded;
    private TextView department;
    private TextView position;
    private TextView story;

    private EditText pointsToSend;
    private EditText comment;

    private Profile profileHolder;
    private Profile loggedInProfile = DisplayProfile.loggedInUserProfile;

    private Reward newReward = new Reward();

    private static int ADD_REWARD = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        Utilities.setupHomeIndicator(getSupportActionBar()); // Home/Up Nav

        portrait = findViewById(R.id.iv_reward);
        name = findViewById(R.id.tv_reward_name);
        pointsAwarded = findViewById(R.id.tv_reward_point);
        department = findViewById(R.id.tv_reward_department);
        position = findViewById(R.id.tv_reward_position);
        story = findViewById(R.id.tv_reward_story);

        pointsToSend = findViewById(R.id.et_reward_pointsToSend);
        comment = findViewById(R.id.et_reward_comment);

        // get Profile from LeaderboardActivity
        Intent intent = getIntent();
        if(intent.hasExtra("ADD_REWARD")) {
            profileHolder = (Profile) intent.getSerializableExtra("ADD_REWARD");
        }

        if(profileHolder != null) {
            loadProfile(profileHolder);
        }else{
            Log.d(TAG, "onCreate: profile from Leaderboard is null" );
        }
        String fullName = profileHolder.getFirstName() + " " + profileHolder.getLastName();
        setTitle(fullName);

    }

    private void loadProfile(Profile p){
        String fullName = p.getLastName() + ", " + p.getFirstName();
        name.setText(fullName);
        pointsAwarded.setText(p.getPoints());
        department.setText(p.getDepartment());
        position.setText(p.getPosition());
        story.setText(p.getStory());

        Bitmap bitmap = textToImage(p.getImageBytes());
        portrait.setImageBitmap(bitmap);

    }

    public Bitmap textToImage(String imgStr64) {
        if (imgStr64 == null) return null;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public void saveReward(){
        String pointsToSendStr = pointsToSend.getText().toString();
        String commentStr = comment.getText().toString();

        newReward.setAmount(pointsToSendStr);
        newReward.setNote(commentStr);
        newReward.setGiverUser(loggedInProfile.getUsername());
        String giverFullName = loggedInProfile.getFirstName() + " " + loggedInProfile.getLastName();
        newReward.setGiverName(giverFullName);
        newReward.setReceiverUser(profileHolder.getUsername());

        new Thread(new RewardsAPIRunnable(this, newReward)).start();
    }

    public void saveAwardDate(String date) {
        newReward.setAwardDate(date);
    }

    public void saveRewardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveReward();
                backToLeaderBoard();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setTitle("Save Changes?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void backToLeaderBoard() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // ------- MENU -------/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_edit:
                saveRewardDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // ------- MENU END -------/



}

