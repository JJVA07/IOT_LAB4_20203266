package com.example.iot_lab4_20203266;

import android.os.Bundle;
import android.util.Log; // 👈 para logs
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20203266.LocationAdapter;
import com.example.iot_lab4_20203266.models.LocationItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationFragment extends Fragment {

    private static final String API_KEY = "30a2c3275bc3456ba38160745250305";
    private static final String TAG = "LocationFragment"; // 👈 etiqueta para logs

    private EditText etSearch;
    private Button btnBuscar;
    private LocationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        btnBuscar = view.findViewById(R.id.btnBuscar);
        RecyclerView rvLocations = view.findViewById(R.id.rvLocations);

        adapter = new LocationAdapter();
        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLocations.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim().toLowerCase();
            Log.d(TAG, "🔎 Texto ingresado: " + query);
            if (!query.isEmpty()) buscarLocaciones(query);
            else Log.w(TAG, "⚠️ El campo de búsqueda está vacío");
        });

        return view;
    }

    private void buscarLocaciones(String query) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.weatherapi.com/v1/search.json?key=" + API_KEY + "&q=" + query;
        Log.d(TAG, "🌐 URL de consulta: " + url);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "❌ Error en la petición HTTP", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "📡 Código de respuesta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Log.d(TAG, "✅ Respuesta JSON: " + json);

                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<LocationItem>>() {}.getType();
                        List<LocationItem> results = gson.fromJson(json, listType);

                        if (results != null) {
                            Log.d(TAG, "📋 Número de resultados: " + results.size());
                        } else {
                            Log.w(TAG, "⚠️ No se pudo parsear el JSON");
                        }

                        requireActivity().runOnUiThread(() -> adapter.updateData(results));

                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando JSON", e);
                    }
                } else {
                    Log.w(TAG, "⚠️ Respuesta no exitosa. Código: " + response.code());
                }
            }
        });
    }
}
