package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.aromabox.data.model.Offer
import com.example.aromabox.data.model.OffersModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel per le offerte.
 * Seguendo le linee guida del docente:
 * - Espone proprietà osservabili (StateFlow) che la View osserva
 * - Trasforma i dati del Model per il presentation layer
 * - Non contiene riferimenti a View, Context o lifecycle
 * - Pattern: _mutableState privato, state pubblico read-only
 */
class OffersViewModel : ViewModel() {

    // Pattern: proprietà privata mutable, proprietà pubblica read-only
    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadOffers()
    }

    /**
     * Carica le offerte dal Model (fonte di verità).
     */
    private fun loadOffers() {
        _isLoading.value = true
        _offers.value = OffersModel.getOffers()
        _isLoading.value = false
    }
}