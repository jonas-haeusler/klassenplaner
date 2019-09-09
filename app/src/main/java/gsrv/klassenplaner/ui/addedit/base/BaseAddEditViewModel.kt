package gsrv.klassenplaner.ui.addedit.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.Result
import gsrv.klassenplaner.data.entities.BaseItem
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.repository.BaseRepository
import gsrv.klassenplaner.data.repository.GroupRepository
import gsrv.klassenplaner.util.SingleLiveEvent
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * ViewModel for [BaseAddEditFragment]
 */
abstract class BaseAddEditViewModel<T : BaseItem>(
    private val baseRepository: BaseRepository<T>,
    groupRepository: GroupRepository
) : ViewModel() {

    private val now = Calendar.getInstance()
    private val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    data class ViewState(
        val groupsLoading: Boolean = false,
        val groups: List<Group> = emptyList(),
        val date: Date? = null,
        val group: Group? = null,
        val isSaving: Boolean = false
    )

    sealed class Command {
        class ShowMessage(@StringRes val messageId: Int) : Command()
        class InvalidInput(val inputError: InputError) : Command()
        class SetText(val subject: String, val text: String) : Command()
        object Abort : Command()
        object SaveSuccessful : Command()
        object SaveFailed : Command()
        object DeletionFailed : Command()
        object DeletionSuccessful : Command()
    }

    sealed class InputError {
        object InvalidSubject : InputError()
        object InvalidDate : InputError()
        object InvalidGroup : InputError()
    }

    private val _viewState = MediatorLiveData<ViewState>()

    val viewState: LiveData<ViewState>
        get() = _viewState

    protected var currentViewState: ViewState
        get() = _viewState.value!!
        set(value) {
            _viewState.value = value
        }

    val command: SingleLiveEvent<Command> = SingleLiveEvent()

    /**
     * The item that was resolved by passing it's id to [setup], null when that never happened.
     */
    private var prePopulatedItem: T? = null

    /**
     * Whether this is a new item or an item that is edited
     */
    private val newItem: Boolean
        get() = prePopulatedItem == null

    private var shouldSetup = false

    init {
        currentViewState = ViewState()

        _viewState.addSource(groupRepository.getGroups()) { result ->
            val resultData = result.data?.filter { it.isLoggedIn }
            if (result is Result.Error || resultData.isNullOrEmpty()) {
                command.value = Command.ShowMessage(R.string.error_loading_groups)
                command.value = Command.Abort
            } else {
                currentViewState = currentViewState.copy(
                    groups = resultData,
                    groupsLoading = result is Result.Loading
                )

                // Setup the pre-populated item if necessary and hasn't been done before
                if (shouldSetup) {
                    shouldSetup = false
                    prePopulatedItem?.let { setupItem(it) }
                }
            }
        }
    }

    /**
     * Load an existing item into the ViewModel. No-op when [itemId] is null.
     */
    fun setup(itemId: String?) {
        itemId?.let {
            viewModelScope.launch {
                runCatching {
                    val item = baseRepository.get(it.toInt())
                    prePopulatedItem = item
                    shouldSetup = true
                }.onFailure {
                    Timber.e(it)
                    command.value = Command.Abort
                }
            }
        }
    }

    fun save(subject: String, text: String) = with(currentViewState) {
        var valid = true

        if (subject.isBlank()) {
            valid = false
            command.value = Command.InvalidInput(InputError.InvalidSubject)
        }

        if (date == null || date < now.time) {
            valid = false
            command.value = Command.InvalidInput(InputError.InvalidDate)
        }

        if (group == null || !group.isLoggedIn) {
            valid = false
            command.value = Command.InvalidInput(InputError.InvalidGroup)
        }

        if (valid) {
            val item = constructItem(subject, date!!, group!!, text, prePopulatedItem)
            if (newItem) {
                save(item)
            } else {
                item.id = prePopulatedItem!!.id
                update(item)
            }
        }
    }

    fun delete() {
        if (newItem) {
            return
        }

        viewModelScope.launch {
            runCatching {
                baseRepository.delete(prePopulatedItem!!)
                command.value = Command.DeletionSuccessful
            }.onFailure {
                Timber.e(it)
                command.value = Command.DeletionFailed
            }
        }
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        currentViewState = currentViewState.copy(date = Date(calendar.timeInMillis))
    }

    fun setDate(timeInMillis: Long) {
        calendar.timeInMillis = timeInMillis
        currentViewState = currentViewState.copy(date = Date(calendar.timeInMillis))
    }

    fun setGroup(group: Group?) {
        currentViewState = currentViewState.copy(group = group)
    }

    private fun save(item: T) {
        viewModelScope.launch {
            runCatching {
                currentViewState = currentViewState.copy(isSaving = true)
                baseRepository.create(item)
                command.value = Command.SaveSuccessful
            }.onFailure {
                Timber.e(it)
                command.value = Command.SaveFailed
            }
        }
    }

    private fun update(item: T) {
        viewModelScope.launch {
            runCatching {
                currentViewState = currentViewState.copy(isSaving = true)
                baseRepository.update(item)
                command.value = Command.SaveSuccessful
            }.onFailure {
                Timber.e(it)
                command.value = Command.SaveFailed
            }
        }
    }

    protected abstract fun setupItem(item: T)

    protected abstract fun constructItem(
        subject: String,
        date: Date,
        group: Group,
        text: String,
        prePopulatedItem: T?
    ): T
}
