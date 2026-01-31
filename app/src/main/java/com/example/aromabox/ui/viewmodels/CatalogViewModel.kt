package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.repository.PerfumeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Enum per le opzioni di ordinamento
enum class SortOption(val displayName: String) {
    NESSUNO("Nessuno"),
    PREZZO_CRESCENTE("Prezzo crescente"),
    PREZZO_DECRESCENTE("Prezzo decrescente")
}

class CatalogViewModel(
    private val repository: PerfumeRepository = PerfumeRepository()
) : ViewModel() {

    private val _perfumes = MutableStateFlow<List<Perfume>>(emptyList())
    val perfumes: StateFlow<List<Perfume>> = _perfumes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tutti")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Filtro ordinamento
    private val _selectedSort = MutableStateFlow(SortOption.NESSUNO)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    // Filtro prezzo (range min-max)
    private val _minPriceFilter = MutableStateFlow(1f)
    val minPriceFilter: StateFlow<Float> = _minPriceFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow(4f)
    val maxPriceFilter: StateFlow<Float> = _maxPriceFilter.asStateFlow()

    // Filtro marche (selezione multipla)
    private val _selectedBrands = MutableStateFlow<Set<String>>(emptySet())
    val selectedBrands: StateFlow<Set<String>> = _selectedBrands.asStateFlow()

    // Filtro genere (selezione multipla)
    private val _selectedGenders = MutableStateFlow<Set<String>>(emptySet())
    val selectedGenders: StateFlow<Set<String>> = _selectedGenders.asStateFlow()

    // Filtro famiglia olfattiva (selezione multipla)
    private val _selectedFamilies = MutableStateFlow<Set<String>>(emptySet())
    val selectedFamilies: StateFlow<Set<String>> = _selectedFamilies.asStateFlow()

    // Filtro note aromatiche (selezione multipla)
    private val _selectedNotes = MutableStateFlow<Set<String>>(emptySet())
    val selectedNotes: StateFlow<Set<String>> = _selectedNotes.asStateFlow()

    init {
        seedAndLoadPerfumes()
    }

    private fun seedAndLoadPerfumes() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.seedPerfumesIfNeeded()
            repository.getAllPerfumes().collect { perfumeList ->
                _perfumes.value = perfumeList
                _isLoading.value = false
            }
        }
    }

    // === SETTERS ===

    fun filterByCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSortOption(sortOption: SortOption) {
        _selectedSort.value = sortOption
    }

    fun setPriceRange(min: Float, max: Float) {
        _minPriceFilter.value = min
        _maxPriceFilter.value = max
    }

    fun setSelectedBrands(brands: Set<String>) {
        _selectedBrands.value = brands
    }

    fun setSelectedGenders(genders: Set<String>) {
        _selectedGenders.value = genders
    }

    fun setSelectedFamilies(families: Set<String>) {
        _selectedFamilies.value = families
    }

    fun setSelectedNotes(notes: Set<String>) {
        _selectedNotes.value = notes
    }

    fun clearFilters() {
        _selectedCategory.value = "Tutti"
        _selectedSort.value = SortOption.NESSUNO
        _minPriceFilter.value = 1f
        _maxPriceFilter.value = 4f
        _selectedBrands.value = emptySet()
        _selectedGenders.value = emptySet()
        _selectedFamilies.value = emptySet()
        _selectedNotes.value = emptySet()
    }

    // === GETTERS PER OPZIONI DISPONIBILI ===

    fun getAvailableBrands(): List<String> {
        return _perfumes.value
            .map { it.marca }
            .distinct()
            .sorted()
    }

    fun getAvailableNotes(): List<String> {
        return _perfumes.value
            .flatMap {
                it.noteOlfattive.noteDiTesta +
                        it.noteOlfattive.noteDiCuore +
                        it.noteOlfattive.noteDiFondo
            }
            .map { it.replaceFirstChar { c -> c.uppercase() } }
            .distinct()
            .sorted()
    }

    // === FILTRO PRINCIPALE ===

    fun getFilteredPerfumes(): List<Perfume> {
        var filtered = _perfumes.value

        // Filtro categoria
        if (_selectedCategory.value != "Tutti") {
            filtered = filtered.filter {
                it.categoria.equals(_selectedCategory.value, ignoreCase = true)
            }
        }

        // Filtro prezzo
        filtered = filtered.filter {
            it.prezzo >= _minPriceFilter.value && it.prezzo <= _maxPriceFilter.value
        }

        // Filtro marche
        if (_selectedBrands.value.isNotEmpty()) {
            filtered = filtered.filter {
                _selectedBrands.value.contains(it.marca)
            }
        }

        // Filtro famiglia olfattiva
        if (_selectedFamilies.value.isNotEmpty()) {
            filtered = filtered.filter { perfume ->
                _selectedFamilies.value.any { family ->
                    perfume.categoria.equals(family, ignoreCase = true)
                }
            }
        }

        // Filtro note aromatiche
        if (_selectedNotes.value.isNotEmpty()) {
            filtered = filtered.filter { perfume ->
                val allNotes = (perfume.noteOlfattive.noteDiTesta +
                        perfume.noteOlfattive.noteDiCuore +
                        perfume.noteOlfattive.noteDiFondo)
                    .map { it.replaceFirstChar { c -> c.uppercase() } }
                _selectedNotes.value.any { note ->
                    allNotes.contains(note)
                }
            }
        }

        // Ordinamento
        filtered = when (_selectedSort.value) {
            SortOption.PREZZO_CRESCENTE -> filtered.sortedBy { it.prezzo }
            SortOption.PREZZO_DECRESCENTE -> filtered.sortedByDescending { it.prezzo }
            SortOption.NESSUNO -> filtered
        }

        return filtered
    }

    fun hasActiveFilters(): Boolean {
        return _selectedSort.value != SortOption.NESSUNO ||
                _minPriceFilter.value > 1f ||
                _maxPriceFilter.value < 4f ||
                _selectedBrands.value.isNotEmpty() ||
                _selectedGenders.value.isNotEmpty() ||
                _selectedFamilies.value.isNotEmpty() ||
                _selectedNotes.value.isNotEmpty()
    }
}