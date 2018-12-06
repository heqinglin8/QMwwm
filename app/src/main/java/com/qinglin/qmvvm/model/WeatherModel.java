package com.qinglin.qmvvm.model;

import com.qinglin.qmvvm.model.bean.WeatherBean;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WeatherModel implements IModel {

    private MutableLiveData<List<WeatherBean>> weatherData;
    private WeatherRepository weatherRepo;
    private List<WeatherBean> mWeatherList;
    private int index;

    public WeatherModel() {
        this.weatherRepo = new WeatherRepository();
    }

    public LiveData<List<WeatherBean>> getWeather() {
        if (weatherData == null) {
            weatherData = new MutableLiveData<>();
            loadData();
        }
        return weatherData;
    }

    public void loadData() {
        if (weatherRepo != null) {
            weatherRepo.loadWeather(new WeatherRepository.NetCallback() {
                @Override
                public void onSuccess(Object response) {
                    mWeatherList = (List<WeatherBean>) response;
                    if (weatherData != null) {
                        weatherData.setValue(mWeatherList);
                    }
                }

                @Override
                public void onFailure() {
                    if (weatherData != null) {
                        weatherData.setValue(null);
                    }
                }
            });
        }
    }

    public void changeWeather() {
        index++;
        String text;
        switch (index) {
            case 1:
                text = "下雨";
                break;
            case 2:
                text = "大风";
                break;
            case 3:
                text = "炎热";
                break;
            default:
                text = "晴朗";
                index = 0;
                break;
        }
        if (mWeatherList != null && mWeatherList.size() > 0) {
            mWeatherList.get(0).now.cond_txt = text;
            weatherData.setValue(mWeatherList);
        }
    }

}
