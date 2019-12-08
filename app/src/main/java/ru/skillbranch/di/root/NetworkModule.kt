package ru.skillbranch.di.root

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.skillbranch.BuildConfig
import ru.skillbranch.data.api.IceAndFireApi
import ru.skillbranch.data.interceptors.ClientInfoInterceptor
import ru.skillbranch.data.interceptors.CurlLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * @author neestell on 2019-12-08.
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
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

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.anapioficeandfire.com/api")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideApi(retrofit: Retrofit): IceAndFireApi {
        return retrofit.create(IceAndFireApi::class.java)
    }
}