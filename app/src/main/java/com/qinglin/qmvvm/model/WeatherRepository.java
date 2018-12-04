package com.qinglin.qmvvm.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinglin.qmvvm.model.bean.WeatherBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherRepository {
    private final static String TAG = "WeatherRepository";
    private final static String location = "CN101010100";
    private final static String key = "ea8fe8c7354745fc8410e5b5339d2540";
    private final static String WEATHER_URL = "https://free-api.heweather.com/s6/weather/now?location=" + location + "&key=" + key;
    private Handler mMainHandler;

    public WeatherRepository() {
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadWeather(final NetCallback callback) {

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(WEATHER_URL)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        Log.d(TAG, "json: " + json);
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                        Gson gson = new Gson();
                        final List<WeatherBean> weatherBeans = gson.fromJson(jsonArray.toString(), new TypeToken<List<WeatherBean>>() {
                        }.getType());
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onSuccess(weatherBeans);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure();
                        }
                    });
                }
            }
        });
    }

    public interface NetCallback {
        void onSuccess(Object response);

        void onFailure();
    }


}
