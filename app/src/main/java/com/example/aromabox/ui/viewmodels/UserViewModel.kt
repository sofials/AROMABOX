package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.data.model.User
import com.example.aromabox.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true

            val uid = auth.currentUser?.uid
            if (uid != null) {
                repository.getUserById(uid).collect { user ->
                    _currentUser.value = user
                    _isLoading.value = false
                }
            } else {
                _currentUser.value = null
                _isLoading.value = false
            }
        }
    }

    fun getCurrentUserId(): String? {
        return _currentUser.value?.uid
    }

    fun getFavoriteIds(): List<String> {
        return _currentUser.value?.preferiti ?: emptyList()
    }

    fun hasCompletedQuiz(): Boolean {
        return _currentUser.value?.profiloOlfattivo != null
    }

    // ✅ NUOVA FUNZIONE: Aggiorna nome e cognome
    fun updateUserProfile(nome: String, cognome: String) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                // Aggiorna solo nome e cognome
                repository.updateUserField(userId, "nome", nome)
                repository.updateUserField(userId, "cognome", cognome)

                // Ricarica l'utente per aggiornare lo stato
                loadCurrentUser()

            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornare il profilo: ${e.message}"
            }
        }
    }

    fun toggleFavorite(perfumeId: String) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                val currentFavorites = getFavoriteIds().toMutableList()

                if (currentFavorites.contains(perfumeId)) {
                    currentFavorites.remove(perfumeId)
                } else {
                    currentFavorites.add(perfumeId)
                }

                repository.updateFavorites(userId, currentFavorites)

            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornare i preferiti: ${e.message}"
            }
        }
    }

    fun updateProfiloOlfattivo(profilo: ProfiloOlfattivo) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                repository.updateProfiloOlfattivo(userId, profilo)
                loadCurrentUser()
            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornare il profilo: ${e.message}"
            }
        }
    }

    fun updateWallet(amount: Double) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                val currentWallet = _currentUser.value?.wallet ?: 0.0
                repository.updateWallet(userId, currentWallet + amount)
            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornare il portafoglio: ${e.message}"
            }
        }
    }

    fun updateConnectionStatus(isConnected: Boolean) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                repository.updateConnectionStatus(userId, isConnected)
            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornare lo stato: ${e.message}"
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}