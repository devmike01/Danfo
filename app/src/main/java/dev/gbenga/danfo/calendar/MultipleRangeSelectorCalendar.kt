package dev.gbenga.danfo.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.TemporalAccessor
import java.util.Calendar
import java.util.Locale


fun LazyListScope.dateItems(timeLength: Int, content: @Composable (Int) -> Unit){
    items(timeLength-1){
        val month = it + 1
        if (month <= 12){
            content(month)
        }
    }
}


@Composable
fun MultipleRangeSelectorCalendar(
    month: YearMonth,
    state: CalendarState
) {
    val years = Year.now()
    val monthDays = month.lengthOfMonth()
    val calendar = Calendar.getInstance()


    Column (modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)){


        /*
          for (day in 0 until daysInMonth){

                }
         */

        LazyColumn(modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {

            dateItems(monthDays){ monthIndex ->
                val month = years.atMonth(monthIndex)
                val monthsInYear = month.lengthOfMonth()
                val firstDay = month.atDay(1)
                val monthName = Month.of(monthIndex).getDisplayName(TextStyle.FULL, Locale.getDefault())


                val startOffset = firstDay.dayOfWeek.value % 7

                Column(modifier = Modifier.height(300.dp)) {


                    Text(
                        text = "$monthName ${years.value}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(20.dp))

                    LazyVerticalGrid(columns = GridCells.Fixed(7),
                        userScrollEnabled = false,
                        modifier = Modifier.wrapContentHeight()) {
                        items(7){ day ->
                            val dayOfWeek = DayOfWeek.of(day+1)
                            Text(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                textAlign = TextAlign.Center)
                        }
                    }

                    LazyVerticalGrid(columns = GridCells.Fixed(7),
                        userScrollEnabled = false,
                        modifier = Modifier.wrapContentHeight()) {

                        // Empty slots before first day
                        items(startOffset) {
                            Box(modifier = Modifier.size(48.dp))
                        }

                        items(monthsInYear) { dayIndex ->
                            val day = month.atDay(dayIndex + 1)
                            var prevWrapper: CalendarState.DateWrapper? = null
                            val pairMaker = remember { mutableListOf<CalendarState.DateWrapper>() }


                            CalendarDay(
                                date = day,
                                isSelected = state.isSelected(day),
                                isInProgress = state.isInProgress(day),
                                onClick = { state.onDateClicked(day) },
                                ranges = state.ranges.map { it.date },
                                startAndEnd = state.startAndEndRange.mapIndexed { index, wrapper ->
                                    var pair : Pair<LocalDate?, LocalDate?>? = null
                                    if ((pairMaker.size + 1) % 2 > 0){
                                        pairMaker.add(wrapper)
                                        pair = pairMaker.firstOrNull()?.date to pairMaker.lastOrNull()?.date
                                        pairMaker.clear()
                                    }else{
                                        pairMaker.add(wrapper)
                                    }

                                    pair ?: Pair(wrapper.date, wrapper.date)
                                }
                            )
                        }
                    }
                }
                }

        }


    }
}