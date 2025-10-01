package com.example.iot_lab4_20203266;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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

import com.example.iot_lab4_20203266.adapters.ForecastAdapter;
import com.example.iot_lab4_20203266.models.ForecastDayItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForecastFragment extends Fragment implements SensorEventListener {

    private static final String API_KEY = "30a2c3275bc3456ba38160745250305";
    private static final String TAG = "ForecastFragment";

    private EditText etIdLocation, etDays;
    private Button btnBuscar;
    private ForecastAdapter adapter;
    private List<ForecastDayItem> forecastList = new ArrayList<>();

    // 👇 Sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 20.0f; // m/s²
    private long lastShakeTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        etIdLocation = view.findViewById(R.id.etIdLocation);
        etDays = view.findViewById(R.id.etDays);
        btnBuscar = view.findViewById(R.id.btnBuscarForecast);
        RecyclerView rvForecast = view.findViewById(R.id.rvForecast);

        adapter = new ForecastAdapter();
        rvForecast.setLayoutManager(new LinearLayoutManager(getContext()));
        rvForecast.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> {
            String idLoc = etIdLocation.getText().toString().trim();
            String days = etDays.getText().toString().trim();
            if (!idLoc.isEmpty() && !days.isEmpty()) buscarPronostico(idLoc, days);
        });

        // ⚡ Sensor Manager
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return view;
    }

    private void buscarPronostico(String idLocation, String days) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=id:" + idLocation + "&days=" + days;
        Log.d(TAG, "🌐 URL Forecast: " + url);

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
                    Log.d(TAG, "✅ JSON Forecast: " + json);

                    try {
                        Gson gson = new Gson();
                        JsonObject root = gson.fromJson(json, JsonObject.class);

                        String locName = root.getAsJsonObject("location").get("name").getAsString();
                        String locId = idLocation;

                        Type listType = new TypeToken<List<ForecastDayItem>>() {}.getType();
                        forecastList = gson.fromJson(
                                root.getAsJsonObject("forecast").getAsJsonArray("forecastday"),
                                listType
                        );

                        List<ForecastDayItem> finalList = forecastList;
                        requireActivity().runOnUiThread(() -> adapter.updateData(finalList, locName, locId));

                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error parseando JSON", e);
                    }
                }
            }
        });
    }

    // ===== SENSOR LISTENER =====
    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z);

            long currentTime = System.currentTimeMillis();
            if (acceleration > SHAKE_THRESHOLD && (currentTime - lastShakeTime > 2000)) {
                lastShakeTime = currentTime;
                mostrarDialogoConfirmacion();
            }
        }
    }

    private void mostrarDialogoConfirmacion() {
        if (forecastList.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar acción")
                .setMessage("¿Desea eliminar el último pronóstico de la lista?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    forecastList.remove(forecastList.size() - 1);
                    requireActivity().runOnUiThread(() -> adapter.updateData(forecastList, "Localidad", "ID"));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se usa
    }
}
