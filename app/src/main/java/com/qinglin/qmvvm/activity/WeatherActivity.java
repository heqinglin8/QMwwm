package com.qinglin.qmvvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinglin.qmvvm.R;
import com.qinglin.qmvvm.model.bean.ActualWeather;
import com.qinglin.qmvvm.viewmodel.WeatherViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class WeatherActivity extends AppCompatActivity {

    private TextView mWeather;
    private Button mChange;
    private Button mReload;
    private WeatherViewModel mWeatherViewModel;
    private RelativeLayout mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        initListener();
    }

    private void initListener() {
        mWeatherViewModel.getActualWeather().observe(this, new Observer() {

            @Override
            public void onChanged(Object o) {
                if (o != null && o instanceof ActualWeather) {
                    ActualWeather weather = (ActualWeather) o;
                    mWeather.setText("天气："+weather.getName());
                    mLoading.setVisibility(View.GONE);
                }else {
                    mLoading.setVisibility(View.VISIBLE);
                    Toast.makeText(WeatherActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherViewModel.changeWeather();
            }
        });
        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoading.setVisibility(View.VISIBLE);
                mWeatherViewModel.loadData();
            }
        });
    }

    private void initView() {
        mWeather = findViewById(R.id.weather);
        mChange = findViewById(R.id.change);
        mLoading = findViewById(R.id.loading);
        mReload = findViewById(R.id.reload);

        mWeatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
    }
}
