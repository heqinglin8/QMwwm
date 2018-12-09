package com.qinglin.qmvvm.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager {

    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");
    private static final String BASE_URL = "http://xxx.com/openapi";//请求接口根地址
    private static final String TAG = "HttpManager";
    private OkHttpClient client;
    private static HttpManager mHttpManager;
    private Handler mHandler;
    private HttpConfig httpConfig;

    /**
     * 单例获取 HttpManager实例
     */
    private static HttpManager getInstance() {
        if (mHttpManager == null) {
            mHttpManager = new HttpManager();
        }
        return mHttpManager;
    }

    private HttpManager() {
        setHttpConfig(new HttpConfig());
        mHandler = new Handler(Looper.getMainLooper());
    }


    /**************
     * 内部逻辑处理
     ****************/
    private Response p_getSync(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    private String p_getSyncAsString(String url) throws IOException {
        return p_getSync(url).body().string();
    }

    private void p_getAsync(String url, final DataCallBack callBack) {
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String result = response.body().string();
                    deliverDataSuccess(result, callBack);
                } catch (IOException e) {
                    deliverDataFailure(call, e, callBack);
                }
            }
        });
    }

    private void p_postAsync(String url, Map<String, String> params, final DataCallBack callBack) {
        RequestBody requestBody = null;

        if (params == null) {
            params = new HashMap<String, String>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().toString();
            String value = null;
            if (entry.getValue() == null) {
                value = "";
            } else {
                value = entry.getValue().toString();
            }
            builder.add(key, value);
        }
        requestBody = builder.build();
        final Request request = new Request.Builder().url(url).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(call, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    deliverDataSuccess(result, callBack);
                } catch (IOException e) {
                    deliverDataFailure(call, e, callBack);
                }
            }
        });
    }

    /**
     * 数据分发的方法
     */
    private void deliverDataFailure(final Call call, final IOException e, final DataCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.requestFailure(call, e);
                }
            }
        });
    }

    private void deliverDataSuccess(final String result, final DataCallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.requestSuccess(result);
                }
            }
        });
    }


    /******************
     * 对外公布的方法
     *****************/
    public static Response getSync(String url) throws IOException {
        return getInstance().p_getSync(url);//同步GET，返回Response类型数据
    }


    public static String getSyncAsString(String url) throws IOException {
        return getInstance().p_getSyncAsString(url);//同步GET，返回String类型数据
    }

    public static void getAsync(String url, DataCallBack callBack) {
        getInstance().p_getAsync(url, callBack);//异步GET请求
    }

    public static void postAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().p_postAsync(url, params, callBack);//异步POST请求
    }

    /**
     * 数据回调接口
     */
    public interface DataCallBack {
        void requestFailure(Call call, IOException e);

        void requestSuccess(String result);
    }

    /**
     * 设置配置信息 这个方法必需要调用一次
     *
     * @param httpConfig
     */
    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        client = new OkHttpClient.Builder()
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * 上传文件
     *
     * @param actionUrl 接口地址
     * @param filePath  本地文件地址
     */
    public void upLoadFile(String actionUrl, String filePath, final DataCallBack callBack) {
        //补全请求地址
        String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
        //创建File
        File file = new File(filePath);
        //创建RequestBody
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, file);
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        final Call call = client.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, e.toString());
                deliverDataFailure(call, e, callBack);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
//                    Log.e(TAG, "response ----->" + string);
                    deliverDataSuccess(string, callBack);
                } else {
                    deliverDataFailure(call, new IOException("上传失败"), callBack);
                }
            }
        });
    }

    /**
     * 上传文件
     *
     * @param actionUrl 接口地址
     * @param paramsMap 参数
     * @param callBack  回调
     * @param <T>
     */
    public void upLoadFile(String actionUrl, HashMap<String, Object> paramsMap, final DataCallBack callBack) {
        try {
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(requestUrl).post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = client.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    deliverDataFailure(call, e, callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
//                        Log.e(TAG, "response ----->" + string);
                        deliverDataSuccess(string, callBack);
                    } else {
                        deliverDataFailure(call, new IOException("上传失败"), callBack);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
