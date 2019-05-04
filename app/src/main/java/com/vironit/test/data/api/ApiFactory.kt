package com.vironit.test.data.api

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory{

    private const val BASE_URL = "https://en.wikipedia.org/w/"

    const val COORDINATE_QUERY_PARAM = "ggscoord"
    const val PAGE_TITLE_QUERY_PARAM = "titles"
    const val CONTINUE_QUERY_PARAM = "imcontinue"
    const val PAGE_LIMIT_SIZE = 20

    private const val ACTION_QUERY_PARAM = "action"
    private const val FORMAT_QUERY_PARAM = "format"
    private const val PROP_QUERY_PARAM = "prop"
    private const val RADIUS_QUERY_PARAM = "ggsradius"
    private const val GENERATOR_QUERY_PARAM = "generator"
    private const val PAGES_LIMIT_QUERY_PARAM = "ggslimit"
    private const val IMAGES_LIMIT_QUERY_PARAM = "imlimit"

    private const val ACTION_QUERY_VALUE = "query"
    private const val FORMAT_QUERY_VALUE = "json"
    private const val GENERATOR_QUERY_VALUE = "geosearch"
    private const val PROP_QUERY_VALUE = "images"
    private const val RADIUS_QUERY_VALUE = "1000"

    private val wikiApiClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val url = chain.request().url().newBuilder()
                .addQueryParameter(ACTION_QUERY_PARAM, ACTION_QUERY_VALUE)
                .addQueryParameter(FORMAT_QUERY_PARAM, FORMAT_QUERY_VALUE)
//                .addQueryParameter(GENERATOR_QUERY_PARAM, GENERATOR_QUERY_VALUE)
                .addQueryParameter(PROP_QUERY_PARAM, PROP_QUERY_VALUE)
//                .addQueryParameter(RADIUS_QUERY_PARAM, RADIUS_QUERY_VALUE)
                .addQueryParameter(PAGES_LIMIT_QUERY_PARAM, PAGE_LIMIT_SIZE.toString())
//                .addQueryParameter(IMAGES_LIMIT_QUERY_PARAM, IMAGES_LIMIT_QUERY_VALUE)
                .build()
            chain.proceed(chain.request().newBuilder().url(url).build())
        }
        .writeTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(wikiApiClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val wikiApi: WikiApi = retrofit().create(WikiApi::class.java)
}