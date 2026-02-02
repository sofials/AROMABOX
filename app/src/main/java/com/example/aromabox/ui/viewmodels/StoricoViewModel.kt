package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.Order
import com.example.aromabox.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestione dello storico ordini.
 * Segue il pattern MVVM con StateFlow per la comunicazione con la View.
 */
class StoricoViewModel : ViewModel() {

    private val userRepository = UserRepository()

    // ==================== STATO PRIVATO (Mutable) ====================

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _selectedTab = MutableStateFlow(StoricoTab.DA_RITIRARE)

    // ==================== STATO PUBBLICO (Read-only) ====================

    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()
    val selectedTab: StateFlow<StoricoTab> = _selectedTab.asStateFlow()

    // ==================== INIZIALIZZAZIONE ====================

    init {
        loadOrders()
    }

    // ==================== METODI PUBBLICI ====================

    fun loadOrders() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                userRepository.getUserOrders(currentUserId).collect { ordersList ->
                    _orders.value = ordersList.sortedByDescending { it.timestamp }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Errore nel caricamento degli ordini: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun selectTab(tab: StoricoTab) {
        _selectedTab.value = tab
    }

    fun markAsRitirato(orderId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                userRepository.markOrderAsRitirato(currentUserId, orderId)
                _orders.value = _orders.value.map { order ->
                    if (order.orderId == orderId) {
                        order.copy(
                            ritirato = true,
                            dataRitiro = System.currentTimeMillis()
                        )
                    } else {
                        order
                    }
                }
            } catch (e: Exception) {
                _error.value = "Errore nell'aggiornamento: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

enum class StoricoTab {
    DA_RITIRARE,
    RITIRATI
}