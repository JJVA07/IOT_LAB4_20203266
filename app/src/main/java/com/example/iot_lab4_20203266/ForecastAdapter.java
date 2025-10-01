package com.example.iot_lab4_20203266.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20203266.R;
import com.example.iot_lab4_20203266.models.ForecastDayItem;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<ForecastDayItem> forecastList = new ArrayList<>();
    private String locationName;
    private String locationId;

    public void updateData(List<ForecastDayItem> newData, String locName, String locId) {
        forecastList = newData;
        locationName = locName;
        locationId = locId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastDayItem item = forecastList.get(position);
        holder.tvLocation.setText("Location: " + locationName + " (ID: " + locationId + ")");
        holder.tvDate.setText("Fecha: " + item.getDate());
        holder.tvMax.setText("Max Temp: " + item.getDay().getMaxtemp_c() + "°C");
        holder.tvMin.setText("Min Temp: " + item.getDay().getMintemp_c() + "°C");
        holder.tvCondition.setText("Clima: " + item.getDay().getCondition().getText());
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocation, tvDate, tvMax, tvMin, tvCondition;

        ViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMax = itemView.findViewById(R.id.tvMax);
            tvMin = itemView.findViewById(R.id.tvMin);
            tvCondition = itemView.findViewById(R.id.tvCondition);
        }
    }
}
