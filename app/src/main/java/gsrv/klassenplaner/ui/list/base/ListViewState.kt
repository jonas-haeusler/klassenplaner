package gsrv.klassenplaner.ui.list.base

import gsrv.klassenplaner.data.entities.Group

data class ListViewState<T>(
    val isContentLoading: Boolean = true,
    val isContentRefreshing: Boolean = false,
    val isContentEmpty: Boolean = false,
    val isOffline: Boolean = false,
    val showNetworkError: Boolean = false,
    val hasLoggedInGroups: Boolean = false,
    val groups: List<Group> = emptyList(),
    val items: List<T> = emptyList()
)
