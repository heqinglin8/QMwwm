package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.WeatherModel;
import com.qinglin.qmvvm.model.bean.WeatherBean;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WeatherViewModel extends BaseViewModel<WeatherModel> {

    private LiveData<List<WeatherBean>> weathers;

    @Override
    WeatherModel createModel() {
        return new WeatherModel();
    }

    public void init() {
        if (weathers != null) {
            return;
        }
        weathers = this.getModel().getWeathers();
    }

    public LiveData<List<WeatherBean>> getWeathers() {
        return weathers;
    }

    public LiveData<WeatherBean> getFirstWeather() {
        return this.getModel().getFirstWeather();
    }

    public void loadData() {
        this.getModel().loadData();
    }

    public void changeWeather() {
        this.getModel().changeWeather();
    }
}
