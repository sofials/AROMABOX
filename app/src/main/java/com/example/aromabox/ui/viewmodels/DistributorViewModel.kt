package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.Distributor
import com.example.aromabox.data.repository.DistributorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DistributorViewModel(
    private val repository: DistributorRepository = DistributorRepository()
) : ViewModel() {

    private val _distributors = MutableStateFlow<List<Distributor>>(emptyList())
    val distributors: StateFlow<List<Distributor>> = _distributors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedDistributor = MutableStateFlow<Distributor?>(null)
    val selectedDistributor: StateFlow<Distributor?> = _selectedDistributor.asStateFlow()

    init {
        loadDistributors()
    }

    private fun loadDistributors() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.seedDistributorsIfNeeded()
            repository.getAllDistributors().collect { distributorList ->
                _distributors.value = distributorList
                _isLoading.value = false
            }
        }
    }

    fun selectDistributor(distributor: Distributor?) {
        _selectedDistributor.value = distributor
    }

    fun getActiveDistributors(): List<Distributor> {
        return _distributors.value.filter { it.attivo }
    }

    fun getDistributorsWithPerfume(perfumeId: String): List<Distributor> {
        return _distributors.value.filter { distributor ->
            distributor.attivo && distributor.getDisponibilita(perfumeId) > 0
        }
    }

    fun getDisponibilita(distributorId: String, perfumeId: String): Int {
        return _distributors.value
            .find { it.id == distributorId }
            ?.getDisponibilita(perfumeId) ?: 0
    }

    /**
     * Decrementa l'inventario di un profumo presso un distributore.
     * L'aggiornamento dello stato locale avviene automaticamente tramite
     * il Flow Firebase in loadDistributors(), quindi NON aggiorniamo
     * manualmente _distributors qui per evitare doppio decremento.
     */
    fun decrementInventory(
        distributorId: String,
        perfumeId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.decrementInventory(distributorId, perfumeId)
            if (success) {
                // NON aggiorniamo lo stato locale manualmente!
                // Il Flow Firebase in loadDistributors() riceverà automaticamente
                // l'aggiornamento da Firebase e aggiornerà _distributors
                onSuccess()
            } else {
                onError("Prodotto non disponibile")
            }
        }
    }
}