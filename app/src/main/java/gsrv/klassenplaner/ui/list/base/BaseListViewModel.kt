package gsrv.klassenplaner.ui.list.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.entities.BaseItem
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.ui.list.ListHostFragment

/**
 * Base [ViewModel] class for childs of [ListHostFragment].
 */
abstract class BaseListViewModel<T : BaseItem> : ViewModel() {
    private val _viewState = MediatorLiveData<ListViewState<T>>()

    /**
     * A [ListViewState] describing the state of the ui.
     * Observe this to be notified about any changes to ui-related content.
     */
    val viewState: LiveData<ListViewState<T>>
        get() = _viewState

    private var currentViewState
        get() = _viewState.value!!
        set(value) {
            _viewState.value = value
        }

    private val _groups = MutableLiveData<List<Group>>()

    private val _items: LiveData<Result<List<T>>> =
        Transformations.switchMap(_groups) {
            getItems(it)
        }

    init {
        currentViewState = ListViewState()

        _viewState.addSource(_items) { result ->
            currentViewState = currentViewState.copy(
                isContentLoading = result is Result.Loading && result.data == null,
                isContentRefreshing = result is Result.Loading && result.data != null,
                isContentEmpty = result.data?.isEmpty() ?: false,
                isOffline = result is Result.Error && result.data != null,
                showNetworkError = result is Result.Error && result.data == null,
                hasLoggedInGroups = _groups.value?.any { it.isLoggedIn } ?: false,
                groups = _groups.value ?: emptyList(),
                items = result?.data ?: emptyList()
            )
        }
    }

    /**
     * Set the [groups] for which we should load data.
     * Observe [viewState] to get notified about the loaded data.
     */
    fun setGroups(groups: List<Group>) {
        if (_groups.value != groups) {
            _groups.value = groups
        }
    }

    /**
     * Re-fetches data from the network for the given [Group]s specified by [setGroups].
     * Observe [viewState] to get notified about the loaded data.
     */
    fun retry() {
        // we simply set the groups to the same value, thus triggering the observer
        _groups.value = _groups.value
    }

    /**
     * Returns a [LiveData] containing a [Result] of the data that was loaded.
     */
    protected abstract fun getItems(groups: List<Group>): LiveData<Result<List<T>>>

    /**
     * Refetches the data loaded via [getItems]
     */
    protected abstract suspend fun refetch(groups: List<Group>)
}
