package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.ReportModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione delle segnalazioni.
 *
 * Segue le linee guida del Prof. Malnati:
 * - Estende ViewModel (lifecycle-conscious)
 * - Espone dati tramite StateFlow (per Jetpack Compose)
 * - Propaga richieste di modifica al Model sottostante
 * - NON contiene riferimenti a View, Context, Fragment
 * - Sopravvive ai cambi di configurazione
 * - Usa viewModelScope per coroutines
 */
class ReportViewModel : ViewModel() {

    // ===== Stato del form =====

    private val _selectedType = MutableStateFlow("bug")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    // ===== Stato dell'invio =====

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** null = nessun risultato, true = successo, false = errore */
    private val _sendResult = MutableStateFlow<Boolean?>(null)
    val sendResult: StateFlow<Boolean?> = _sendResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ===== Azioni dalla View =====

    fun onTypeSelected(type: String) {
        _selectedType.value = type
    }

    fun onMessageChanged(newMessage: String) {
        _message.value = newMessage
    }

    /**
     * Invia la segnalazione tramite il Model singleton.
     * Validazione nel ViewModel (logica di presentazione),
     * persistenza nel Model (logica di business).
     */
    fun sendReport() {
        val currentMessage = _message.value.trim()

        // Validazione (logica di presentazione)
        if (currentMessage.isBlank()) {
            _errorMessage.value = "Inserisci un messaggio per la segnalazione"
            return
        }

        if (currentMessage.length < 10) {
            _errorMessage.value = "Il messaggio deve essere di almeno 10 caratteri"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _sendResult.value = null

            val result = ReportModel.sendReport(
                type = _selectedType.value,
                message = currentMessage
            )

            result.fold(
                onSuccess = {
                    _sendResult.value = true
                    // Reset form dopo invio riuscito
                    _message.value = ""
                    _selectedType.value = "bug"
                },
                onFailure = { error ->
                    _sendResult.value = false
                    _errorMessage.value = error.message ?: "Errore durante l'invio"
                }
            )

            _isLoading.value = false
        }
    }

    /**
     * Reset dello stato del risultato (dopo che l'utente ha visto il feedback).
     */
    fun clearResult() {
        _sendResult.value = null
        _errorMessage.value = null
    }
}