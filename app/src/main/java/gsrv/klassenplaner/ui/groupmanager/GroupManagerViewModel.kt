package gsrv.klassenplaner.ui.groupmanager

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.repository.GroupRepository
import gsrv.klassenplaner.util.SingleLiveEvent
import kotlinx.coroutines.launch

class GroupManagerViewModel(
    val groupRepository: GroupRepository
) : ViewModel() {
    data class ViewState(
        val isLoading: Boolean = false,
        val isEmpty: Boolean = false,
        val groups: List<Group> = emptyList()
    )

    private val _viewState = MediatorLiveData<ViewState>()

    val viewState: LiveData<ViewState>
        get() = _viewState

    private var currentViewState: ViewState
        get() = _viewState.value!!
        set(value) {
            _viewState.value = value
        }

    sealed class Command {

        class Close(@StringRes val message: Int): Command()
    }
    val command: SingleLiveEvent<Command> = SingleLiveEvent()

    init {
        currentViewState = ViewState()

        _viewState.addSource(groupRepository.getGroups()) { result ->
            if (result.data.isNullOrEmpty()) {
                command.value = Command.Close(R.string.error_loading_groups)
            } else {
                result.data?.let { groups ->
                    currentViewState = currentViewState.copy(
                        groups = groups,
                        isLoading = result is Result.Loading,
                        isEmpty = groups.isEmpty() && result !is Result.Loading
                    )
                }
            }
        }
    }

    fun login(group: Group, password: String) {
        group.password = password
        viewModelScope.launch {
            groupRepository.login(group)
        }
    }

    fun logout(group: Group) {
        viewModelScope.launch {
            groupRepository.logout(group)
        }
    }

    fun deleteLocal(group: Group) {
        viewModelScope.launch {
            groupRepository.deleteGroupLocally(group)
        }
    }

    fun delete(group: Group) {
        viewModelScope.launch {
            groupRepository.deleteGroup(group)
        }
    }
}
