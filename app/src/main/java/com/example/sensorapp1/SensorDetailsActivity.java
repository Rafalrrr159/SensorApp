package com.example.sensorapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_SENSOR_TYPE_PARAMETER = "EXTRA_SENSOR_TYPE";
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private TextView sensorLightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        sensorLightTextView = findViewById(R.id.sensor_light_label);
        int index = getIntent().getIntExtra(EXTRA_SENSOR_TYPE_PARAMETER, Sensor.TYPE_ACCELEROMETER);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(index);
        if (sensorLight == null) {
            sensorLightTextView.setText(R.string.missing_sensor);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sensorLight != null) {
            sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                sensorLightTextView.setText(getResources().getString(R.string.light_sensor_label, sensorEvent.values[0]));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorLightTextView.setText(getResources().getString(R.string.accelerometer_sensor_label, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
                break;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("AccuracyTag", "Work");
    }
}