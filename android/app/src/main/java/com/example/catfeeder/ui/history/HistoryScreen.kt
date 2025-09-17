package com.example.catfeeder.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.catfeeder.R
import com.example.catfeeder.data.model.Feeding
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TimeRangeSelector(onTimeRangeSelected = { viewModel.loadHistory(it) })
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.feedings) { feeding ->
                            FeedingCard(feeding)
                        }
                    }
                }
                uiState.error?.let {
                    Text(
                        text = stringResource(R.string.error_message, it),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun TimeRangeSelector(onTimeRangeSelected: (TimeRange) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onTimeRangeSelected(TimeRange.LAST_24_HOURS) }) {
            Text("24H")
        }
        Button(onClick = { onTimeRangeSelected(TimeRange.LAST_7_DAYS) }) {
            Text("7D")
        }
        Button(onClick = { onTimeRangeSelected(TimeRange.LAST_30_DAYS) }) {
            Text("30D")
        }
    }
}

@Composable
fun FeedingCard(feeding: Feeding) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT+8")
            val formattedTime = sdf.format(Date(feeding.timestamp))
            val typeString = when (feeding.type) {
                "meal" -> stringResource(R.string.meal)
                "snack" -> stringResource(R.string.snack)
                else -> feeding.type
            }
            Text(text = stringResource(R.string.fed_by, feeding.userId, typeString), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(R.string.feeding_time, formattedTime), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
