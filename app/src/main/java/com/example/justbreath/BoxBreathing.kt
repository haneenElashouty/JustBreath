package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.justbreath.ui.theme.JustBreathTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PlayState {
    forest, rain, ocean, Mute
}

class BoxBreathing : ComponentActivity() {

    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var mediaPlayer3: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MediaPlayers with looping soundtracks
        mediaPlayer1 = MediaPlayer.create(this, R.raw.forest).apply { isLooping = true }
        mediaPlayer2 = MediaPlayer.create(this, R.raw.rain).apply { isLooping = true }
        mediaPlayer3 = MediaPlayer.create(this, R.raw.ocean).apply { isLooping = true }

        setContent {
            JustBreathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // Display the BoxBreathing screen with initialized MediaPlayers
                    BoxBreathingScreen(mediaPlayer1, mediaPlayer2, mediaPlayer3)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Pause sounds when the activity is not visible
        mediaPlayer1.pause()
        mediaPlayer2.pause()
        mediaPlayer3.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources on activity destruction
        mediaPlayer1.release()
        mediaPlayer2.release()
        mediaPlayer3.release()
    }
}

@Composable
fun BoxBreathingScreen(
    mediaPlayer1: MediaPlayer,
    mediaPlayer2: MediaPlayer,
    mediaPlayer3: MediaPlayer
) {
    val myFontFamily = FontFamily(Font(R.font.sansita_bold))

    // Adjust layout size for orientation changes
    val configuration = LocalConfiguration.current
    val boxSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 220.dp else 250.dp
    val playSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 50.dp else 70.dp
    val playPadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 20.dp else 50.dp

    // Setup context and timing for breathing phases
    val context = LocalContext.current
    val inhaleTime = 4
    val holdTime = 7
    val exhaleTime = 8
    val phases = listOf("Inhale", "Hold", "Exhale", "Hold")

    // Manage screen ID and favorites with SharedPreferences
    val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    val screenId = sharedPreferences.getInt("screenId", 0)
    var countdownTime by remember { mutableStateOf(inhaleTime) }
    var currentPhaseIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var isFavorite by remember { mutableStateOf(screenId == 3) }
    var playState by remember { mutableStateOf(PlayState.Mute) }
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Control breathing animation cycle based on playing state
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            countdownTime = inhaleTime
            currentPhaseIndex = 0
            progress = 0f

            while (isPlaying) {
                val duration = when (phases[currentPhaseIndex]) {
                    "Inhale" -> inhaleTime
                    "Hold" -> holdTime
                    "Exhale" -> exhaleTime
                    else -> holdTime
                }
                val startTime = System.currentTimeMillis()
                progress = 0f
                while (progress < 1f && isPlaying) {
                    val elapsedTime = (System.currentTimeMillis() - startTime) / 1000f
                    countdownTime = (duration - elapsedTime).coerceAtLeast(0f).toInt()
                    progress = 1f - (duration - elapsedTime) / duration.toFloat()
                    delay(16L)
                }
                currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
            }
        } else {
            countdownTime = 0
            progress = 0f
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.backgroud_floating),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Top row with Back button and Music/Favorite options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                val intent = Intent(context, MainList::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }) {
                Icon(
                    painter = painterResource(R.drawable.round_arrow_mind_24),
                    contentDescription = "Back",
                    tint = Color(110, 56, 112, 255)
                )
            }
            Row {
                IconButton(onClick = {
                    currentPlayer?.pause()
                    currentPlayer?.seekTo(0)
                    currentPlayer = when (playState) {
                        PlayState.forest -> mediaPlayer1.also { playState = PlayState.rain }
                        PlayState.rain -> mediaPlayer2.also { playState = PlayState.ocean }
                        PlayState.ocean -> null.also { playState = PlayState.Mute }
                        PlayState.Mute -> mediaPlayer3.also { playState = PlayState.forest }
                    }
                    currentPlayer?.start()
                }) {
                    Icon(
                        painter = painterResource(
                            if (playState == PlayState.Mute) R.drawable.vector_17 else R.drawable.vector_2
                        ),
                        contentDescription = "Music",
                        tint = Color(110, 56, 112, 255)
                    )
                }

                IconButton(onClick = {
                    isFavorite = !isFavorite
                    sharedPreferences.edit().putInt("screenId", if (isFavorite) 3 else 0).apply()
                }) {
                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.vector_5 else R.drawable.vector_10),
                        contentDescription = "Favorite",
                        tint = Color(110, 56, 112, 255)
                    )
                }
            }
        }

        // Breathing animation box with countdown timer
        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(boxSize).border(20.dp, Color(210, 155, 215, 255))) {}
            Canvas(modifier = Modifier.size(boxSize - 22.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val lineLength = canvasWidth * 2 + canvasHeight * 2
                val currentLength = (progress / 4) * lineLength
                when (currentPhaseIndex) {
                    0 -> drawLine(
                        Color(35, 95, 210, 255),
                        Offset(0f, 0f),
                        Offset(currentLength.coerceAtMost(canvasWidth), 0f),
                        30f,
                        StrokeCap.Round
                    )

                    1 -> drawLine(
                        Color(35, 95, 210, 255),
                        Offset(canvasWidth, 0f),
                        Offset(canvasWidth, currentLength.coerceAtMost(canvasHeight)),
                        30f,
                        StrokeCap.Round
                    )

                    2 -> drawLine(
                        Color(35, 95, 210, 255),
                        Offset(canvasWidth, canvasHeight),
                        Offset((canvasWidth - currentLength).coerceAtLeast(0f), canvasHeight),
                        30f,
                        StrokeCap.Round
                    )

                    3 -> drawLine(
                        Color(35, 95, 210, 255),
                        Offset(0f, canvasHeight),
                        Offset(0f, (canvasHeight - currentLength).coerceAtLeast(0f)),
                        30f,
                        StrokeCap.Round
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,  // Align horizontally to the center
                verticalArrangement = Arrangement.Center,  // Vertically center the content
                modifier = Modifier.fillMaxSize()  // Fill the entire screen
            ) {

                Text(
                    text = phases[currentPhaseIndex],
                    fontSize = 32.sp,
                    fontFamily = myFontFamily,
                    color = Color(110, 56, 112, 255),
                    textAlign = TextAlign.Center,

                )

                Text(
                    text = countdownTime.toString(),
                    fontSize = 24.sp,
                    fontFamily = myFontFamily,
                    color = Color(110, 56, 112, 255),
                    textAlign = TextAlign.Center,

                )
            }
        }

        // Play/Pause button
        IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
                .padding(playPadding)
                .size(playSize)
                .align(Alignment.BottomCenter)
        ) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.round_pause_mind_32 else R.drawable.round_play_mind_50),
                contentDescription = "Play/Pause",
                tint =  Color(110, 56, 112, 255),
                modifier = Modifier.size(playSize)
            )
        }
    }
}

