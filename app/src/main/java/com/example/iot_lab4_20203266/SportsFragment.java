package com.example.iot_lab4_20203266;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20203266.adapters.SportsAdapter;
import com.example.iot_lab4_20203266.models.FootballMatch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SportsFragment extends Fragment {

    private static final String API_KEY = "30a2c3275bc3456ba38160745250305";
    private static final String TAG = "SportsFragment";

    private EditText etLocation;
    private Button btnBuscar;
    private RecyclerView rvSports;
    private TextView tvEmptyMessage;
    private SportsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sports, container, false);

        etLocation = view.findViewById(R.id.etLocationSports);
        btnBuscar = view.findViewById(R.id.btnBuscarSports);
        rvSports = view.findViewById(R.id.rvSports);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        adapter = new SportsAdapter();
        rvSports.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSports.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> {
            String query = etLocation.getText().toString().trim().toLowerCase();
            if (!query.isEmpty()) buscarPartidos(query);
        });

        return view;
    }

    private void buscarPartidos(String location) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.weatherapi.com/v1/sports.json?key=" + API_KEY + "&q=" + location;
        Log.d(TAG, "🌐 URL Sports: " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "❌ Error en la petición HTTP", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Log.d(TAG, "✅ JSON Sports: " + json);

                    try {
                        Gson gson = new Gson();
                        JsonObject root = gson.fromJson(json, JsonObject.class);

                        JsonArray footballArray = root.getAsJsonArray("football");

                        if (footballArray != null && footballArray.size() > 0) {
                            Type listType = new TypeToken<List<FootballMatch>>() {}.getType();
                            List<FootballMatch> matches = gson.fromJson(footballArray, listType);

                            requireActivity().runOnUiThread(() -> {
                                tvEmptyMessage.setVisibility(View.GONE);
                                adapter.updateData(matches);
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                adapter.updateData(Collections.emptyList());
                                tvEmptyMessage.setVisibility(View.VISIBLE);
                            });
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando JSON", e);
                    }
                }
            }
        });
    }
}
