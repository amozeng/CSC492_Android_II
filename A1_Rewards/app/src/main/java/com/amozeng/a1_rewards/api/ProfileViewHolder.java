package com.amozeng.a1_rewards.api;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amozeng.a1_rewards.R;

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    public TextView name, posAndDp, points;
    public ImageView portrait;


    public ProfileViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.recycler_name);
        posAndDp = view.findViewById(R.id.recycler_posAndDp);
        points = view.findViewById(R.id.recycler_points);
        portrait = view.findViewById(R.id.recycler_portrait);
    }
}
