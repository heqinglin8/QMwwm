package com.qinglin.qmvvm.model;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.qinglin.qmvvm.constants.UrlConstants;
import com.qinglin.qmvvm.model.bean.ActualWeather;
import com.qinglin.qmvvm.network.BaseTask;
import com.qinglin.qmvvm.utils.UriUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author
 * @date
 */

public class ActualTask extends BaseTask<ActualTask.RequestValues, ActualTask.ResponseValue> {
    private final static String TAG = "ActualTask";

    @Override
    protected void executeTask(RequestValues requestValues) {
        if (requestValues == null) {
            return;
        }
        final ResponseValue responseValue = new ResponseValue();
        //网络请求天气数据
        String cityId = requestValues.cityId;
        Map<String, String> params = new HashMap<>();
        params.put("id", cityId);
        params.put("appid", UrlConstants.WEATHER_API_KEY);
        Uri uri = UriUtils.addParameters(UrlConstants.ACTUAL_WEATHER_URL, params);


        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(uri.toString())
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                onError(responseValue, "请求网络失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                if (response.code() == 200) {
                    ActualWeather weather = gson.fromJson(response.body().string(), ActualWeather.class);
                    responseValue.mWeather = weather;
                    onSuccess(responseValue);
                } else {
                    onError(responseValue, "请求网络失败");
                }
            }
        });

    }

    public static class RequestValues implements BaseTask.RequestValues {
        public String cityId;
    }

    public static class ResponseValue implements BaseTask.ResponseValue {
        ActualWeather mWeather;
    }
}