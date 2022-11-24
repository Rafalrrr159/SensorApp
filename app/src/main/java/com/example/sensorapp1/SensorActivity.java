package com.example.sensorapp1;

import static com.example.sensorapp1.SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
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

import java.util.Arrays;
import java.util.List;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private RecyclerView recyclerView;
    private SensorAdapter adapter;
    private boolean subtitleVisible;
    public static final String EXTRA_SENSOR_TYPE_PARAMETER = "EXTRA_SENSOR_TYPE";
    private static final String KEY_SUBTITLE_VISIBLE = "subtitleVisible";
    private static final String SENSOR_APP_TAG = "SENSOR_APP_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE_VISIBLE);
        }

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        for (int i = 0; i < sensorList.size(); ++i) {
            Sensor s = sensorList.get(i);
            Log.d(SENSOR_APP_TAG, s.getName() + "; " + s.getVendor() + "; " + s.getMaximumRange());
        }
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
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                invalidateOptionsMenu();
                updateSubtitle();
                return true;
        }
    }

    public void updateSubtitle() {
        String subtitle = null;
        if (subtitleVisible)
            subtitle = getString(R.string.subtitle_format, sensorList.size());
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SUBTITLE_VISIBLE, subtitleVisible);
    }

    private class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView sensorIconImageView;
        private TextView sensorNameTextView;
        private Sensor sensor;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));
            itemView.setOnClickListener(this);

            sensorIconImageView = itemView.findViewById(R.id.sensor_icon);
            sensorNameTextView = itemView.findViewById(R.id.sensor_name);
        }

        public void bind(Sensor sensor) {
            this.sensor = sensor;
            sensorIconImageView.setImageResource(R.drawable.ic_sensor);
            sensorNameTextView.setText(sensor.getName()+" "+sensor.getType());
            View itemContainer = itemView.findViewById(R.id.sensor_list_item);
            View magneticContainer = itemView.findViewById(R.id.sensor_list_item);
            if (sensor.getType() == Sensor.TYPE_LIGHT || sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                sensorNameTextView.setBackgroundColor(Color.rgb(255, 153, 51));
                itemContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivityForResult(intent, 1);
                });
            }
            else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                sensorNameTextView.setBackgroundColor(Color.rgb(51, 255, 255));
                magneticContainer.setOnClickListener(v->{
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETER, sensor.getType());
                    startActivityForResult(intent, 1);
                });
            }
            else
                sensorNameTextView.setBackgroundColor(Color.TRANSPARENT);
        }

        @Override
        public void onClick(View v) {}
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private List<Sensor> sensors;

        public SensorAdapter(List<Sensor> tasks) {
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
            Sensor sensor = sensors.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }
}
