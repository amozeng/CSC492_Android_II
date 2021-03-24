package com.amozeng.a1_rewards.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amozeng.a1_rewards.LeaderboardActivity;
import com.amozeng.a1_rewards.R;

import java.util.List;

public class ProfileAdaptor extends RecyclerView.Adapter<ProfileViewHolder>  {

    private static final String TAG = "ProfileAdaptor";

    private List<Profile> profileListHolder;
    private LeaderboardActivity leaderboardActivity;

    public ProfileAdaptor(List<Profile> pList, LeaderboardActivity m) {
        this.profileListHolder = pList;
        this.leaderboardActivity = m;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW profileHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_profile, parent, false);
        itemView.setOnClickListener(leaderboardActivity);

        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile p = profileListHolder.get(position);
        String fullName = p.getLastName() + ", " + p.getFirstName();
        holder.name.setText(fullName);
        holder.points.setText(p.getPoints());
        String posAndDp = p.getPosition() + ", " + p.getDepartment();
        holder.posAndDp.setText(posAndDp);
        String imgStr64 = p.getImageBytes();
        Bitmap bitmap = textToImageBitmap(imgStr64);
        holder.portrait.setImageBitmap(bitmap);
    }



    @Override
    public int getItemCount() {
        return profileListHolder.size();
    }

    public Bitmap textToImageBitmap(String imgStr64) {
        if (imgStr64 == null) {
            Log.d(TAG, "textToImageBitmap: imgStr64 is null" );
            return null;
        }

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return bitmap;
    }
}
