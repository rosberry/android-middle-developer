package ru.skillbranch.di.root

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.skillbranch.AppConfig
import ru.skillbranch.BuildConfig
import ru.skillbranch.data.api.IceAndFireApi
import ru.skillbranch.data.interceptors.ClientInfoInterceptor
import ru.skillbranch.data.interceptors.CurlLoggingInterceptor
import java.util.concurrent.TimeUnit


/**
 * @author neestell on 2019-12-08.
 */

object NetworkModule {
    private val gson: Gson by lazy { Gson() }
    private val gsonConverterFactory: GsonConverterFactory by lazy { GsonConverterFactory.create(gson) }
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(ClientInfoInterceptor())
            .apply {
                if (BuildConfig.DEBUG) {
                    addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                    addInterceptor(CurlLoggingInterceptor())
                }
            }
            .build()
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    val api: IceAndFireApi by lazy { retrofit.create(IceAndFireApi::class.java) }
}
