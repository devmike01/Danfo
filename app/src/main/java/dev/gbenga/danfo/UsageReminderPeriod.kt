package dev.gbenga.danfo

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.roundToInt

@Composable
fun UsageReminderPeriodDialog(periods: List<Period>,
                              onDismissRequest: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismissRequest) {
        UsageReminderPeriod( periods)
    }
}
@Composable
fun UsageReminderPeriod(periods: List<Period>) {

    val textColor = MaterialTheme.typography.headlineSmall.color
    val itemHeightPx = with(LocalDensity.current) { 60.dp.toPx() }
    var layoutHeight by remember { mutableStateOf(0) }
    var offsetY by remember { mutableStateOf(0f) }
    val itemCount = periods.size

    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    offsetY += delta //.coerceIn(-layoutHeight.toFloat(), layoutHeight.toFloat())
                }
            ).onGloballyPositioned{
                layoutHeight = it.size.height
            },
        contentAlignment = Alignment.Center
    ) {

        val centerIndex = ((-offsetY / itemHeightPx).toInt())

        for (i in -1..1) {

            val index = (centerIndex + i).mod(itemCount)
            val item = periods[index]

            val y = i * itemHeightPx + (offsetY % itemHeightPx)

            val distanceFromCenter = kotlin.math.abs(y)

            val isCentered = distanceFromCenter < itemHeightPx / 2.3

            val scale = if (isCentered) 1f else 0.85f
            val alpha = if (isCentered) 1f else 0.4f

            Text(
                text = item.name,
                modifier = Modifier
                    .offset { IntOffset(0, y.roundToInt()) }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = if (isCentered) textColor else textColor.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
@Preview
fun PreviewUsageReminderPeriod(){
    UsageReminderPeriod(periods = listOf(
        Period.Daily, Period.Monthly, Period.Weekly
    ),)
}

enum class Period(timeInMillis: Long){
    Weekly(60 * 60 * 1000 * 24 * 7),
    Daily(60 * 60 * 1000 * 24),
    Monthly(60 * 60 * 1000 * 24 *30),
    SelectPeriod(0L)
}