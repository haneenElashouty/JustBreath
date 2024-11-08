package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.justbreath.ui.theme.JustBreathTheme
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.materialcore.toRadians
import kotlin.math.cos
import kotlin.math.sin


class FSEBreath : ComponentActivity() {

    private lateinit var mediaPlayer1: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer
    private lateinit var mediaPlayer3: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MediaPlayers
        mediaPlayer1 = MediaPlayer.create(this, R.raw.forest).apply { isLooping = true }
        mediaPlayer2 = MediaPlayer.create(this, R.raw.rain).apply { isLooping = true }
        mediaPlayer3 = MediaPlayer.create(this, R.raw.ocean).apply { isLooping = true }

        setContent {
            JustBreathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // Pass the MediaPlayers to the composable function
                    BreathingScreen(mediaPlayer1, mediaPlayer2, mediaPlayer3)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Pause all media players when the activity is stopped
        mediaPlayer1.pause()
        mediaPlayer2.pause()
        mediaPlayer3.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release media players when the activity is destroyed
        mediaPlayer1.release()
        mediaPlayer2.release()
        mediaPlayer3.release()
    }
}

@Composable
fun BreathingScreen(
    mediaPlayer1: MediaPlayer,
    mediaPlayer2: MediaPlayer,
    mediaPlayer3: MediaPlayer
) {
    //font
    val myFontFamily = FontFamily(
        Font(R.font.sansita_bold),
    )

    val context = LocalContext.current
    // Timing for Inhale, Hold, and Exhale phases
    val inhaleTime = 4
    val holdTime = 7
    val exhaleTime = 8
    //value of favourite
    val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    val screenId = sharedPreferences.getInt("screenId", 0)

    //music
    var playState by remember { mutableStateOf(PlayState.Mute) }
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // States for countdown, phase, and play/pause status

    var countdownTime by remember { mutableStateOf(inhaleTime) }
    var currentPhase by remember { mutableStateOf("Inhale") }
    var isPlaying by remember { mutableStateOf(false) }
    var isFavorite by remember { if (screenId==2) mutableStateOf(true) else mutableStateOf(false)  }

    // Determine  size based on orientation
    val configuration = LocalConfiguration.current
    val boxSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 250.dp else 300.dp
    val playSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 50.dp else 70.dp
    val playpadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 20.dp else 50.dp


    // Infinite animation for breathing circle
    val infiniteTransition = rememberInfiniteTransition()
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
//to radiant
fun Float.toRadians(): Float {
    return (this * Math.PI / 180).toFloat()
}
    // Handle the breathing phases and countdown logic
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                countdownTime = when (currentPhase) {
                    "Inhale" -> inhaleTime
                    "Hold" -> holdTime
                    "Exhale" -> exhaleTime
                    else -> inhaleTime
                }

                repeat(countdownTime) {
                    countdownTime--
                    delay(1000L)
                }

                currentPhase = when (currentPhase) {
                    "Inhale" -> "Hold"
                    "Hold" -> "Exhale"
                    else -> "Inhale"
                }
            }
        }
    }

    // Main UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFEDE2F6), Color(0xFFD2DDF7)))),
        contentAlignment = Alignment.Center
    ) {
        // Top row with back and favorite buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { val intent = Intent(context, MainList::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)}) {
                Icon(
                    painter = painterResource(R.drawable.round_arrow_mind_24),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Back",
                    tint = Color(25, 43, 82, 255)
                )
            }

            Row {
                //music button
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
                        tint = Color(25, 43, 82, 255)
                    )
                }


                IconButton(onClick = {
                    isFavorite = !isFavorite
                    val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("screenId", if (isFavorite) 2 else 0)
                    editor.apply()
                }) {
                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.vector_5 else R.drawable.vector_10),
                        contentDescription = "Favorite",
                        tint = Color(25, 43, 82, 255)
                    )
                }
            }
        }


        // Circular breathing animation
        Box(
            modifier = Modifier
                .size(boxSize)
                .align(Alignment.Center)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(boxSize-23.dp)
                    .clip(CircleShape)
                    .border(30.dp, Color(34, 68, 134, 255), CircleShape)
            )

            Canvas(modifier = Modifier.size(boxSize-38.dp)) {
                val strokeWidth = 16.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = size.center

                val angle = 0f
                val sweepAngle = -180f


                // Create the sweep gradient with start and end colors
                val gradient = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(239, 220, 242, 255),
                        Color.Transparent
                    ),
                    center = center,

                    )
                if(isPlaying) {
                    rotate(-90f + animatedProgress * -360f, pivot = center) {
                        drawArc(
                            brush = gradient,
                            startAngle = angle, // Start from 90 degrees
                            sweepAngle = sweepAngle,  // Use animated progress for sweep angle
                            useCenter = false,
                            style = Stroke(
                                strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            ),
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(center.x - radius, center.y - radius)
                        )
                    }
                }
                else{
                    drawArc(
                        brush = gradient,
                        startAngle = angle, // Start from 90 degrees
                        sweepAngle = angle-180f,  // Use animated progress for sweep angle
                        useCenter = false,
                        style = Stroke(
                            strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        ),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(center.x - radius, center.y - radius)
                    )
                }
            }

        }

        // Text inside the breathing circle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if(!isPlaying) {"paused"} else currentPhase,
                color = Color(34, 68, 134, 255),
                fontSize = 24.sp,
                fontFamily = myFontFamily,
                textAlign = TextAlign.Center
            )
            Text(
                text = if(!isPlaying){"0"} else countdownTime.toString(),
                color = Color(34, 68, 134, 255),
                fontSize = 36.sp,
                fontFamily = myFontFamily,
                textAlign = TextAlign.Center
            )
        }


        // Play/Pause button at the bottom
        IconButton(
            onClick = {
                isPlaying = !isPlaying
                // Pause animation logic can be implemented here
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(playpadding).size(playSize)
        ) {
            Icon(

                painter = painterResource(if (isPlaying) R.drawable.round_pause_mind_32 else R.drawable.round_play_mind_50),
                contentDescription = "Play/Pause",
                tint = Color(25, 43, 82, 255),
                modifier = Modifier.size(playSize)
            )
        }
    }
    }

