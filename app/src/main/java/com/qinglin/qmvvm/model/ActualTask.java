package com.qinglin.qmvvm.model;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.qinglin.qmvvm.constants.UrlConstants;
import com.qinglin.qmvvm.model.bean.ActualWeather;
import com.qinglin.qmvvm.network.BaseTask;
import com.qinglin.qmvvm.network.HttpManager;
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

        try {
            Response response = HttpManager.getSync(uri.toString());
            Gson gson = new Gson();
            if (response.code() == 200) {
                ActualWeather weather = gson.fromJson(response.body().string(), ActualWeather.class);
                responseValue.mWeather = weather;
                onSuccess(responseValue);
            } else {
                onError(responseValue, "请求网络失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            onError(responseValue, "请求网络失败,错误："+e.toString());
        }
    }

    public static class RequestValues implements BaseTask.RequestValues {
        public String cityId;
    }

    public static class ResponseValue implements BaseTask.ResponseValue {
        ActualWeather mWeather;
    }
}