package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.WeatherModel;
import com.qinglin.qmvvm.model.bean.ActualWeather;

import androidx.lifecycle.LiveData;

public class WeatherViewModel extends BaseViewModel<WeatherModel> {


    @Override
    WeatherModel createModel() {
        return new WeatherModel(mPresenterTag);
    }

    public LiveData<ActualWeather> getActualWeather() {
        return this.getModel().getActualWeather();
    }

    public void loadData() {
        this.getActualWeather();
    }

    public void changeWeather() {
        this.getModel().changeWeather();
    }
}