/**
 * BreathingScreen Composable Function
 *
 * This function creates the UI for the "Breathing Exercise" screen in the JustBreath app.
 * It helps users follow a guided breathing exercise by controlling phases like "Inhale", "Hold", "Exhale".
 * It also has animations and sound effects that enhance the user's experience.
 *
 * Key Concepts:
 *
 * 1. **Dynamic Layout**:
 *      - The layout (how everything is arranged on the screen) changes depending on the device’s orientation (portrait or landscape).
 *      - Elements like buttons and the animated breathing guide adjust their size and position to fit properly on different screens.
 *
 * 2. **Breathing Phases and Animation**:
 *      - There are three phases in the exercise: "Inhale", "Hold", and "Exhale". Each phase has a set time (Inhale: 4 seconds, Hold: 7 seconds, Exhale: 8 seconds).
 *      - The animation shows the breathing cycle visually, expanding and contracting with the breathing phases.
 *      - The progress of the animation is tied to the countdown of each phase. It helps the user know when to inhale, hold, or exhale.
 *      - The `LaunchedEffect` ensures that the animation and countdown are updated as long as the exercise is playing.
 *
 * 3. **Background Music Control**:
 *      - The app lets the user choose between different sounds (forest, rain, ocean) to play in the background while they do the breathing exercise.
 *      - `MediaPlayer` is used to play these sounds, and the user can switch between them or mute the sound using buttons.
 *      - The `playState` keeps track of what sound is playing or if it's muted.
 *
 * 4. **Favorite Button and SharedPreferences**:
 *      - There is a button to mark this breathing exercise screen as a "favorite." This allows users to quickly access it later.
 *      - The favorite status is saved using `SharedPreferences`, which is a way to save small pieces of data (like favorites) so that the app remembers it between sessions.
 *
 * 5. **Play/Pause Button**:
 *      - The button at the bottom starts and stops the exercise. When pressed, it either begins or pauses the animation and the countdown timer.
 *      - This gives the user control over the pace of the exercise.
 *
 * 6. **Modular Code Structure**:
 *      - The code is organized in a way that makes it easier to understand and maintain. Each part of the screen's functionality (layout, animation, sound, favorite status) is separated into its own section of code.
 *      - This helps prevent the code from becoming messy and makes it easier to make changes or fix bugs in the future.
 *
 * How It Works:
 *  - This screen is used to guide users through a breathing exercise, with visual and audio cues to help them follow the timing of each phase.
 *  - It can be added to the app and displayed as the main screen where the user performs the breathing exercise.
 *  - The app makes sure to clean up resources, like the music, when the user moves away from the screen.
 *
 * Note: The app uses Android’s `MediaPlayer` to manage and loop the background music. When the user changes the sound or mutes it, the current music is stopped and reset before starting the next one.
 */




