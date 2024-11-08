package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.justbreath.ui.theme.JustBreathTheme

/**
 * MainList Activity
 *
 * This activity displays a list of breathing exercises in a responsive layout that adjusts
 * based on the device's orientation (portrait or landscape). The exercises are presented in
 * either a column or lazy row, and the user can navigate to different screens by clicking
 * on each exercise.
 */
class MainList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JustBreathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TheList() // Display the list of exercises
                }
            }
        }
    }
}

/**
 * TheList Composable Function
 *
 * This composable function is responsible for displaying the list of available breathing exercises.
 * It adjusts the layout based on the screen orientation, either using a LazyRow (for landscape)
 * or a Column (for portrait). Each exercise is clickable and navigates to the corresponding screen.
 *
 * Key Features:
 * - Dynamic layout based on device orientation (portrait vs. landscape).
 * - Each exercise is represented by an image and a label.
 * - Clicking an exercise navigates to a new screen using an Intent.
 */
@Composable
fun TheList(modifier: Modifier = Modifier) {
    val myFontFamily = FontFamily(Font(R.font.sansita_bold)) // Font for labels
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE // Detect orientation

    // Get screen ID from SharedPreferences to determine where to navigate
    val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
    val screenId = sharedPreferences.getInt("screenId", 0)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White) // Background color for the screen
    ) {
        // Determine box height based on screen size and orientation
        val boxHeight = if (maxWidth < 600.dp) 200.dp else 300.dp
        val boxWidth = if (isLandscape) maxWidth / 3 else maxWidth // Adjust width for landscape mode

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 20.dp)
                .align(Alignment.Center) // Align content in the center
        ) {

            // Menu button to navigate to different screens based on screenId
            IconButton(
                onClick = {
                    val intent = when (screenId) {
                        0 -> Intent(context, MainActivity::class.java)
                        1 -> Intent(context, FavMindfulBreathing::class.java)
                        2 -> Intent(context, FavFSEBreath::class.java)
                        3 -> Intent(context, FavBoxBreathing::class.java)
                        else -> Intent(context, MainActivity::class.java)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp) // Positioning the menu icon
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vector_3),
                    contentDescription = "Menu",
                    tint = Color(25, 43, 82, 255) // Menu icon color
                )
            }

            // Switch between LazyRow (landscape) and Column (portrait) based on orientation
            if (isLandscape) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Exercise items for landscape mode
                    item {
                        ResponsiveBox(
                            context,
                            "Mindful Breathing",
                            R.drawable.mind,
                            boxHeight,
                            myFontFamily,
                            Intent(context, MindfulBreathing::class.java)
                        )
                    }
                    item {
                        ResponsiveBox(
                            context,
                            "4_7_8 Breathing",
                            R.drawable.fse,
                            boxHeight,
                            myFontFamily,
                            Intent(context, FSEBreath::class.java)
                        )
                    }
                    item {
                        ResponsiveBox(
                            context,
                            "Box Breathing",
                            R.drawable.box,
                            boxHeight,
                            myFontFamily,
                            Intent(context, BoxBreathing::class.java)
                        )
                    }
                }
            } else {
                // Display exercises in a column for portrait mode
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ResponsiveBox(
                        context,
                        "Mindful Breathing",
                        R.drawable.mind,
                        boxHeight,
                        myFontFamily,
                        Intent(context, MindfulBreathing::class.java)
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Spacer between items
                    ResponsiveBox(
                        context,
                        "4_7_8 Breathing",
                        R.drawable.fse,
                        boxHeight,
                        myFontFamily,
                        Intent(context, FSEBreath::class.java)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ResponsiveBox(
                        context,
                        "Box Breathing",
                        R.drawable.box,
                        boxHeight,
                        myFontFamily,
                        Intent(context, BoxBreathing::class.java)
                    )
                }
            }
        }
    }
}

/**
 * ResponsiveBox Composable Function
 *
 * This composable function creates a clickable box with an image and label.
 * The box's size adjusts based on the screen size, and clicking the box navigates to a new screen.
 *
 * Key Features:
 * - Dynamic image display with the ability to scale according to the box size.
 * - Text label overlaying the image.
 * - The box is clickable and navigates to the provided screen via an Intent.
 */
@Composable
fun ResponsiveBox(
    context: Context,
    label: String,
    imageRes: Int,
    boxHeight: Dp,
    fontFamily: FontFamily,
    intent: Intent
) {
    Box(
        modifier = Modifier
            .height(boxHeight) // Adjust box height dynamically
            .clickable {
                context.startActivity(intent) // Navigate when clicked
            }
            .padding(8.dp) // Padding around the box
    ) {
        // Display the image inside the box
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = imageRes),
            contentDescription = label,
            contentScale = ContentScale.Crop // Crop image to fit the box
        )
        // Display the label text overlaid on the image
        Text(
            text = label,
            color = Color.White,
            fontFamily = fontFamily,
            fontSize = if (boxHeight < 300.dp) 16.sp else 20.sp, // Adjust font size based on box height
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 6.dp) // Position label in the bottom-left corner
        )
    }
}

/**
 * Preview Composable Function
 *
 * A preview of the MainList composable function, showing the layout as it would appear in the app.
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JustBreathTheme {
        TheList() // Preview the list of exercises
    }
}
/*
 * This code defines the MainList screen, which displays a list of breathing exercises in a responsive layout.
 * The layout adapts based on the device's orientation (portrait or landscape).
 * - When the device is in portrait mode, the exercises are displayed in a column.
 * - In landscape mode, the exercises are displayed in a horizontal LazyRow, making better use of available space.
 *
 * The `TheList` composable manages this responsive layout, dynamically adjusting the size of the exercise boxes (`ResponsiveBox`)
 * based on screen orientation and other factors like screen width.
 *
 * An `IconButton` is placed at the top-right corner, and when clicked, it navigates to different screens based on a saved "screenId"
 * from SharedPreferences. The app supports multiple exercises like Mindful Breathing, 4_7_8 Breathing, and Box Breathing, each
 * associated with an image and a label.
 *
 * `ResponsiveBox` is a reusable component that wraps each exercise label and image, with an associated click event that triggers
 * navigation to the relevant activity. The `boxHeight` adjusts depending on the screen size, and text size is adjusted based on the box size.
 */
