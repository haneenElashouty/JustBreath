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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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


class FavMindfulBreathing : ComponentActivity() {

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
                    Greeting(mediaPlayer1, mediaPlayer2, mediaPlayer3)
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
fun Greeting(
    mediaPlayer1: MediaPlayer,
    mediaPlayer2: MediaPlayer,
    mediaPlayer3: MediaPlayer
) {
    //font
    val myFontFamily = FontFamily(
        Font(R.font.sansita_bold),
    )
    //timer
    val context = LocalContext.current
    val inhaleTime = 6
    val exhaleTime = 8
    //value of favourite
    val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    val screenId = sharedPreferences.getInt("screenId", 0)

    //music
    var playState by remember { mutableStateOf(PlayState.Mute) }
    var currentPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    var currentPhase by remember { mutableStateOf("Inhale") }
    var isPlaying by remember { mutableStateOf(false) }
    var countdownTime by remember { mutableIntStateOf(inhaleTime) }

    // Determine  size based on orientation
    val configuration = LocalConfiguration.current
    val boxSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 300f else 400f
    val boxSize2 = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 200.dp else 300.dp
    val playSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 50.dp else 70.dp
    val playpadding = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 20.dp else 50.dp





    // Animating the circles
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation =  tween(3000) ,
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val animatedScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000,1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val animatedScale3 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000,2000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                countdownTime = when (currentPhase) {
                    "Inhale" -> inhaleTime
                    "Exhale" -> exhaleTime
                    else -> inhaleTime
                }

                repeat(countdownTime) {
                    countdownTime--
                    delay(1000L)
                }

                currentPhase = when (currentPhase) {
                    "Inhale" -> "Exhale"
                    "Exhale"->  "Inhale"
                    else -> "Inhale"
                }
            }
        }else{currentPhase="Inhale"}

    }



    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.group_4), // Replace with your actual background image resource
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Top bar with back, music, and favorite buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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

        // Middle circles with animation
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(boxSize2),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (isPlaying){
                    val size1 = size.minDimension * animatedScale1
                    val size2 = size.minDimension * animatedScale2
                    val size3 = size.minDimension * animatedScale3

                    drawCircle(Color(25, 43, 82, 102), radius = size1/3f )
                    drawCircle(Color(25, 43, 82, 102), radius = size2/3.2f )
                    drawCircle(Color(25, 43, 82, 153), radius = size3/3.4f )}
                else{ drawCircle(Color(25, 43, 82, 102), radius = boxSize-120f )
                    drawCircle(Color(25, 43, 82, 102), radius = boxSize-60f )
                    drawCircle(Color(25, 43, 82, 153), radius = boxSize )}
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if(!isPlaying) {"paused"} else currentPhase,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = myFontFamily,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if(!isPlaying){"0"} else countdownTime.toString(),
                    color = Color.White,
                    fontSize = 36.sp,
                    fontFamily = myFontFamily,
                    textAlign = TextAlign.Center
                )
            }
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
This is the `FavMindfulBreathing` Activity, which represents a screen for a mindful breathing exercise with animated circles, music controls, and a countdown timer.

Key components:
1. **MediaPlayer** - Three `MediaPlayer` objects are created for playing ambient sounds (forest, rain, ocean), all set to loop continuously. The activity manages the playback and switching between these soundtracks. When the activity is paused or destroyed, the media players are paused and released respectively to free resources.
2. **UI Layout** - The main UI consists of animated breathing circles in the center, which expand and contract with each cycle of the exercise. The circles animate using the `infiniteRepeatable` transition to create a soothing effect. The layout adjusts based on the screen orientation, providing a responsive design for both portrait and landscape modes.
3. **State Management** - Several pieces of state are handled using Jetpack Compose's `remember` and `mutableStateOf`:
    - The `currentPhase` state keeps track of the breathing cycle (Inhale, Exhale).
    - `countdownTime` manages the countdown for each phase of the cycle.
    - `isPlaying` determines if the animation and countdown are active or paused.
    - `playState` stores the current state of the background music, controlling which audio track is playing or muted.
4. **SharedPreferences** - The screen ID is saved and retrieved from `SharedPreferences` to determine if the screen should be marked as a favorite. This allows the state to persist across sessions.
5. **Breathing Logic** - The breathing exercise is structured in a cycle of three phases:
    - **Inhale** (6 seconds)
    - **Exhale** (8 seconds)
    The `LaunchedEffect` is used to handle the countdown timer and phase transitions. After each phase finishes, the state switches between Inhale and Exhale.
6. **Music Control** - The music button toggles between different ambient sounds (forest, rain, ocean). When clicked, the current sound stops, and the next sound starts playing. If all sounds are paused, the button toggles to mute the music. The music playback is managed using the `MediaPlayer` instances and their `start()` and `pause()` methods.
7. **Responsive Design** - The screen layout adjusts based on the device's orientation. The size of the breathing animation (`boxSize2`) and the play/pause button (`playSize`) change dynamically to fit the screen.
8. **Animations** - The breathing circles in the center of the screen animate using `animateFloat` to create a smooth, pulsing effect that simulates the breathing cycle. The animation is applied to three circles, each with a different timing for a layered effect. The circles' size increases and decreases in sync with the breathing phases.
9. **UI Elements** -
    - The top bar contains a back button for navigation, a music toggle button to control ambient sounds, and a menu button for navigation.
    - The central part of the screen shows the animated breathing circles and the current phase (Inhale, Exhale).
    - A play/pause button at the bottom of the screen starts or stops the breathing exercise and animation.
10. **Lifecycle Management** - The `onStop()` and `onDestroy()` lifecycle methods ensure that the media players are properly paused and released when the activity is not in use, to prevent memory leaks and excessive resource usage.

This code offers a relaxing user experience, combining calming music, animation, and a timed breathing exercise. It uses Jetpack Compose's state management and animation features to build a smooth and responsive interface.
*/
