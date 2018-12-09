package com.qinglin.qmvvm.utils;

import android.net.Uri;

import java.net.URL;
import java.util.Map;

public class UriUtils {

    public static Uri addParameters(String baseUrl, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            builder.appendQueryParameter(entry.getKey(),entry.getValue());
        }
       return builder.build();
    }

}
