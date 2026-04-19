package dev.gbenga.danfo.calendar

import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AtomicReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger

class CalendarState {

    var ranges = mutableStateListOf<DateWrapper>()
        private set

    var preselects = mutableListOf(
        DateWrapper(thisDateOf(1, 1)),
        DateWrapper(thisDateOf(1, 10)),
        DateWrapper(thisDateOf(1, 25)),
        DateWrapper(thisDateOf(1, 30))
    )

    var activeStart by mutableStateOf<LocalDate?>(null)
        private set

    val startAndEndRange = mutableListOf<DateWrapper>()

    init {
        populate()

    }

    fun thisDateOf(month: Int, dayOfMonth: Int): LocalDate{
        return LocalDate.of(2026, month, dayOfMonth)
    }

    fun populate(){
        val pairList = mutableListOf<LocalDate>()
        preselects.forEachIndexed { index, wrapper ->
            if (index % 2 > 0){
                prepopulate(pairList.first(), wrapper.date)
                pairList.clear()
            }else{
                pairList.add(wrapper.date)
            }
            startAndEndRange.add(wrapper.copy(isPreselect = true))
        }
    }

    private fun prepopulate(startDate: LocalDate, endDate: LocalDate){
        val calendar = Calendar.getInstance()
        val difference  = (ChronoUnit.DAYS.between(
          startDate, endDate).toInt() + 1)
        var startDateIndex = startDate.dayOfMonth
        var endIndex = (difference + startDate.dayOfMonth)
        calendar.timeInMillis = endDate.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli() ?: 0L



        while (startDateIndex < endIndex){
            val month = YearMonth.of(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1)
            val daysInMonth = month.lengthOfMonth()

            // days = 31 but daysInMonth = 30,
            if (startDateIndex > daysInMonth){
                endIndex -= daysInMonth // date is already in the next month by one day
                startDateIndex = 1
                calendar.add(Calendar.MONTH, 1)
            }

            calendar.set(Calendar.DAY_OF_MONTH, startDateIndex)
            ranges.add(DateWrapper.preselect(calendar.time.toLocalDate()))

            startDateIndex += 1
        }
    }

    fun onDateClicked(newDate: LocalDate) {
        startAndEndRange.add(DateWrapper(newDate))
        addDataToRanges(newDate)

    }

    fun addDataToRanges(newDate: LocalDate){
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = getLastSelectedDate()?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli() ?: 0L
        if(getLastSelectedDate()?.isAfter(newDate) ==true
            || newDate == getLastSelectedDate()
            || startAndEndRange.count { !it.isPreselect } > 2){

            // Remove what the user only selected at this time
            ranges.removeAll { !it.isPreselect }
            startAndEndRange.removeAll { !it.isPreselect }

            val dateWrapper = DateWrapper(newDate)
            startAndEndRange.add(dateWrapper)
            ranges.add(dateWrapper)
        } else if (isNotSelected(newDate) && isLater(newDate)){
            val endSelectedDayOfMonth = getLastSelectedDate()
            var isNewMonth = false
            var days = endSelectedDayOfMonth?.dayOfMonth?: 1 // March days = 30th
            val diffDays = (ChronoUnit.DAYS.between(endSelectedDayOfMonth,
                newDate).toInt() + 1) // May 20; diff = 50
            var ceiling = (diffDays + ((endSelectedDayOfMonth?.dayOfMonth?: 1)))
            //var dayIndex = 1
            while (days < ceiling){
                val month = YearMonth.of(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1)
                val daysInMonth = month.lengthOfMonth()

                // days = 31 but daysInMonth = 30,
                if (days > daysInMonth){
                    ceiling -= daysInMonth // date is already in the next month by one day
                    days = 1
                    isNewMonth =true
                }
                if (isNewMonth){
                    calendar.add(Calendar.MONTH, 1)
                    isNewMonth = false
                }
                calendar.set(Calendar.DAY_OF_MONTH, days)
                ranges.add(DateWrapper(calendar.time.toLocalDate()))

                days += 1
              //  dayIndex += 1
            }


        }else {
            ranges.add(DateWrapper(newDate))
        }
    }

    fun Date.toLocalDate(): LocalDate = toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    private fun getLastSelectedDate(): LocalDate?{
        return ranges.lastOrNull { !it.isPreselect }?.date
    }

    private fun isLater(newDate: LocalDate): Boolean{
        return getLastSelectedDate()?.isBefore(newDate) == true
    }

    fun isNotSelected(date: LocalDate): Boolean {
        return !isSelected(date)
    }
    fun isSelected(date: LocalDate): Boolean {
        return ranges.any { it.date == date }
    }

    fun isInProgress(date: LocalDate): Boolean {
        val start = activeStart ?: return false
        return if (date >= start) {
            date >= start
        } else {
            date <= start
        }
    }

    fun clear() {
        ranges.clear()
        activeStart = null
    }

    data class DateWrapper(val date: LocalDate,
                           val isPreselect: Boolean = false){
        companion object{
            fun preselect(date: LocalDate,): DateWrapper{
              return  DateWrapper(date, true)
            }
        }
    }
}

@Composable
fun CalendarDay(
    startAndEnd: List<Pair<LocalDate?, LocalDate?>>,
    ranges: List<LocalDate>,
    date: LocalDate,
    isSelected: Boolean,
    isInProgress: Boolean,
    onClick: () -> Unit
) {

    var modifier = Modifier
        .wrapContentHeight()
        .fillMaxWidth()

    if(ranges.contains(date)){
        modifier = modifier.background(Color.Green)
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Color.Black
                    .takeIf { startAndEnd.any { it.first == date || it.second == date } }
                    ?: Color.Transparent)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}


data class DateRange(
    val start: LocalDate,
    val end: LocalDate
) {
    fun contains(date: LocalDate): Boolean {
        return !date.isBefore(start) && !date.isAfter(end)
    }
}