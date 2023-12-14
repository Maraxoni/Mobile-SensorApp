package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorActivity extends AppCompatActivity {
    String subtitle;
    private SensorAdapter adapter;
    private List<SensorEntity> sensorList;
    private boolean subtitleVisible;
    private static final String KEY_SUBTITLE_VISIBLE = "subtitleVisible";
    public static final String SENSOR_APP_TAG = "SensorActivity";
    public static final String KEY_EXTRA_SENSOR_INDEX = "SensorActivity.sensor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE_VISIBLE);
        }

        RecyclerView recyclerView = findViewById(R.id.sensor_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = new ArrayList<>();

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 0; i < sensors.size(); ++i) {
            Sensor s = sensors.get(i);
            Log.d(SENSOR_APP_TAG, s.getName() + "; " + s.getVendor() + "; " + s.getMaximumRange());
            sensorList.add(new SensorEntity(s, i, false));
        }
        sensorList.get(0).setSelectable(true);
        sensorList.get(1).setSelectable(true);
        sensorList.get(3).setSelectable(true);

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.show_subtitle) {
            subtitleVisible = !subtitleVisible;
            invalidateOptionsMenu();
            updateSubtitle();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateSubtitle() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (subtitleVisible) {
                subtitle = getString(R.string.subtitle_format, sensorList.size());
            }
            actionBar.setSubtitle(subtitle);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SUBTITLE_VISIBLE, subtitleVisible);
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private final List<SensorEntity> sensors;

        public SensorAdapter(List<SensorEntity> tasks) {
            this.sensors = tasks;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(SensorActivity.this);
            return new SensorHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            SensorEntity sensor = sensors.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }
    private class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView iconImageView;
        private final TextView nameTextView;
        private SensorEntity sensor;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));
            itemView.setOnClickListener(this);

            iconImageView = itemView.findViewById(R.id.sensor_icon);
            nameTextView = itemView.findViewById(R.id.sensor_name);
        }

        public void bind(SensorEntity sensor) {
            this.sensor = sensor;
            iconImageView.setImageResource(R.drawable.ic_sensor_foreground);
            nameTextView.setText(sensor.getSensor().getName());
            if (sensor.isSelectable())
                nameTextView.setBackgroundColor(Color.rgb(200, 0, 200));
            else
                nameTextView.setBackgroundColor(Color.TRANSPARENT);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
            intent.putExtra(KEY_EXTRA_SENSOR_INDEX, sensor.getIndex());
            startActivity(intent);
        }
    }
}