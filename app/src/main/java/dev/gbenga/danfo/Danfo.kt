package dev.gbenga.danfo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

object Danfo{

    private val _passengers = mutableMapOf<String, Any>()

    private val _event = MutableSharedFlow<MutableMap<String, Any>>(
        replay = 1
    )

    fun <T> emit(key: String, value: T){
        _passengers[key] = value as Any
        _event.tryEmit(_passengers)
    }

    fun <T> subscribe(key: String): Flow<T?> {
        return _event.map { it.remove(key) as? T }
    }

}