package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun MediaDisplay(isVideo: Boolean,player: ExoPlayer){
    Box(
        modifier = Modifier
            .size(200.dp)
            .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF6C7CFF).copy(alpha = 0.3f))
            .background(Color(0xFF161B2E), RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ){
        if(isVideo){
            AndroidView(
                factory = {context ->
                    PlayerView(context).apply{
                        this.player = player
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        else{
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color(0xFF6C7CFF).copy(alpha = 0.2f)
            )
        }
    }
}