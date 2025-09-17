package com.example.catfeeder.ui.feeding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catfeeder.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

@Composable
fun FeedingScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Display current status
                    Text(text = stringResource(R.string.current_status), style = MaterialTheme.typography.headlineMedium)
                    uiState.currentStatus?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
                        val formattedTime = sdf.format(Date(it.timestamp))
                        val typeString = when (it.type) {
                            "meal" -> stringResource(R.string.meal)
                            "snack" -> stringResource(R.string.snack)
                            else -> it.type
                        }
                        Text(stringResource(R.string.last_fed_by, it.userId, typeString))
                        Text(stringResource(R.string.feeding_time, formattedTime))
                    } ?: Text(stringResource(R.string.no_feeding_records))

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Button(
                        onClick = { viewModel.addFeeding("meal") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.feed_meal))
                    }
                    Button(
                        onClick = { viewModel.addFeeding("snack") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.feed_snack))
                    }

                    // Error Handling
                    if (uiState.showDuplicateFeedingDialog) {
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissDuplicateFeedingDialog() },
                            title = { Text(stringResource(R.string.duplicate_feeding_title)) },
                            text = { Text(stringResource(R.string.duplicate_feeding_message)) },
                            confirmButton = {
                                Row(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { viewModel.dismissDuplicateFeedingDialog() }
                                    ) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                    TextButton(
                                        onClick = {
                                            viewModel.overwriteLastMeal("meal")
                                            viewModel.dismissDuplicateFeedingDialog()
                                        }
                                    ) {
                                        Text(stringResource(R.string.overwrite))
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.addFeeding("meal", force = true)
                                            viewModel.dismissDuplicateFeedingDialog()
                                        }
                                    ) {
                                        Text(stringResource(R.string.confirm))
                                    }
                                }
                            }
                        )
                    }

                    uiState.error?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.error_message, error), color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
