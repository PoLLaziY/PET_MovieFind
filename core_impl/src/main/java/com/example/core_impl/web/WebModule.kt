package com.example.core_impl.web

import com.example.core_api.WebService
import com.example.core_impl.web.retrofit.RetrofitAPI
import com.example.domain.ConstForRestAPI
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class WebModule {

    @Provides
    @Singleton
    fun provideWebService(retrofitAPI: RetrofitAPI): WebService =
        RetrofitWebService(retrofitAPI)

    @Provides
    @Singleton
    fun provideRetrofitAPI(client: OkHttpClient): RetrofitAPI = Retrofit.Builder()
        .baseUrl(ConstForRestAPI.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RetrofitAPI::class.java)

    @Provides
    @Singleton
    fun provideClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()

    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BASIC }
}