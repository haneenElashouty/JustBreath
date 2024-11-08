package com.example.justbreath

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.tooling.preview.Preview
import com.example.justbreath.ui.theme.JustBreathTheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

// MainActivity: The entry point of the app
class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the main content of the app when screenId is 0
        setContent {
            JustBreathTheme {
                MainScreen()  // Composable for the main screen
            }
        }
    }
}

// MainScreen Composable: This is the splash screen and home page for the app
@Composable
fun MainScreen() {
    // Define the custom font family used for text
    val myFontFamily = FontFamily(
        Font(R.font.sansita_regular),  // Specify the font resource
    )

    // Get context for navigation and UI updates
    val context = LocalContext.current

    // Main Box that holds the UI components
    Box(
        modifier = Modifier
            .fillMaxSize()  // Fill the entire screen size
            .background(color = Color.White)  // Set the background color to white
    ) {
        // Hamburger Menu Icon - Clicking navigates to the MainList screen
        IconButton(
            onClick = {
                val intent = Intent(context, MainList::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)  // Position it in the top right corner
                .padding(16.dp)  // Add padding around the icon
        ) {
            Icon(
                painter = painterResource(id = R.drawable.vector_1),  // Replace with your hamburger icon
                contentDescription = "Menu",  // Description for accessibility
                tint = Color(25, 43, 82, 255)  // Icon color
            )
        }

        // Center Content: Displays a heart image and some text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,  // Align horizontally to the center
            verticalArrangement = Arrangement.Center,  // Vertically center the content
            modifier = Modifier.fillMaxSize()  // Fill the entire screen
        ) {
            // Heart image displayed in the center
            Image(
                painter = painterResource(id = R.drawable.vector),  // Replace with your heart image
                contentDescription = "Heart",  // Description for accessibility
                modifier = Modifier.size(64.dp)  // Set the size of the heart image
            )

            Spacer(modifier = Modifier.height(16.dp))  // Space between the image and text

            // Text description below the heart image
            Text(
                text = "Add your favourite exercises \nfor quick access",  // Text message
                style = MaterialTheme.typography.headlineMedium,  // Headline style for text
                fontSize = 20.sp,  // Font size for the text
                textAlign = TextAlign.Center,  // Align the text in the center
                fontFamily = myFontFamily,  // Apply the custom font
                color = Color(25, 43, 82, 255)  // Set the text color
            )
        }

        // Bottom Image Button: Background image for the bottom section of the screen
        Image(
            painter = painterResource(id = R.drawable.backgroud_floating_elements_1),  // Replace with your image
            contentDescription = "Bottom Background",  // Description for accessibility
            modifier = Modifier
                .fillMaxWidth()  // Make the image fill the entire width of the screen
                .align(Alignment.BottomCenter)  // Align it at the bottom center
            , contentScale = ContentScale.Crop  // Scale the image to crop appropriately
        )
    }
}

// Preview for the MainScreen Composable to visualize it during development
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    JustBreathTheme {
        MainScreen()  // Preview the MainScreen composable
    }
}

/*
 * MainActivity is the entry point of the app and hosts the main screen. The screen is built using Jetpack Compose and includes various UI components arranged within a Box layout. The primary layout consists of three parts:
 * 1. A hamburger menu icon positioned at the top-right corner, which navigates to the MainList screen when clicked.
 * 2. A central Column that displays a heart image and a motivational text, both aligned in the center of the screen. The text encourages users to add their favorite exercises for quick access, and a custom font is applied to it for styling.
 * 3. A background image placed at the bottom of the screen, which stretches across the entire width of the screen and adds a decorative touch.
 *
 * The screen utilizes a Box modifier with `fillMaxSize()` to ensure the content occupies the full screen. The UI components are placed using appropriate alignment and padding to create a well-structured layout. Colors, fonts, and icons are defined to enhance the visual appeal. The navigation to the MainList screen is handled using an Intent triggered by the hamburger menu icon.
 *
 * The `MainActivityPreview` function is a composable preview that helps visualize the layout of the main screen during development. This allows quick feedback and testing of the UI components before implementing further logic.
 *
 * The code is modular, making it easy to adjust specific UI elements without affecting others. The use of clear comments throughout the code improves maintainability and readability for future developers working on the project.
 */
