package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.justbreath.ui.theme.JustBreathTheme
import kotlinx.coroutines.delay

class FavFSEBreath : ComponentActivity() {

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
                    FavBreathingScreen(mediaPlayer1, mediaPlayer2, mediaPlayer3)
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
fun FavBreathingScreen(
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


            IconButton(
                onClick = {
                    val intent = Intent(context, MainList::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vector_1), // Replace with your hamburger icon
                    contentDescription = "Menu",
                    tint = Color(25, 43, 82, 255)
                )
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

                val angle = 0f // Adjust 270 based on your sweep range
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
                                cap = StrokeCap.Round
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
                            cap = StrokeCap.Round
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

/*
This is the `FavFSEBreath` Activity, which represents a screen for a breathing exercise with audio controls and animated graphics.

Key components:
1. **MediaPlayer** - Three MediaPlayer objects are initialized (`mediaPlayer1`, `mediaPlayer2`, `mediaPlayer3`) for different ambient sounds (forest, rain, ocean) that loop continuously. The activity also controls the playback of these sounds.
2. **UI Layout** - The screen features a breathing animation that cycles through the phases of breathing: Inhale, Hold, and Exhale. The layout adjusts according to the device's orientation (portrait vs. landscape), ensuring a responsive UI.
3. **SharedPreferences** - The app reads a stored screen ID to customize user preferences or to maintain consistency across app sessions.
4. **State Management** - Various `remember` and `mutableStateOf` hooks are used for managing UI states like:
    - The current countdown time (for inhale, hold, exhale)
    - The breathing phase (Inhale, Hold, Exhale)
    - The music state (Mute, Forest, Rain, Ocean)
    - Whether the breathing exercise is playing or paused.
5. **Animation** - A circular progress bar that animates to represent the breathing cycle, showing a sweep effect that rotates as the countdown progresses. The animation is driven by an `infiniteRepeatable` transition.
6. **Music Control** - The user can toggle between the different soundtracks (forest, rain, ocean) by pressing the music button. When a new sound is selected, the corresponding `MediaPlayer` starts playing, while the others are paused.
7. **Breathing Logic** - The breathing cycle consists of 3 phases:
    - Inhale (4 seconds)
    - Hold (7 seconds)
    - Exhale (8 seconds)
    The countdown for each phase is managed in a loop, and the UI updates every second. The phase transitions from Inhale → Hold → Exhale → Inhale, and so on.
8. **UI Layout in Columns and Rows** - The layout uses a `Box` as the primary container, with nested `Row` and `Column` for placing items. The `IconButton`s for switching sounds and navigating to other screens are positioned in the top row, while the breathing animation is centered in the middle.
9. **Responsive Design** - The app's layout and button sizes adjust based on the screen orientation. The size of the breathing circle (`boxSize`) and the play/pause button (`playSize`) change depending on whether the device is in portrait or landscape mode.
10. **Play/Pause Button** - The user can start or stop the breathing animation using a play/pause button. The button's state (play or pause) is toggled by pressing it, and this triggers the animation of the countdown and breathing cycle.

This code is structured to provide a smooth user experience with transitions, animations, and music control for a relaxing breathing exercise. It manages state effectively using Jetpack Compose and provides interactive elements like play/pause buttons and music toggle options.
*/


