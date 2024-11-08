package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.justbreath.ui.theme.JustBreathTheme

/**
 * SplashActivity serves as the entry point to the app. It handles the display of the splash screen
 * and navigation logic based on the app's version and user preferences.
 */
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve screenId from SharedPreferences to determine the target screen to navigate to
        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val screenId = sharedPreferences.getInt("screenId", 0)

        // Check Android version to determine whether to use the custom splash screen or skip it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 and above: Skip custom splash screen and directly navigate to the target screen
            navigateToScreen(screenId)
        } else {
            // Android 11 and below: Show custom splash screen with a delayed navigation
            setContent {
                JustBreathTheme {
                    navigateToScreen(screenId) // Navigate to screen immediately after splash screen
                }
            }

            // Use Handler to delay the navigation to MainActivity (or target screen) by 500ms
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Close SplashActivity after navigating
            }, 500) // Adjust delay duration as necessary
        }
    }

    /**
     * Navigate to the appropriate screen based on the screenId.
     *
     * @param screenId An integer value stored in SharedPreferences that determines which screen
     *                 the app should navigate to (e.g., MainActivity, FavMindfulBreathing).
     */
    private fun navigateToScreen(screenId: Int) {
        val intent = when (screenId) {
            0 -> Intent(this, MainActivity::class.java)
            1 -> Intent(this, FavMindfulBreathing::class.java)
            2 -> Intent(this, FavFSEBreath::class.java)
            3 -> Intent(this, FavBoxBreathing::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish() // Close SplashActivity after navigating to the next screen
    }
}

/**
 * Composable function that displays the content of the splash screen.
 * The content consists of an image centered in a background with a light color.
 *
 * @param modifier Modifier to adjust the appearance and layout of the composable.
 */
@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(240, 247, 254)), // Light background color
        contentAlignment = Alignment.Center // Center the content (image)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_round), // Splash screen logo image
            contentDescription = "Splash Screen Logo" // Accessibility description
        )
    }
}

/**
 * Preview composable function to visualize the splash screen content during development.
 * This helps to quickly check the UI layout without running the app.
 */
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    JustBreathTheme {
        SplashScreenContent() // Preview for the splash screen
    }
}

/*
 * SplashActivity is the main entry point of the app and handles the splash screen logic.
 * The activity first retrieves the `screenId` from SharedPreferences to decide which screen to navigate to after the splash.
 *
 * The app checks the Android version:
 * - On Android 12 (API level 31) and above, it directly navigates to the appropriate screen without showing a custom splash screen.
 * - On older versions (Android 11 and below), a custom splash screen is shown with a delay before navigating to the target screen.
 *
 * The `navigateToScreen()` function is responsible for determining the target screen based on the `screenId`. It launches the appropriate activity and closes the splash screen.
 * A Handler is used to add a short delay (500ms) before transitioning to the `MainActivity`.
 *
 * The `SplashScreenContent()` composable defines the splash screen UI, which includes a centered image on a light background.
 *
 * The `SplashScreenPreview()` composable is a preview function that renders the splash screen layout for preview during development.
 */
