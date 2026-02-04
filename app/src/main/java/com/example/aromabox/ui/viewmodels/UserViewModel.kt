package com.example.aromabox.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aromabox.data.model.Badge
import com.example.aromabox.data.model.BadgeDefinitions
import com.example.aromabox.data.model.BadgeRequirementType
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.data.model.User
import com.example.aromabox.data.repository.UserRepository
import com.example.aromabox.data.repository.UserStats
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

    // Badge states
    private val _userBadges = MutableStateFlow<List<Badge>>(emptyList())
    val userBadges: StateFlow<List<Badge>> = _userBadges.asStateFlow()

    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    private val _newlyUnlockedBadge = MutableStateFlow<Badge?>(null)
    val newlyUnlockedBadge: StateFlow<Badge?> = _newlyUnlockedBadge.asStateFlow()

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

                // Carica anche i badge
                user?.let {
                    loadUserBadgesAndStats(uid)
                }
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

                // Verifica badge Esploratore dopo aggiunta preferiti
                checkAndUnlockBadges(BadgeRequirementType.PREFERITI, currentFavorites.size)

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

    /**
     * Crea un nuovo ordine per l'utente.
     * Genera automaticamente un PIN a 6 cifre per il ritiro.
     *
     * @param perfume Il profumo acquistato
     * @param distributorId L'ID del distributore selezionato
     * @param distributorName Il nome del distributore
     * @param onSuccess Callback con il PIN generato
     * @param onError Callback in caso di errore
     */
    fun createOrder(
        perfume: Perfume,
        distributorId: String,
        distributorName: String,
        onSuccess: (pin: String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: run {
                onError("Utente non autenticato")
                return@launch
            }

            try {
                val result = repository.addOrder(
                    userId = userId,
                    perfume = perfume,
                    distributorId = distributorId,
                    distributorName = distributorName
                )

                result.fold(
                    onSuccess = { order ->
                        // Incrementa contatori e verifica badge
                        val newAcquisti = repository.incrementAcquisti(userId)
                        val newErogazioni = repository.incrementErogazioni(userId)

                        checkAndUnlockBadges(BadgeRequirementType.ACQUISTI, newAcquisti)
                        checkAndUnlockBadges(BadgeRequirementType.EROGAZIONI, newErogazioni)

                        onSuccess(order.pin)
                    },
                    onFailure = { exception ->
                        onError("Errore nella creazione dell'ordine: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                onError("Errore nella creazione dell'ordine: ${e.message}")
            }
        }
    }

    // ✅ Aggiorna wallet e stato locale immediatamente
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

    // ==================== BADGE METHODS ====================

    /**
     * Carica i badge e le statistiche dell'utente
     */
    private fun loadUserBadgesAndStats(userId: String) {
        viewModelScope.launch {
            try {
                val stats = repository.getUserStats(userId)
                _userStats.value = stats

                // Genera la lista completa di badge con stato
                val unlockedBadges = repository.getUnlockedBadges(userId)
                val unlockedIds = unlockedBadges.map { it.id }.toSet()

                val allBadgesWithState = BadgeDefinitions.allBadges.map { definition ->
                    val isUnlocked = unlockedIds.contains(definition.id)
                    val unlockedBadge = unlockedBadges.find { it.id == definition.id }

                    Badge.fromDefinition(
                        definition = definition,
                        isUnlocked = isUnlocked,
                        dataOttenimento = unlockedBadge?.dataOttenimento ?: 0L
                    )
                }

                _userBadges.value = allBadgesWithState
            } catch (e: Exception) {
                _errorMessage.value = "Errore nel caricamento badge: ${e.message}"
            }
        }
    }

    /**
     * Verifica e sblocca badge in base al tipo di requisito e valore raggiunto
     */
    private fun checkAndUnlockBadges(requirementType: BadgeRequirementType, currentValue: Int) {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                // Trova tutti i badge di questo tipo che possono essere sbloccati
                val badgesToCheck = BadgeDefinitions.findByRequirementType(requirementType)

                for (definition in badgesToCheck) {
                    // Verifica se il requisito è soddisfatto
                    if (currentValue >= definition.requirementValue) {
                        // Verifica se non è già sbloccato
                        val alreadyUnlocked = repository.isBadgeUnlocked(userId, definition.id)

                        if (!alreadyUnlocked) {
                            // Sblocca il badge
                            val newBadge = Badge.fromDefinition(
                                definition = definition,
                                isUnlocked = true,
                                dataOttenimento = System.currentTimeMillis()
                            )

                            repository.unlockBadge(userId, newBadge)

                            // Notifica UI del nuovo badge sbloccato
                            _newlyUnlockedBadge.value = newBadge

                            // Ricarica i badge
                            loadUserBadgesAndStats(userId)
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Errore nella verifica badge: ${e.message}"
            }
        }
    }

    /**
     * Chiamato quando l'utente lascia una recensione
     */
    fun onReviewSubmitted() {
        viewModelScope.launch {
            val userId = getCurrentUserId() ?: return@launch

            try {
                val newRecensioni = repository.incrementRecensioni(userId)
                checkAndUnlockBadges(BadgeRequirementType.RECENSIONI, newRecensioni)
            } catch (e: Exception) {
                _errorMessage.value = "Errore nell'aggiornamento recensioni: ${e.message}"
            }
        }
    }

    /**
     * Resetta la notifica del nuovo badge sbloccato
     */
    fun clearNewlyUnlockedBadge() {
        _newlyUnlockedBadge.value = null
    }

    /**
     * Ottieni tutti i badge con il loro stato attuale
     */
    fun getAllBadgesWithState(): List<Badge> {
        return _userBadges.value
    }

    /**
     * Conta i badge sbloccati
     */
    fun getUnlockedBadgeCount(): Int {
        return _userBadges.value.count { it.isUnlocked }
    }

    /**
     * Conta i badge totali
     */
    fun getTotalBadgeCount(): Int {
        return BadgeDefinitions.allBadges.size
    }
}