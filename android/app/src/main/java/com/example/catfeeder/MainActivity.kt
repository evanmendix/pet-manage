package com.example.catfeeder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.catfeeder.ui.feeding.FeedingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A basic theme wrapper can be added later in a ui/theme/Theme.kt file
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FeedingScreen()
                }
            }
        }
    }
}
