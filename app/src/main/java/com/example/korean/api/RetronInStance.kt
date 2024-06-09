package com.example.korean.api

import com.example.korean.ulti.Contanse.Companion.Main_Url
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetronInStance {
    companion object{
        private val retrofit by lazy {
            val loggin = HttpLoggingInterceptor()
            loggin.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggin)
                .build()
            Retrofit.Builder()
                .baseUrl(Main_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(retrolApi::class.java)
        }
    }
}