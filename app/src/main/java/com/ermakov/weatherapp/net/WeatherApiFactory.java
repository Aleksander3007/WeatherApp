package com.ermakov.weatherapp.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс отвечает за работу с объектами для OpenWeatherMap API.
 */
public abstract class WeatherApiFactory {
    public static final String BASE_URL = "http://api.openweathermap.org/";
    public static final String API_KEY = "APPID";
    public static final String API_KEY_VALUE = "46d68e219e534a8169f27c7aa20b02da";

    public static final String URL_ICON = BASE_URL + "img/w/";

    private static final int CONNECT_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 30;

    private static OkHttpClient.Builder sHttpClientBuilder = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);

    private static Retrofit.Builder sRetrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static HttpLoggingInterceptor sLogging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.NONE);

    static {
        sHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                HttpUrl originalHttpUrl = originalRequest.url();

                HttpUrl httpUrl = originalHttpUrl.newBuilder()
                        .addQueryParameter(API_KEY, API_KEY_VALUE)
                        .build();

                Request request = originalRequest.newBuilder()
                        .url(httpUrl)
                        .build();

                return chain.proceed(request);
            }
        });
    }

    public static WeatherApi createWeatherApiService() {
        return createRetrofit().create(WeatherApi.class);
    }

    private static Retrofit createRetrofit() {
        if (!sHttpClientBuilder.interceptors().contains(sLogging)) {
            sHttpClientBuilder.addInterceptor(sLogging);
        };
        return sRetrofitBuilder
                .client(sHttpClientBuilder.build())
                .build();
    }

    /**
     * Сформировать путь до иконки погоды.
     * @param iconId id иконки погоды.
     * @return путь до иконки погоды.
     */
    public static String createUrlToIcon(String iconId) {
        return URL_ICON + iconId;
    }
}
