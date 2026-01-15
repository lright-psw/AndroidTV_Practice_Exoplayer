package com.example.myapplication.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.myapplication.data.model.MusicItem
import com.example.myapplication.ui.components.AudioVisualizer
import com.example.myapplication.ui.components.MediaDisplay
import com.example.myapplication.viewmodel.MediaViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(viewModel: MediaViewModel, playlist: List<MusicItem>) {
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "ProgressAnimation"
    )

    BackHandler(enabled = true) {
        if (viewModel.isPlaylistVisible) {
            // 재생목록이 열려있으면 목록만 닫기
            viewModel.isPlaylistVisible = false
            viewModel.controlTarget = ControlTarget.NEXT
        } else {
            // 3. context를 Activity로 캐스팅하여 종료합니다.
            (context as? Activity)?.finish()
        }
    }

    // 1. 실시간 시간 업데이트 로직
    LaunchedEffect(viewModel.isPlaying) {
        while (viewModel.isPlaying) {
            viewModel.currentPos = viewModel.player.currentPosition
            if (viewModel.duration > 0) {
                viewModel.progress = viewModel.currentPos.toFloat() / viewModel.duration.toFloat()
            }
            delay(500)
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // 애니메이션 스케일값
    val progressScale by animateFloatAsState(if (viewModel.controlTarget == ControlTarget.PROGRESS) 1.05f else 1f)
    val prevScale by animateFloatAsState(if (viewModel.controlTarget == ControlTarget.PREV) 1.2f else 1f)
    val playScale by animateFloatAsState(if (viewModel.controlTarget == ControlTarget.PLAY) 1.2f else 1f)
    val nextScale by animateFloatAsState(if (viewModel.controlTarget == ControlTarget.NEXT) 1.2f else 1f)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B0F1A))){
        if(!playlist[viewModel.currentTrackIndex].isVideo){
            AudioVisualizer(isPlaying = viewModel.isPlaying)
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(listOf(
                        Color(0xFF1E243D).copy(alpha = 0.6f),
                        Color(0xFF0B0F1A).copy(alpha = 0.8f)
                    ), radius = 2500f)
                )
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.key == Key.Back || event.key == Key.Escape) {
                        return@onKeyEvent false
                    }
                    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                    handleKeyEvent(event, viewModel, playlist)
                    true
                }
        ) {
            // [왼쪽 영역: 플레이어 메인]
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("NOW PLAYING", color = Color(0xFF6C7CFF), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // 미디어 디스플레이 (기존 MediaDisplay 컴포넌트 호출)
                MediaDisplay(playlist[viewModel.currentTrackIndex].isVideo, viewModel.player)

                Spacer(modifier = Modifier.height(16.dp))
                Text(playlist[viewModel.currentTrackIndex].title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(playlist[viewModel.currentTrackIndex].artist, color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))

                // 2. 프로그레스 바 및 시간 표시
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.scale(progressScale)) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .width(400.dp)
                            .height(if (viewModel.controlTarget == ControlTarget.PROGRESS) 10.dp else 4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF6C7CFF),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Row(
                        modifier = Modifier.width(400.dp).padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTime(viewModel.currentPos), color = Color.Gray, fontSize = 11.sp)
                        Text(formatTime(viewModel.duration), color = Color.Gray, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 컨트롤 버튼
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Icon(Icons.Default.SkipPrevious, null, modifier = Modifier.size(40.dp).scale(prevScale), tint = if(viewModel.controlTarget == ControlTarget.PREV) Color(0xFF6C7CFF) else Color.White)
                    Surface(modifier = Modifier.size(64.dp).scale(playScale), shape = CircleShape, color = if (viewModel.controlTarget == ControlTarget.PLAY) Color(0xFF6C7CFF) else Color(0xFF161B2E), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))) {
                        Box(contentAlignment = Alignment.Center) { Icon(if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp)) }
                    }
                    Icon(Icons.Default.SkipNext, null, modifier = Modifier.size(40.dp).scale(nextScale), tint = if(viewModel.controlTarget == ControlTarget.NEXT) Color(0xFF6C7CFF) else Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 1. 하단 안내 문구 복구
                Text(
                    text = if (!viewModel.isPlaylistVisible) "←→ 탐색  •  오른쪽(>) 끝에서 재생목록 열기" else "← 왼쪽으로 재생목록 닫기",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp
                )
            }

            // [오른쪽 영역: 재생목록]
            AnimatedVisibility(visible = viewModel.isPlaylistVisible, enter = expandHorizontally(), exit = shrinkHorizontally()) {
                Column(modifier = Modifier.width(300.dp).fillMaxHeight().background(Color.Black.copy(alpha = 0.4f)).padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlaylistPlay, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PLAYLIST", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(playlist) { index, item ->
                            val isCurrent = index == viewModel.currentTrackIndex
                            val isFocused = viewModel.controlTarget == ControlTarget.PLAYLIST && index == viewModel.focusedPlaylistIndex
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (isFocused) Color(0xFF6C7CFF).copy(alpha = 0.4f) else if (isCurrent) Color(0xFF6C7CFF).copy(alpha = 0.15f) else Color.Transparent),
                                border = if (isFocused) BorderStroke(2.dp, Color(0xFF6C7CFF)) else null,
                                modifier = Modifier.fillMaxWidth().scale(if(isFocused) 1.05f else 1f)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(if(item.isVideo) Icons.Default.Videocam else Icons.Default.Audiotrack, null, tint = if(isCurrent) Color(0xFF6C7CFF) else Color.Gray, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                                        Text(item.artist, color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

// 헬퍼 함수: 시간 포맷
fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

// 3. 리모컨 키 이벤트 핸들러 (탐색 기능 포함)
private fun handleKeyEvent(event: KeyEvent, viewModel: MediaViewModel, playlist: List<MusicItem>) {
    val SEEK_INTERVAL = 10_000L
    when (event.key) {
        Key.DirectionUp -> {
            if (viewModel.controlTarget == ControlTarget.PLAYLIST) { if (viewModel.focusedPlaylistIndex > 0) viewModel.focusedPlaylistIndex-- }
            else viewModel.controlTarget = ControlTarget.PROGRESS
        }
        Key.DirectionDown -> {
            if (viewModel.controlTarget == ControlTarget.PLAYLIST) { if (viewModel.focusedPlaylistIndex < playlist.size - 1) viewModel.focusedPlaylistIndex++ }
            else viewModel.controlTarget = ControlTarget.PLAY
        }
        Key.DirectionRight -> {
            when (viewModel.controlTarget) {
                ControlTarget.PROGRESS -> viewModel.seekBy(SEEK_INTERVAL)
                ControlTarget.PREV -> viewModel.controlTarget = ControlTarget.PLAY
                ControlTarget.PLAY -> viewModel.controlTarget = ControlTarget.NEXT
                ControlTarget.NEXT -> { viewModel.isPlaylistVisible = true; viewModel.controlTarget = ControlTarget.PLAYLIST }
                else -> {}
            }
        }
        Key.DirectionLeft -> {
            when (viewModel.controlTarget) {
                ControlTarget.PROGRESS -> viewModel.seekBy(-SEEK_INTERVAL)
                ControlTarget.PLAYLIST -> { viewModel.isPlaylistVisible = false; viewModel.controlTarget = ControlTarget.NEXT }
                ControlTarget.NEXT -> viewModel.controlTarget = ControlTarget.PLAY
                ControlTarget.PLAY -> viewModel.controlTarget = ControlTarget.PREV
                else -> {}
            }
        }
        Key.Enter, Key.DirectionCenter, Key.NumPadEnter -> {
            when (viewModel.controlTarget) {
                ControlTarget.PLAY -> viewModel.togglePlay()
                ControlTarget.PREV -> viewModel.player.seekToPrevious()
                ControlTarget.NEXT -> viewModel.player.seekToNext()
                ControlTarget.PLAYLIST ->
                    viewModel.player.seekTo(viewModel.focusedPlaylistIndex, 0L)
                else -> {}
            }
        }
    }
}