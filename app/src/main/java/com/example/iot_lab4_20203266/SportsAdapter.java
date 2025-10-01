package com.example.iot_lab4_20203266.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20203266.R;
import com.example.iot_lab4_20203266.models.FootballMatch;

import java.util.ArrayList;
import java.util.List;

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.ViewHolder> {

    private List<FootballMatch> matches = new ArrayList<>();

    public void updateData(List<FootballMatch> newMatches) {
        matches = newMatches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sports, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FootballMatch match = matches.get(position);
        holder.tvStadium.setText("Stadium: " + match.getStadium());
        holder.tvCountry.setText("Country: " + match.getCountry());
        holder.tvTournament.setText("Tournament: " + match.getTournament());
        holder.tvStart.setText("Start: " + match.getStart());
        holder.tvMatch.setText("Match: " + match.getMatch());
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStadium, tvCountry, tvTournament, tvStart, tvMatch;

        ViewHolder(View itemView) {
            super(itemView);
            tvStadium = itemView.findViewById(R.id.tvStadium);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvTournament = itemView.findViewById(R.id.tvTournament);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvMatch = itemView.findViewById(R.id.tvMatch);
        }
    }
}
