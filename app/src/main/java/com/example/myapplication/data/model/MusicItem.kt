package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

// 서버에서 받아올 데이터 클래스 (DTO)


data class MusicItem(
    val title: String,
    val artist: String,
    @SerializedName("mediaUrl") val uri: String,
    val isVideo: Boolean = false,
    val subtitleUrl: String? = null
)