/**
 * BoxBreathingScreen Composable Function
 *
 * This composable function creates the main UI for the Box Breathing exercise screen in the JustBreath app.
 * It combines breathing phases (Inhale, Hold, Exhale) with animations, sound options, and UI elements to guide users
 * through a calm, structured breathing exercise. Each phase's timing is synchronized with a moving progress indicator,
 * creating a visual cue for users to follow along.
 *
 * Key Components:
 * 1. **Dynamic Layout Adjustments**:
 *      - The screen dynamically adjusts to portrait and landscape orientations. Based on the current configuration,
 *        it adapts the box size, play button size, and padding, providing a responsive and intuitive design on different devices.
 *
 * 2. **Breathing Phases and Animation**:
 *      - The breathing exercise includes four phases: "Inhale", "Hold", "Exhale", and "Hold" again. Each phase has its own duration
 *        (Inhale for 4 seconds, Hold for 7 seconds, Exhale for 8 seconds), which users follow visually through a countdown timer and
 *        a rectangular breathing guide animation that moves along the border of the box.
 *      - The `progress` variable drives the progress of each phase, visually guiding the user around the edges of the rectangle.
 *      - The `LaunchedEffect` coroutine allows the animation to loop while `isPlaying` is true, handling the phase transitions and
 *        countdowns for each cycle.
 *
 * 3. **MediaPlayer Control and Sound Effects**:
 *      - Users can toggle between ambient sound options (forest, rain, ocean) or mute the sounds entirely.
 *        This feature is implemented using three `MediaPlayer` instances, which are paused and resumed based on user selection.
 *      - The `IconButton` controls cycle through sound choices and play or pause the selected track accordingly.
 *
 * 4. **Favorite Button and SharedPreferences**:
 *      - Users can mark this screen as a favorite. The favorite status is stored using SharedPreferences, ensuring
 *        the user's choice persists even after closing the app. The shared preference also helps with managing screen IDs for navigation.
 *
 * 5. **Play/Pause Button**:
 *      - The play/pause button at the bottom of the screen controls the breathing animation. Tapping it toggles the `isPlaying` state,
 *        starting or stopping the breathing guide and countdown timer.
 *
 * 6. **Code Organization and Modularity**:
 *      - The code is organized to separate concerns: MediaPlayer setup, layout adjustments based on orientation,
 *        animations, sound controls, and favorite preferences are each handled in their own segments.
 *      - This modular approach enhances readability and makes it easier to debug or expand features in the future.
 *
 * Usage:
 *  - This screen is designed to guide users in practicing box breathing, with audio, visual, and countdown guidance.
 *  - It can be integrated directly as the main content in the `BoxBreathing` activity, where lifecycle methods manage
 *    MediaPlayer resources to prevent memory leaks.
 *
 */
