package dev.gbenga.danfo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.gbenga.danfo.calendar.CalendarState
import dev.gbenga.danfo.calendar.MultipleRangeSelectorCalendar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.YearMonth
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Scaffold {
                val navHostController = rememberNavController()
                NavHost(navHostController, startDestination = Routes.Home,
                    modifier = Modifier.padding(it)){
                    composable<Routes.Home> {
                        DanfoDemo(navHostController)
                    }

                    composable<Routes.NextPage> {
                        NextPageScreen(navHostController)
                    }
                }
            }
           // NavigationEventHistory

        }
    }
}


@Serializable
sealed interface Routes{

    @Serializable
    object Home: Routes

    @Serializable
    object NextPage : Routes
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DanfoDemo(navHostController: NavHostController){
    val messenger = rememberMessenger()
    val state = remember { CalendarState() }

    MultipleRangeSelectorCalendar(
        month = YearMonth.now(),
        state = state
    )

}

@Composable
fun NextPageScreen(navHostController: NavHostController){
    val messenger = rememberMessenger()
    val userFlow = navHostController.previousBackStackEntry?.savedStateHandle?.getStateFlow("msg", "null")
    var msg by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        messenger.readMessage().collect { message ->
            msg = message
        }
    }
    Text(text = msg ?: "MANGO", style = MaterialTheme.typography.headlineLarge)
}

@Composable
fun JoltAnimation(){
    val alphabets = remember { "abcdefghijklmnopqrstuvwxyz0" }
    var charAnimateVal by remember { mutableStateOf(1) }
    var charDelay by remember { mutableStateOf(0) }
    var chars = remember { mutableStateListOf(26,26,26,26,26,26,26) }
    val stack = remember { mutableStateListOf<Int>(8,13,0,17,4, 0,3) }
    val coroutineScope = rememberCoroutineScope()
    var index by remember { mutableStateOf(0) }

   LaunchedEffect(Unit) {
       if (stack.isNotEmpty() && chars.isNotEmpty()){
           delay(200)
           chars[index] = stack.removeAt(0)
           index += 1
       }
   }


    Row {
        val headlineLarge = MaterialTheme.typography.headlineLarge
        chars.forEachIndexed { colorIndex, it ->

            AnimatedContent(
                targetState = it,

                transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (colorIndex % 2 ==0) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInVertically { height -> height } + fadeIn(animationSpec = tween()) togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }, label = "animated content"
            ) { targetCount ->
                Text(text = alphabets[targetCount].toString(),
                    style = headlineLarge.copy(
                        //fontFamily = calculatorFont,
                        color = headlineLarge.color.takeIf {
                            colorIndex <= 2
                        } ?: Color.Red,
                    ))

                /*
                style = headlineLarge.copy(
                        fontFamily = calculatorFont,
                        color = headlineLarge.color.takeIf {
                            index <= 2
                        } ?: primaryColor,
                    )
                 */
            }

        }

    }
}




@Preview
@Composable
fun PreviewJoltAnimation(){
    JoltAnimation()
}