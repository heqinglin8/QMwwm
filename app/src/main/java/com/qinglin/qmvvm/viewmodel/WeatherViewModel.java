package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.bean.WeatherBean;
import com.qinglin.qmvvm.model.WeatherRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeatherViewModel extends ViewModel {

    private MutableLiveData<List<WeatherBean>> weatherData;
    private WeatherRepository weatherRepo;
    private List<WeatherBean> mWeatherList;
    private int index;

    public WeatherViewModel(WeatherRepository weatherRepo) {
        this.weatherRepo = weatherRepo;
    }

    public LiveData<List<WeatherBean>> getWeatherBean() {
        if (weatherData == null) {
            weatherData = new MutableLiveData<>();
            loadingData();
        }
        return weatherData;
    }

    public void loadingData() {
        if (weatherRepo != null) {
            weatherRepo.loadWeather(new WeatherRepository.NetCallback() {
                @Override
                public void onSuccess(Object response) {
                    mWeatherList = (List<WeatherBean>) response;
                    weatherData.setValue(mWeatherList);
                }

                @Override
                public void onFailure() {
                    weatherData.setValue(null);
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
