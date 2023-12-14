package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sensorapp.R;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorTextView;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        sensorTextView = findViewById(R.id.sensor_label);
        root = sensorTextView.getRootView();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        int index = getIntent().getIntExtra(SensorActivity.KEY_EXTRA_SENSOR_INDEX, -1);
        if (index == 0 || index == 3)
            sensor = sensorManager.getSensorList(Sensor.TYPE_ALL).get(index);
        else if (index == 1){
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        } else {
            sensor = null;
            sensorTextView.setText(R.string.missing_sensor);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sensor != null)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] v = event.values;
        switch (sensorType) {
            case Sensor.TYPE_LIGHT: {
                sensorTextView.setText(getResources().getString(R.string.light_sensor_label, v[0]));
                int color = Color.rgb((int) v[0], 0, 0);
                root.setBackgroundColor(color);
                break;
            }
            case Sensor.TYPE_ACCELEROMETER: {
                sensorTextView.setText(getResources().getString(R.string.accelerometer_sensor_label, v[0], v[1], v[2]));
                int r = (int) (255 * (Math.abs(v[0]) / 78.4532));
                int g = (int) (255 * (Math.abs(v[1]) / 78.4532));
                int b = (int) (255 * (Math.abs(v[2]) / 78.4532));
                root.setBackgroundColor(Color.rgb(r, g, b));
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(SensorActivity.SENSOR_APP_TAG, "wywołano onAccuracyChanged");
    }
}