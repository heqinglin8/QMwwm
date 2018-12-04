package com.qinglin.qmvvm.model.bean;

public class WeatherBean {

    public WeatherBasicBean basic;
    public WeatherNowBean now;

    public WeatherBasicBean getBasic() {
        return basic;
    }

    public void setBasic(WeatherBasicBean basic) {
        this.basic = basic;
    }

    public WeatherNowBean getNow() {
        return now;
    }

    public void setNow(WeatherNowBean now) {
        this.now = now;
    }
}
