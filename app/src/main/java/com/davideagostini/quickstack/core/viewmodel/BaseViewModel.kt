package com.davideagostini.quickstack.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel base for one-shot UI messages.
 *
 * Transient feedback such as snackbars lives outside screen state so recomposition and
 * process restoration do not accidentally replay it as persistent UI.
 */
open class BaseViewModel : ViewModel() {
    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    protected fun emitMessage(message: String) {
        viewModelScope.launch {
            _messages.emit(message)
        }
    }
}
