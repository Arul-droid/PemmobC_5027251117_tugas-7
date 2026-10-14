package com.nbs.app.sensor;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscope;

    private TextView tvSensor;
    private TextView tvX;
    private TextView tvY;
    private TextView tvZ;
    private TextView tvMotion;
    private TextView tvStatus;
    private Button btnToggle;

    private boolean monitoringEnabled = true;
    private boolean listenerRegistered = false;
    private boolean activityResumed = false;

    // Ambang untuk demo deteksi rotasi, dalam rad/s.
    private static final float ROTATION_THRESHOLD = 0.15f;
    private static final String STATE_MONITORING = "monitoringEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWindowInsets();

        tvSensor = findViewById(R.id.tvSensor);
        tvX = findViewById(R.id.tvX);
        tvY = findViewById(R.id.tvY);
        tvZ = findViewById(R.id.tvZ);
        tvMotion = findViewById(R.id.tvMotion);
        tvStatus = findViewById(R.id.tvStatus);
        btnToggle = findViewById(R.id.btnToggle);

        if (savedInstanceState != null) {
            monitoringEnabled = savedInstanceState.getBoolean(
                    STATE_MONITORING,
                    true
            );
        }

        sensorManager = (SensorManager)
                getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            gyroscope = sensorManager.getDefaultSensor(
                    Sensor.TYPE_GYROSCOPE
            );
        }

        if (gyroscope == null) {
            tvSensor.setText("Gyroscope tidak tersedia");
            tvStatus.setText("HP ini tidak memiliki sensor gyroscope.");
            tvMotion.setText("Sensor tidak didukung");
            btnToggle.setEnabled(false);
        } else {
            tvSensor.setText("Sensor: " + gyroscope.getName());
        }

        updateButton();

        btnToggle.setOnClickListener(view -> {
            monitoringEnabled = !monitoringEnabled;

            if (monitoringEnabled && activityResumed) {
                startMonitoring();
            } else {
                stopMonitoring();
                tvStatus.setText("Status: dijeda");
                tvMotion.setText("Pembacaan dihentikan");
            }

            updateButton();
        });
    }

    @SuppressWarnings("deprecation")
    private void setupWindowInsets() {
        View root = findViewById(R.id.rootLayout);

        final int initialLeft = root.getPaddingLeft();
        final int initialTop = root.getPaddingTop();
        final int initialRight = root.getPaddingRight();
        final int initialBottom = root.getPaddingBottom();

        // Menjaga tampilan agar tidak tertutup status/navigation bar.
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(
                    initialLeft + insets.getSystemWindowInsetLeft(),
                    initialTop + insets.getSystemWindowInsetTop(),
                    initialRight + insets.getSystemWindowInsetRight(),
                    initialBottom + insets.getSystemWindowInsetBottom()
            );

            return insets;
        });

        root.requestApplyInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed = true;

        if (monitoringEnabled) {
            startMonitoring();
        } else if (gyroscope != null) {
            tvStatus.setText("Status: dijeda");
        }
    }

    @Override
    protected void onPause() {
        activityResumed = false;
        stopMonitoring();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_MONITORING, monitoringEnabled);
        super.onSaveInstanceState(outState);
    }

    private void startMonitoring() {
        if (sensorManager == null
                || gyroscope == null
                || listenerRegistered) {
            return;
        }

        listenerRegistered = sensorManager.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_UI
        );

        if (listenerRegistered) {
            tvStatus.setText("Status: aktif — putar HP untuk mencoba");
            tvMotion.setText("Menunggu data sensor...");
        } else {
            tvStatus.setText("Status: gagal mengaktifkan sensor");
            tvMotion.setText("Data sensor tidak tersedia");
        }
    }

    private void stopMonitoring() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        listenerRegistered = false;
    }

    private void updateButton() {
        btnToggle.setText(monitoringEnabled ? "Jeda" : "Mulai");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_GYROSCOPE
                || !monitoringEnabled
                || !listenerRegistered) {
            return;
        }

        // Kecepatan rotasi pada tiap sumbu, dalam rad/s.
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        tvX.setText(formatReading("X", x));
        tvY.setText(formatReading("Y", y));
        tvZ.setText(formatReading("Z", z));

        // Besar kecepatan rotasi gabungan dari ketiga sumbu.
        double rotationSpeed = Math.sqrt(
                x * x + y * y + z * z
        );

        if (rotationSpeed > ROTATION_THRESHOLD) {
            tvMotion.setText("Rotasi terdeteksi");
            tvMotion.setTextColor(Color.rgb(180, 83, 9));
        } else {
            tvMotion.setText("Rotasi sangat kecil / HP diam");
            tvMotion.setTextColor(Color.rgb(21, 128, 61));
        }
    }

    private String formatReading(String axis, float value) {
        return String.format(
                Locale.US,
                "%s: %.3f rad/s",
                axis,
                value
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Tidak diperlukan untuk tampilan demo ini.
    }
}