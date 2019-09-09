package gsrv.klassenplaner.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.repository.GroupRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class SetupViewModel(private val groupRepository: GroupRepository) : ViewModel() {
    enum class AuthenticationState {
        AUTHENTICATED,          // The setup was successful
        AUTHENTICATING,         // The authentication is being checked
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private val _authenticationState = MediatorLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState>
        get() = _authenticationState

    init {
        _authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun login(groupId: String) {
        val id = groupId.toIntOrNull() ?: throw IllegalArgumentException("groupId must be numeral")
        val source = groupRepository.getGroup(id, forceFetch = true)
        _authenticationState.addSource(source) { result ->
            _authenticationState.value = when (result) {
                is Result.Success -> AuthenticationState.AUTHENTICATED
                is Result.Loading -> AuthenticationState.AUTHENTICATING
                is Result.Error -> AuthenticationState.INVALID_AUTHENTICATION
            }
        }
    }

    fun register(groupName: String) {
        viewModelScope.launch {
            runCatching {
                _authenticationState.value = AuthenticationState.AUTHENTICATING
                groupRepository.createGroup(Group(name = groupName))
                _authenticationState.value = AuthenticationState.AUTHENTICATED
            }.onFailure { exception ->
                Timber.e(exception)
                _authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
            }
        }
    }
}
