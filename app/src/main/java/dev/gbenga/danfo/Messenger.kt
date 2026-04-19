package dev.gbenga.danfo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

interface Messenger {

    fun sendMessage(msg: String)

    fun readMessage(): Flow<String>

}

@Composable
fun rememberMessenger(): Messenger{
    val context = LocalContext.current
    return remember { MessengerImpl(context
        .getSharedPreferences("message", Context.MODE_PRIVATE)) }
}

class MessengerImpl(private val sharedPreferences: SharedPreferences) : Messenger{

    companion object{
        const val MESSENGER_KEY = "MessengerImpl.msg";
    }

    private val _event = MutableSharedFlow<String>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 1
    )

    override fun sendMessage(msg: String) {
        sharedPreferences.edit {
            putString(MESSENGER_KEY, msg)
        }
        _event.tryEmit(msg)
    }

    override fun readMessage(): Flow<String> = flow{
        // User fires from another
        sharedPreferences.getString(MESSENGER_KEY, null)?.let {
            emit(it)
            sharedPreferences.edit { remove(MESSENGER_KEY) }
        }
        // Handles user is on the page
        _event.asSharedFlow().collect { msg ->
            sharedPreferences.edit{ remove(MESSENGER_KEY)}
            emit(msg)
        }

    }

}