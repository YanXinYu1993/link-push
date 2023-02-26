package com.push.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Objects;

/**
 * @description:
 * @author: Yan XinYu
 */
@Slf4j
public class OkhttpUtils {
    public static final MediaType JSON = MediaType.get("application/json;charset=utf-8");

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(5))
            .callTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(15))
            .build();

    public static void AsyncPost(String url,String body,Callback callback){
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        CLIENT.newCall(request).enqueue(callback);
    }

    public static String post(String url,String body) throws IOException {
        RequestBody requestBody = RequestBody.create(body, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }

}
