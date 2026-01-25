package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.repository.PerfumeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: PerfumeRepository = PerfumeRepository()
) : ViewModel() {

    private val _perfumes = MutableStateFlow<List<Perfume>>(emptyList())
    val perfumes: StateFlow<List<Perfume>> = _perfumes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tutti")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        seedAndLoadPerfumes()
    }

    private fun seedAndLoadPerfumes() {
        viewModelScope.launch {
            _isLoading.value = true

            // ✅ Popola Firebase se vuoto
            repository.seedPerfumesIfNeeded()

            // Carica i profumi
            repository.getAllPerfumes().collect { perfumeList ->
                _perfumes.value = perfumeList
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getFilteredPerfumes(): List<Perfume> {
        return if (_selectedCategory.value == "Tutti") {
            _perfumes.value
        } else {
            _perfumes.value.filter {
                it.categoria.equals(_selectedCategory.value, ignoreCase = true)
            }
        }
    }
}