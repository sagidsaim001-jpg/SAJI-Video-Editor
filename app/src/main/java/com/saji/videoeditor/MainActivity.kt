package com.saji.videoeditor

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Film
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import java.io.File

private val Ink = Color(0xFF080B12)
private val Panel = Color(0xFF141A26)
private val Panel2 = Color(0xFF1C2433)
private val Accent = Color(0xFF8B7CFF)
private val Muted = Color(0xFF929BAD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(8, 11, 18)
        window.navigationBarColor = android.graphics.Color.rgb(8, 11, 18)
        setContent { SajiTheme { SajiEditorApp() } }
    }
}

@Composable
private fun SajiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = Accent,
            background = Ink,
            surface = Panel,
            onPrimary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

@Composable
private fun SajiEditorApp() {
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) videoUri = uri
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
        if (videoUri == null) {
            HomeScreen(
                onNewProject = { picker.launch(arrayOf("video/*")) },
                onImport = { picker.launch(arrayOf("video/*")) }
            )
        } else {
            EditorScreen(
                uri = videoUri!!,
                onBack = { videoUri = null },
                onReplace = { picker.launch(arrayOf("video/*")) }
            )
        }
    }
}

@Composable
private fun HomeScreen(onNewProject: () -> Unit, onImport: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Ink).padding(horizontal = 22.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(Accent, Color(0xFF4BC7D8)))),
                contentAlignment = Alignment.Center
            ) {
                Text("S", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("SAJI", fontSize = 13.sp, color = Muted, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Text("Video Editor", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.weight(1f))
            Surface(color = Panel2, shape = CircleShape) {
                Text("1.0", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = Muted, fontSize = 12.sp)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Make something\nworth watching.", fontSize = 31.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold)
            Text("Your clips. Your style. Your edit.", color = Muted, fontSize = 14.sp)
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onNewProject() },
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(containerColor = Panel)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF29274B), Color(0xFF172D3E))))
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Surface(color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
                        androidx.compose.material3.Icon(
                            Icons.Default.Add, contentDescription = null,
                            modifier = Modifier.padding(10.dp).size(24.dp), tint = Color.White
                        )
                    }
                    Text("New project", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Start with a video from your gallery", color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("CREATE PROJECT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.4.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("↗", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("WORKSPACE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.weight(1f))
            Text("RECENT", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
        }
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .clip(RoundedCornerShape(20.dp)).background(Panel.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(9.dp)) {
                androidx.compose.material3.Icon(Icons.Default.Film, contentDescription = null, tint = Muted, modifier = Modifier.size(34.dp))
                Text("No projects yet", fontWeight = FontWeight.SemiBold)
                Text("Import a clip and your edits will begin here.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
                OutlinedButton(onClick = onImport, border = BorderStroke(1.dp, Accent.copy(alpha = 0.65f))) {
                    Text("Import video", color = Color.White)
                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun EditorScreen(uri: Uri, onBack: () -> Unit, onReplace: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false
        }
    }
    var playing by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }
    var trimStart by remember { mutableLongStateOf(0L) }
    var trimEnd by remember { mutableLongStateOf(0L) }
    var showTrim by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var overlayText by remember { mutableStateOf("") }
    var textDraft by remember { mutableStateOf("") }
    var ratio by remember { mutableStateOf("16:9") }
    var showRatio by remember { mutableStateOf(false) }
    var exporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && player.duration > 0) {
                    duration = player.duration
                    if (trimEnd == 0L) trimEnd = player.duration
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, playing) {
        while (true) {
            if (player.isPlaying) position = player.currentPosition
            delay(200)
        }
    }

    fun exportClip() {
        if (duration <= 0L || trimEnd <= trimStart) {
            Toast.makeText(context, "Video is still loading. Try again.", Toast.LENGTH_SHORT).show()
            return
        }
        val folder = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
        val out = File(folder, "SAJI_${System.currentTimeMillis()}.mp4")
        val clippedItem = MediaItem.Builder().setUri(uri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(trimStart)
                    .setEndPositionMs(trimEnd)
                    .build()
            ).build()
        val edited = EditedMediaItem.Builder(clippedItem).build()
        exporting = true
        exportMessage = null
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: ExportResult) {
                exporting = false
                exportMessage = "Export complete. Saved in:\n${out.absolutePath}"
            }
            override fun onError(
                composition: androidx.media3.transformer.Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) {
                exporting = false
                exportMessage = "Export failed: ${exportException.message ?: "Unknown error"}"
            }
        }
        try {
            Transformer.Builder(context).addListener(listener).build().start(edited, out.absolutePath)
        } catch (e: Exception) {
            exporting = false
            exportMessage = "Could not start export: ${e.message}"
        }
    }

    val previewPlayer = player

    Column(modifier = Modifier.fillMaxSize().background(Ink)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.clip(CircleShape).clickable { onBack() },
                color = Panel, shape = CircleShape
            ) {
                androidx.compose.material3.Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.padding(10.dp), tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("SAJI STUDIO", fontSize = 10.sp, color = Muted, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                Text("Edit project", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { exportClip() },
                enabled = !exporting,
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
                shape = RoundedCornerShape(13.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 9.dp)
            ) {
                androidx.compose.material3.Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                Text("Export", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.Black)
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->
                    PlayerView(viewContext).apply {
                        this.player = previewPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                update = { it.player = player }
            )
            if (overlayText.isNotBlank()) {
                Text(
                    overlayText,
                    modifier = Modifier.align(Alignment.Center).padding(18.dp),
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                color = Color.Black.copy(alpha = 0.55f), shape = RoundedCornerShape(8.dp)
            ) {
                Text(ratio, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = Color.White, fontSize = 10.sp)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatTime(position), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Slider(
                    modifier = Modifier.weight(1f).height(28.dp).padding(horizontal = 8.dp),
                    value = position.toFloat().coerceIn(0f, duration.coerceAtLeast(1L).toFloat()),
                    onValueChange = {
                        position = it.toLong()
                        player.seekTo(position)
                    },
                    valueRange = 0f..duration.coerceAtLeast(1L).toFloat()
                )
                Text(formatTime(duration), color = Muted, fontSize = 11.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { player.seekTo((player.currentPosition - 5000).coerceAtLeast(0)) }) { Text("−5s", color = Muted) }
                Surface(
                    modifier = Modifier.clip(CircleShape).clickable { if (player.isPlaying) player.pause() else player.play() },
                    color = Accent, shape = CircleShape
                ) {
                    androidx.compose.material3.Icon(
                        if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play or pause",
                        modifier = Modifier.padding(12.dp).size(24.dp), tint = Color.White
                    )
                }
                TextButton(onClick = { player.seekTo((player.currentPosition + 5000).coerceAtMost(duration)) }) { Text("+5s", color = Muted) }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("TIMELINE", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
                Spacer(Modifier.weight(1f))
                Text("${formatTime(duration)}  •  1 clip", color = Muted, fontSize = 10.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp)
                    .clip(RoundedCornerShape(12.dp)).background(Panel)
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(4.dp).height(44.dp).clip(RoundedCornerShape(3.dp)).background(Accent))
                Row(
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp)).background(Panel2)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    repeat(7) { index ->
                        Box(
                            modifier = Modifier.weight(1f).height(36.dp).clip(RoundedCornerShape(4.dp))
                                .background(if (index % 2 == 0) Color(0xFF3A465C) else Color(0xFF29364A)),
                            contentAlignment = Alignment.Center
                        ) { Text("▥", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp) }
                    }
                }
                Box(Modifier.width(4.dp).height(44.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF52D6B5)))
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ToolTile("Trim", Icons.Default.ContentCut) { showTrim = true }
                ToolTile("Text", Icons.Default.TextFields) { textDraft = overlayText; showText = true }
                ToolTile("Canvas", Icons.Default.Crop) { showRatio = true }
                ToolTile("Adjust", Icons.Default.Tune) {
                    Toast.makeText(context, "More adjustments are coming in the next build.", Toast.LENGTH_SHORT).show()
                }
                ToolTile("Replace", Icons.Default.Film, onReplace)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Text is a preview overlay in this build. Trim export saves the selected video range.",
                color = Muted, fontSize = 10.sp, lineHeight = 14.sp
            )
        }
    }

    if (showText) {
        AlertDialog(
            onDismissRequest = { showText = false },
            containerColor = Panel,
            title = { Text("Add text") },
            text = {
                OutlinedTextField(
                    value = textDraft,
                    onValueChange = { textDraft = it },
                    label = { Text("Your text") },
                    placeholder = { Text("Type something...") },
                    singleLine = false
                )
            },
            confirmButton = {
                TextButton(onClick = { overlayText = textDraft; showText = false }) { Text("Apply", color = Accent) }
            },
            dismissButton = {
                TextButton(onClick = { overlayText = ""; showText = false }) { Text("Remove", color = Muted) }
            }
        )
    }

    if (showRatio) {
        AlertDialog(
            onDismissRequest = { showRatio = false },
            containerColor = Panel,
            title = { Text("Canvas ratio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("16:9", "9:16", "1:1").forEach { value ->
                        OutlinedButton(
                            onClick = { ratio = value; showRatio = false },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, if (ratio == value) Accent else Panel2)
                        ) {
                            Text(value, color = Color.White)
                            if (ratio == value) {
                                Spacer(Modifier.width(8.dp))
                                androidx.compose.material3.Icon(Icons.Default.Check, contentDescription = null, tint = Accent)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showRatio = false }) { Text("Done", color = Accent) } }
        )
    }

    if (showTrim) {
        val safeDuration = duration.coerceAtLeast(1L)
        AlertDialog(
            onDismissRequest = { showTrim = false },
            containerColor = Panel,
            title = { Text("Trim clip") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Start  ${formatTime(trimStart)}", color = Muted, fontSize = 12.sp)
                    Slider(
                        value = trimStart.toFloat().coerceIn(0f, safeDuration.toFloat()),
                        onValueChange = { trimStart = it.toLong().coerceAtMost((trimEnd - 500).coerceAtLeast(0)) },
                        valueRange = 0f..safeDuration.toFloat()
                    )
                    Text("End  ${formatTime(trimEnd)}", color = Muted, fontSize = 12.sp)
                    Slider(
                        value = trimEnd.toFloat().coerceIn(0f, safeDuration.toFloat()),
                        onValueChange = { trimEnd = it.toLong().coerceAtLeast(trimStart + 500).coerceAtMost(safeDuration) },
                        valueRange = 0f..safeDuration.toFloat()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrim = false; player.seekTo(trimStart) }) { Text("Apply", color = Accent) }
            },
            dismissButton = {
                TextButton(onClick = { trimStart = 0; trimEnd = duration; showTrim = false }) { Text("Reset", color = Muted) }
            }
        )
    }

    if (exporting || exportMessage != null) {
        AlertDialog(
            onDismissRequest = { if (!exporting) exportMessage = null },
            containerColor = Panel,
            title = { Text(if (exporting) "Exporting video" else "Export status") },
            text = {
                if (exporting) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = Accent, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Text("Rendering your MP4. Keep the app open.", color = Muted, fontSize = 13.sp)
                    }
                } else Text(exportMessage.orEmpty(), color = Muted, fontSize = 12.sp)
            },
            confirmButton = {
                if (!exporting) TextButton(onClick = { exportMessage = null }) { Text("OK", color = Accent) }
            }
        )
    }
}

@Composable
private fun ToolTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(65.dp).clip(RoundedCornerShape(13.dp))
            .background(Panel).clickable(onClick = onClick).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        androidx.compose.material3.Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(21.dp))
        Text(label, color = Muted, fontSize = 10.sp)
    }
}

private fun formatTime(ms: Long): String {
    val total = (ms.coerceAtLeast(0L) / 1000L).toInt()
    val minutes = total / 60
    val seconds = total % 60
    return "%02d:%02d".format(minutes, seconds)
}
