package com.amozeng.a1_rewards.api;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amozeng.a1_rewards.DisplayProfile;
import com.amozeng.a1_rewards.R;

import java.util.List;

public class RewardAdaptor extends RecyclerView.Adapter<RewardViewHolder> {

    private static final String TAG = "RewardAdaptor";

    private List<Reward> rewardListHolder;
    private DisplayProfile displayProfile;

    public RewardAdaptor(List<Reward> rList, DisplayProfile d) {
        this.rewardListHolder = rList;
        this.displayProfile = d;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW profileHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_reward, parent, false);
        itemView.setOnClickListener(displayProfile);
        return new RewardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward r = rewardListHolder.get(position);
        String date = r.getAwardDate().substring(0,10); //"2021-01-29T20:46:38" to "2021-01-29"
        String name = r.getGiverName();
        String points = r.getAmount();
        String notes = r.getNote();

        holder.date.setText(date);
        holder.name.setText(name);
        holder.points.setText(points);
        holder.notes.setText(notes);
    }

    @Override
    public int getItemCount() {
        return rewardListHolder.size();
    }
}
