package com.qinglin.qmvvm.model;

import android.util.Log;

import com.qinglin.qmvvm.model.bean.ActualWeather;
import com.qinglin.qmvvm.network.QResponseCallback;
import com.qinglin.qmvvm.network.TaskExecuteScheduler;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WeatherModel implements IModel {

    private MutableLiveData<ActualWeather> actualWeather = new MutableLiveData<>();
    private int index;
    private String mPresenterTag;

    public WeatherModel(String presenterTag) {
        this.mPresenterTag = presenterTag;
    }

    public LiveData<ActualWeather> getActualWeather() {
        this.getWeatherFromNet(mPresenterTag,"707860",new QResponseCallback<ActualTask.ResponseValue>(){

            @Override
            public void onSuccess(ActualTask.ResponseValue responseValue) {
                actualWeather.setValue(responseValue.mWeather);
            }

            @Override
            public void onError(ActualTask.ResponseValue error, String errorMsg) {
                Log.e("hql",errorMsg);
                if (actualWeather != null) {
                    actualWeather.setValue(null);
                }
            }

            @Override
            public void doWhat(ActualTask.ResponseValue doWhat) {

            }
        });
        return actualWeather;
    }

    public void getWeatherFromNet(String tag, String cityId, QResponseCallback<ActualTask.ResponseValue> callback) {
        final ActualTask.RequestValues requestValues = new ActualTask.RequestValues();
        requestValues.cityId = cityId;
        ActualTask task = new ActualTask();
        task.setCancelTask(true);

        TaskExecuteScheduler.getInstance().execute(tag, task, requestValues, callback);
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
    }

}
