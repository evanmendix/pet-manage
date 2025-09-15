package com.example.catfeeder

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.catfeeder.data.UserManager
import com.example.catfeeder.data.model.Feeding
import com.example.catfeeder.ui.main.MainUiState
import com.example.catfeeder.ui.main.MainViewModel
import com.example.catfeeder.ui.user.UserUiState
import com.example.catfeeder.ui.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatFeederTheme {
                val userState by userViewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val state = userState) {
                        is UserUiState.Loading -> LoadingScreen()
                        is UserUiState.NeedsSetup -> {
                            UserSetupScreen(onSetupComplete = { name ->
                                userViewModel.createUser(state.uid, name)
                            })
                        }
                        is UserUiState.UserExists -> {
                            userManager.currentUser = state.user
                            val mainUiState by mainViewModel.uiState.collectAsState()
                            MainScreen(
                                uiState = mainUiState,
                                onFeedButtonClick = { mainViewModel.onFeedMealClicked(it) }
                            )
                        }
                        is UserUiState.Error -> ErrorScreen(message = state.message)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    uiState: MainUiState,
    onFeedButtonClick: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var hasImage by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
            if (success) {
                onFeedButtonClick(imageUri)
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newUri = createImageUri(context)
                imageUri = newUri
                cameraLauncher.launch(newUri)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is MainUiState.Loading -> CircularProgressIndicator()
            is MainUiState.Success -> {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = { onFeedButtonClick(null) }) {
                        Text(text = "Feed Meal")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text(text = "Feed with Photo")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                FeedingHistory(feedings = uiState.feedings)
            }
            is MainUiState.Error -> {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val file = File(context.filesDir, "camera_photos/${UUID.randomUUID()}.jpg")
    file.parentFile?.mkdirs()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}
// ... other composables remain the same ...
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UserSetupScreen(onSetupComplete: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome! Please set up your profile.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter your name") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSetupComplete(name) },
            enabled = name.isNotBlank()
        ) {
            Text("Save")
        }
    }
}

@Composable
fun FeedingHistory(feedings: List<Feeding>) {
    LazyColumn {
        items(feedings) { feeding ->
            FeedingHistoryItem(feeding)
        }
    }
}

@Composable
fun FeedingHistoryItem(feeding: Feeding) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateString = formatter.format(Date(feeding.timestamp))

            Text(
                text = "Fed by: ${feeding.user?.name ?: "Unknown"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Time: $dateString",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Type: ${feeding.type}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CatFeederTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}
