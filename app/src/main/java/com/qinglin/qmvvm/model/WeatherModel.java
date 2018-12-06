package com.qinglin.qmvvm.model;

import com.qinglin.qmvvm.model.bean.WeatherBean;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WeatherModel implements IModel {

    private MutableLiveData<List<WeatherBean>> weathers;
    MutableLiveData<WeatherBean> mFirstWeather = new MutableLiveData();
    private WeatherRepository weatherRepo;
    private List<WeatherBean> mWeatherList;
    private int index;

    public WeatherModel() {
        this.weatherRepo = new WeatherRepository();
    }

    public LiveData<List<WeatherBean>> getWeathers() {
        if (weathers == null) {
            weathers = new MutableLiveData<>();
            loadData();
        }
        return weathers;
    }

    public MutableLiveData<WeatherBean> getFirstWeather() {
        return mFirstWeather;
    }

    public void loadData() {
        if (weatherRepo != null) {
            weatherRepo.loadWeather(new WeatherRepository.NetCallback() {
                @Override
                public void onSuccess(Object response) {
                    mWeatherList = (List<WeatherBean>) response;
                    if (weathers != null) {
                        weathers.setValue(mWeatherList);
                    }
                    if (mWeatherList != null && mWeatherList.size() > 0) {
                        mFirstWeather.setValue(mWeatherList.get(0));
                    }
                }

                @Override
                public void onFailure() {
                    if (weathers != null) {
                        weathers.setValue(null);
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
            mFirstWeather.setValue(mWeatherList.get(0));
        }
    }

}
