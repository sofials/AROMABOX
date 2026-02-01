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

    fun decrementInventory(
        distributorId: String,
        perfumeId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.decrementInventory(distributorId, perfumeId)
            if (success) {
                // Aggiorna lo stato locale
                _distributors.value = _distributors.value.map { distributor ->
                    if (distributor.id == distributorId) {
                        val newInventario = distributor.inventario.toMutableMap()
                        val currentQty = newInventario[perfumeId] ?: 0
                        if (currentQty > 0) {
                            newInventario[perfumeId] = currentQty - 1
                        }
                        distributor.copy(inventario = newInventario)
                    } else {
                        distributor
                    }
                }
                onSuccess()
            } else {
                onError("Prodotto non disponibile")
            }
        }
    }
}