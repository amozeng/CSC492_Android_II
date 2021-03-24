package com.amozeng.a1_rewards.api;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amozeng.a1_rewards.R;

public class RewardViewHolder extends RecyclerView.ViewHolder{

    public TextView date;
    public TextView name;
    public TextView points;
    public TextView notes;

    public RewardViewHolder(View view) {
        super(view);
        date = view.findViewById(R.id.entry_reward_awardDate);
        name = view.findViewById(R.id.entry_reward_name);
        points = view.findViewById(R.id.entry_reward_points);
        notes = view.findViewById(R.id.entry_reward_notes);
    }
}
