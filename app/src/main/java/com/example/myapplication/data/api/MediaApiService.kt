package com.example.myapplication.data.api

import android.R
import com.example.myapplication.data.model.MusicItem
import retrofit2.http.GET
import retrofit2.http.Url

// API 정의 인터페이스

interface MediaApiService{
    @GET
    suspend fun getPlayList(@Url url: R.string): List<MusicItem>
}