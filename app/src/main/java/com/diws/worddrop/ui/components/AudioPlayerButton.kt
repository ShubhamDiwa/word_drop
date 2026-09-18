package com.diws.worddrop.ui.components

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.diws.worddrop.ui.theme.PrimaryContainerPurple
import com.diws.worddrop.ui.theme.PrimaryPurple
import java.util.Locale

@Suppress("UNUSED_PARAMETER")
@Composable
fun AudioPlayerButton(
    audioUrl: String?,
    wordToSpeak: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = ttsEngine
        onDispose {
            try {
                ttsEngine.stop()
                ttsEngine.shutdown()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    val speakWord = {
        val engine = tts
        if (engine != null) {
            engine.language = Locale.US
            val result = engine.speak(wordToSpeak, TextToSpeech.QUEUE_FLUSH, null, "vocab_tts_id")
            if (result == TextToSpeech.ERROR) {
                Toast.makeText(context, wordToSpeak, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, wordToSpeak, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(PrimaryContainerPurple.copy(alpha = 0.3f))
            .clickable { speakWord() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.VolumeUp,
            contentDescription = "Listen to pronunciation",
            tint = PrimaryPurple,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}
