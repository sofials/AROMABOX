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

    private var isAuthInitialized = false

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser

        if (!isAuthInitialized) {
            isAuthInitialized = true

            if (user != null) {
                loadUserData(user.uid)
            } else {
                _currentUser.value = null
                _isLoading.value = false
            }
        } else {
            if (user != null) {
                loadUserData(user.uid)
            } else {
                _currentUser.value = null
                _isLoading.value = false
            }
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun loadUserData(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getUserById(uid).collect { user ->
                _currentUser.value = user
                _isLoading.value = false
            }
        }
    }

    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            loadUserData(uid)
        } else {
            _currentUser.value = null
            _isLoading.value = false
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

    fun updateUserProfile(nome: String, cognome: String) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                repository.updateUserField(userId, "nome", nome)
                repository.updateUserField(userId, "cognome", cognome)
                loadCurrentUser()
            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornare il profilo: ${e.message}"
            }
        }
    }

    fun toggleFavorite(perfumeId: String) {
        viewModelScope.launch {
            val userId = getCurrentUserId()

            if (userId.isNullOrBlank()) {
                _errorMessage.value = "Utente non autenticato"
                return@launch
            }

            try {
                val freshUser = repository.getUserByIdOnce(userId)
                val currentFavorites = freshUser?.preferiti?.toMutableList() ?: mutableListOf()

                if (currentFavorites.contains(perfumeId)) {
                    currentFavorites.remove(perfumeId)
                } else {
                    currentFavorites.add(perfumeId)
                }

                repository.updateFavorites(userId, currentFavorites)
                _currentUser.value = _currentUser.value?.copy(preferiti = currentFavorites)

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

    // ✅ CORRETTO: Aggiorna wallet e stato locale immediatamente
    fun rechargeWallet(amount: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: run {
                onError("Utente non autenticato")
                return@launch
            }

            try {
                val currentWallet = _currentUser.value?.wallet ?: 0.0
                val newWallet = currentWallet + amount

                // Aggiorna su Firebase
                repository.updateWallet(userId, newWallet)

                // Aggiorna stato locale immediatamente
                _currentUser.value = _currentUser.value?.copy(wallet = newWallet)

                onSuccess()
            } catch (e: Exception) {
                onError("Errore nella ricarica: ${e.message}")
            }
        }
    }

    // Mantieni anche il vecchio metodo per compatibilità
    fun updateWallet(amount: Double) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                val currentWallet = _currentUser.value?.wallet ?: 0.0
                val newWallet = currentWallet + amount
                repository.updateWallet(userId, newWallet)
                _currentUser.value = _currentUser.value?.copy(wallet = newWallet)
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
    }
}