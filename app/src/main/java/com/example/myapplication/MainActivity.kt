package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.model.MusicItem
import com.example.myapplication.ui.PlayerScreen
import com.example.myapplication.viewmodel.MediaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = MediaViewModel(this)

        // 초기 샘플 데이터
        val playlist = listOf(
            MusicItem("Sample Music", "Artist A", "android.resource://$packageName/raw/sample", false),
            MusicItem("Video Clip", "Nature", "android.resource://$packageName/raw/video_sample", true),
            MusicItem("Sample Music2", "Nature", "android.resource://$packageName/raw/sample_2", false)
        )
        viewModel.preparePlaylist(playlist)

        setContent {
            Surface(color = Color(0xFF0B0F1A)) {
                PlayerScreen(viewModel, playlist)
            }
        }
    }
}