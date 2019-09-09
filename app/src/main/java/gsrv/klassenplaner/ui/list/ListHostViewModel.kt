package gsrv.klassenplaner.ui.list

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gsrv.klassenplaner.data.repository.GroupRepository
import gsrv.klassenplaner.util.SingleLiveEvent

class ListHostViewModel(private val groupRepository: GroupRepository) : ViewModel() {
    private val _load = SingleLiveEvent<Unit>()

    val groups = Transformations.switchMap(_load) {
        groupRepository.getGroups()
    }

    init {
        loadGroups()
    }

    fun loadGroups() {
        _load.call()
    }
}
