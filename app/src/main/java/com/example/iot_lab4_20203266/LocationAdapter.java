package com.example.iot_lab4_20203266;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20203266.models.LocationItem;
import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<LocationItem> locations = new ArrayList<>();

    public LocationAdapter() {
    }

    public void updateData(List<LocationItem> newLocations) {
        this.locations = newLocations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationItem item = locations.get(position);
        holder.tvId.setText("ID: " + item.getId());
        holder.tvName.setText("Nombre: " + item.getName());
        holder.tvRegion.setText("Región: " + item.getRegion());
        holder.tvCountry.setText("País: " + item.getCountry());
        holder.tvLatLon.setText("Lat: " + item.getLat() + " / Lon: " + item.getLon());
        holder.tvUrl.setText("URL: " + item.getUrl());
    }

    @Override
    public int getItemCount() {
        return locations != null ? locations.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvRegion, tvCountry, tvLatLon, tvUrl;

        ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            tvRegion = itemView.findViewById(R.id.tvRegion);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvLatLon = itemView.findViewById(R.id.tvLatLon); // 👈 coincide con el layout
            tvUrl = itemView.findViewById(R.id.tvUrl);
        }
    }
}
