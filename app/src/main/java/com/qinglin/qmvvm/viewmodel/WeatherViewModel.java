package com.qinglin.qmvvm.viewmodel;

import com.qinglin.qmvvm.model.WeatherModel;
import com.qinglin.qmvvm.model.bean.WeatherBean;

import java.util.List;

import androidx.lifecycle.LiveData;

public class WeatherViewModel extends BaseViewModel {

    private LiveData<List<WeatherBean>> weather;

    @Override
    WeatherModel createModel() {
        return new WeatherModel();
    }

   void init(){
        if(weather == null){
          return;
        }
       weather = getModel()
   }

}
