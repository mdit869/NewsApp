package com.example.korean.api

import com.example.korean.model.NewsResponse
import com.example.korean.ulti.Contanse.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface retrolApi {

    @GET("v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country")
        codeCountry : String ="us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        api : String = API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchEverything(
        @Query("q") searchQuery: String,
        @Query("page")pageNumber: Int = 1,
        @Query("apiKey") apiKey: String= API_KEY
    ):Response<NewsResponse>
}