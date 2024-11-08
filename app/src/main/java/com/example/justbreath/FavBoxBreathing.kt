package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

// Main Activity to manage media players and screen content
class FavBoxBreathing : ComponentActivity() {

    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var mediaPlayer3: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MediaPlayers for background music with looping enabled
        mediaPlayer1 = MediaPlayer.create(this, R.raw.forest).apply { isLooping = true }
        mediaPlayer2 = MediaPlayer.create(this, R.raw.rain).apply { isLooping = true }
        mediaPlayer3 = MediaPlayer.create(this, R.raw.ocean).apply { isLooping = true }

        setContent {
            JustBreathTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    // Pass media players to the composable screen
                    FavBoxBreathingScreen(mediaPlayer1, mediaPlayer2, mediaPlayer3)
                }
            }
        }
    }

    // Stop music when the activity is paused
    override fun onStop() {
        super.onStop()
        mediaPlayer1.pause()
        mediaPlayer2.pause()
        mediaPlayer3.pause()
    }

    // Release resources when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1.release()
        mediaPlayer2.release()
        mediaPlayer3.release()
    }
}

// Composable function to handle the breathing exercise UI and logic
@Composable
fun FavBoxBreathingScreen(
    mediaPlayer1: MediaPlayer,
    mediaPlayer2: MediaPlayer,
    mediaPlayer3: MediaPlayer
) {
    val context = LocalContext.current

    // Define font for text
    val myFontFamily = FontFamily(Font(R.font.sansita_bold))

    // Define breathing phase times
    val inhaleTime = 4
    val holdTime = 7
    val exhaleTime = 8
    val phases = listOf("Inhale", "Hold", "Exhale", "Hold")

    // Shared preferences to save favorite state
    val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)

    // State variables for breathing phases, countdown, and progress
    var countdownTime by remember { mutableStateOf(inhaleTime) }
    var currentPhaseIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    // State for managing music playback
    var playState by remember { mutableStateOf("Mute") }
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Get device orientation and adjust sizes accordingly
    val configuration = LocalConfiguration.current
    val boxSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 220.dp else 250.dp
    val playSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 50.dp else 70.dp
    val playPadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 20.dp else 50.dp

    // Start animation when `isPlaying` is true
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                val duration = when (phases[currentPhaseIndex]) {
                    "Inhale" -> inhaleTime
                    "Hold" -> holdTime
                    "Exhale" -> exhaleTime
                    else -> holdTime
                }

                // Capture the start time of each phase
                val startTime = System.currentTimeMillis()

                // Reset progress for the new phase
                progress = 0f

                // Calculate progress until the phase is complete
                while (progress < 1f && isPlaying) {
                    val elapsedTime = (System.currentTimeMillis() - startTime) / 1000f // Time elapsed in seconds
                    countdownTime = (duration - elapsedTime).coerceAtLeast(0f).toInt()
                    progress = 1f - (duration - elapsedTime) / duration.toFloat()

                    // Delay to control animation frame rate
                    delay(16L)
                }

                // Cycle to the next phase
                currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
            }
        } else {
            // Set paused state immediately when not playing
            countdownTime = 0
            progress = 0f
        }
    }

    // Main layout for the screen, containing the background and UI components
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        // Set background image
        Image(
            painter = painterResource(id = R.drawable.backgroud_floating),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Header Row with music button and menu button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Music button logic
            IconButton(onClick = {
                // Stop current music and reset position
                currentPlayer?.pause()
                currentPlayer?.seekTo(0)

                // Switch between the music tracks
                when (playState) {
                    "Mute" -> {
                        playState = "Forest"
                        currentPlayer = mediaPlayer1
                    }
                    "Forest" -> {
                        playState = "Rain"
                        currentPlayer = mediaPlayer2
                    }
                    "Rain" -> {
                        playState = "Ocean"
                        currentPlayer = mediaPlayer3
                    }
                    "Ocean" -> {
                        playState = "Mute"
                        currentPlayer = null
                    }
                }
                // Start the selected music or stop if "Mute"
                currentPlayer?.start()
            }) {
                Icon(
                    painter = painterResource(
                        if (playState == "Mute") R.drawable.vector_17 else R.drawable.vector_2
                    ),
                    contentDescription = "Music",
                    tint = Color(110, 56, 112, 255)
                )
            }

            // Menu button to navigate to another screen
            IconButton(
                onClick = {
                    val intent = Intent(context, MainList::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vector_1),
                    contentDescription = "Menu",
                    tint = Color(110, 56, 112, 255)
                )
            }
        }

        // Breathing phase box with animated line progress
        Box(modifier = Modifier.size(boxSize), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(boxSize).border(20.dp, Color(210, 155, 215, 255))) {}
            Canvas(modifier = Modifier.size(boxSize - 22.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val lineLength = canvasWidth * 2 + canvasHeight * 2
                val currentLength = (progress / 4) * lineLength

                // Draw animated line depending on the current phase
                when (currentPhaseIndex) {
                    0 -> drawLine(
                        color = Color(35, 95, 210, 255),
                        start = Offset(0f, 0f),
                        end = Offset(currentLength.coerceAtMost(canvasWidth), 0f),
                        strokeWidth = 30f,
                        cap = StrokeCap.Round
                    )
                    1 -> drawLine(
                        color = Color(35, 95, 210, 255),
                        start = Offset(canvasWidth, 0f),
                        end = Offset(canvasWidth, currentLength.coerceAtMost(canvasHeight)),
                        strokeWidth = 30f,
                        cap = StrokeCap.Round
                    )
                    2 -> drawLine(
                        color = Color(35, 95, 210, 255),
                        start = Offset(canvasWidth, canvasHeight),
                        end = Offset((canvasWidth - currentLength).coerceAtLeast(0f), canvasHeight),
                        strokeWidth = 30f,
                        cap = StrokeCap.Round
                    )
                    3 -> drawLine(
                        color = Color(35, 95, 210, 255),
                        start = Offset(0f, canvasHeight),
                        end = Offset(0f, (canvasHeight - currentLength).coerceAtLeast(0f)),
                        strokeWidth = 30f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Text to display the current phase and countdown
        Text(
            text = "${phases[currentPhaseIndex]} $countdownTime",
            fontFamily = myFontFamily,
            color = Color(110, 56, 112, 255),
            fontSize = 35.sp,
            textAlign = TextAlign.Center
        )

        // Play/pause button for breathing animation
        Box(
            modifier = Modifier
                .padding(playPadding)
                .align(Alignment.BottomCenter)
        ) {
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
}

/**
 * FavBoxBreathingScreen Composable Function
 *
 * This composable function is responsible for creating the UI of the "Box Breathing" exercise screen
 * in the JustBreath app. It integrates breathing phases (Inhale, Hold, Exhale) with visual animations,
 * audio options, and interactive UI components to guide users through a structured breathing exercise.
 * Each phase’s timing is synchronized with a moving progress indicator that visually guides the user
 * throughout the exercise.
 *
 * Key Components:
 *
 * 1. **Dynamic Layout Adjustments**:
 *      - The layout adapts to both portrait and landscape orientations. It adjusts the box size,
 *        play button size, and padding dynamically based on the device's orientation.
 *      - This responsive design ensures an intuitive experience across different screen sizes and orientations.
 *
 * 2. **Breathing Phases and Animation**:
 *      - The breathing exercise consists of four phases: "Inhale", "Hold", "Exhale", and "Hold".
 *        Each phase has a defined duration (Inhale: 4 seconds, Hold: 7 seconds, Exhale: 8 seconds).
 *      - These phases are synchronized with a countdown timer and a progress bar that animates along the
 *        rectangular breathing guide, moving around its borders in sync with the phase's timing.
 *      - The animation is driven by the `progress` variable, which updates based on the time elapsed for
 *        the current phase.
 *      - The `LaunchedEffect` coroutine handles the animation loop, transitioning between phases and updating
 *        the countdown time. This effect runs as long as `isPlaying` is true.
 *
 * 3. **MediaPlayer Control and Sound Effects**:
 *      - The app offers a choice of ambient soundtracks (forest, rain, ocean) to accompany the breathing exercise.
 *      - Three separate `MediaPlayer` instances are used to manage these soundtracks, with looping enabled.
 *      - The `IconButton` lets users cycle between different soundtracks or mute the sound by changing the
 *        `playState`. When a new sound is selected, the previous track is paused and reset.
 *      - The music state is controlled via the `playState` variable, which manages the selected sound option
 *        ("Mute", "Forest", "Rain", "Ocean").
 *
 * 4. **Favorite Button and SharedPreferences**:
 *      - The app allows users to mark the breathing screen as a favorite. This state is saved using
 *        `SharedPreferences`, ensuring that the user’s choice is persistent across app sessions.
 *      - The SharedPreferences are also used to manage and store screen IDs, enabling navigation control based
 *        on user preferences.
 *
 * 5. **Play/Pause Button**:
 *      - The play/pause button at the bottom of the screen toggles the `isPlaying` state, which starts and stops
 *        the breathing animation and the countdown timer.
 *      - When the button is tapped, it either begins or pauses the breathing guide animation, based on the current state.
 *
 * 6. **Code Organization and Modularity**:
 *      - The code is structured to ensure clarity and maintainability. Each aspect of the screen, including the
 *        MediaPlayer setup, layout adjustments for orientation changes, animations, sound controls, and favorite
 *        preferences, are logically separated.
 *      - This modular approach increases readability, simplifies debugging, and provides a solid foundation for
 *        adding new features in the future.
 *
 * Usage:
 *  - This screen is used to guide users through the box breathing exercise, providing both visual and audio cues.
 *  - It integrates seamlessly into the app, typically being displayed as the main content of the `BoxBreathing`
 *    activity.
 *  - The lifecycle methods in the activity manage the `MediaPlayer` instances to prevent memory leaks and ensure
 *    proper resource management.
 *
 * Note: This composable function relies on Android's `MediaPlayer` to handle the ambient soundtracks, and
 * the sound is played in a loop. When the user switches between soundtracks, the current sound is paused and
 * reset to the beginning before playing the next track.
 */

