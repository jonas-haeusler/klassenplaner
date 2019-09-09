package gsrv.klassenplaner.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gsrv.klassenplaner.data.entities.Group
import timber.log.Timber

class GlobalStateViewModel : ViewModel() {
    private val _groups = MutableLiveData<List<Group>>()
    val groups: LiveData<List<Group>>
        get() = _groups

    fun setGroups(groups: List<Group>) {
        if (_groups.value != groups) {
            _groups.value = groups
        }
     viewModelScope
    }
}
