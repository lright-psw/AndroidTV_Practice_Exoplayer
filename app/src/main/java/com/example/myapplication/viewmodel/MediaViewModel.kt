package com.example.myapplication.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.myapplication.data.model.MusicItem
import com.example.myapplication.ui.ControlTarget

class MediaViewModel(context: Context) : ViewModel(){
    val player = ExoPlayer.Builder(context).build()

    var controlTarget by mutableStateOf(ControlTarget.PROGRESS)
    var isPlaying by mutableStateOf(false)
    var progress by mutableStateOf(0f)
    var currentPos by mutableLongStateOf(0L)
    var duration by mutableLongStateOf(0L)
    var currentTrackIndex by mutableIntStateOf(0)
    var isPlaylistVisible by mutableStateOf(false)
    var focusedPlaylistIndex by mutableIntStateOf(0)


    fun preparePlaylist(playlist : List<MusicItem>){
        val mediaItems = playlist.map { item ->
            MediaItem.Builder()
                .setUri(Uri.parse(item.uri))
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(item.title)
                        .setArtist(item.artist)
                        .build()
                )
                .build()
        }
        player.setMediaItems(mediaItems)
        player.prepare()

        player.addListener(object: Player.Listener{
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentTrackIndex = player.currentMediaItemIndex
                currentPos = 0L
                progress = 0f

                // 재생목록에서 선택 중이 아니라면 포커스 인덱스도 동기화
                if (controlTarget != ControlTarget.PLAYLIST) {
                    focusedPlaylistIndex = currentTrackIndex
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = player.duration
                }
            }
        })
    }

    fun togglePlay() {
        if (player.isPlaying)
            player.pause()
        else
            player.play()
    }

    fun seekBy(delta:Long){
        if (player.duration <=0)
            return
        val newPos = (player.currentPosition + delta).coerceIn(0,player.duration)
        player.seekTo(newPos)
        currentPos = newPos
        progress = newPos.toFloat() / player.duration.toFloat()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    fun playTrackAt(index: Int) {
        if (index in 0 until player.mediaItemCount) {
            player.seekTo(index, 0L)
            player.play() // 이동 후 즉시 재생 시도
        }
    }
}