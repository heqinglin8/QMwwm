package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.WeatherModel;
import com.qinglin.qmvvm.model.bean.WeatherBean;

import java.util.List;

import androidx.lifecycle.LiveData;

public class WeatherViewModel extends BaseViewModel<WeatherModel> {

    private LiveData<List<WeatherBean>> weather;

    @Override
    WeatherModel createModel() {
        return new WeatherModel();
    }

    public void init() {
        if (weather != null) {
            return;
        }
        weather = this.getModel().getWeather();
    }

    public LiveData<List<WeatherBean>> getWeather() {
        return weather;
    }

    public void loadData() {
        this.getModel().loadData();
    }

    public void changeWeather() {
        this.getModel().changeWeather();
    }
}
