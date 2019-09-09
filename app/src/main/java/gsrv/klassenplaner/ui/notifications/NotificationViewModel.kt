package gsrv.klassenplaner.ui.notifications

import android.content.SharedPreferences
import android.text.format.DateUtils
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditViewModel
import gsrv.klassenplaner.util.SingleLiveEvent

private const val KEY_HOMEWORK_DATE = "pref_notifications_homework_date"
private const val KEY_HOMEWORK_TIME = "pref_notifications_homework_time"
private const val KEY_EXAM_DATE = "pref_notifications_exam_date"
private const val KEY_EXAM_TIME = "pref_notifications_exam_time"

class NotificationViewModel(val preferences: SharedPreferences) : ViewModel() {

    data class ViewState(
        val homeworkDateSelection: Int,
        val homeworkTimeInMillis: Long,
        val examDateSelection: Int,
        val examTimeInMillis: Long
    )

    sealed class Command {
        object ShowHomeworkTimePicker : Command()
        object ShowExamTimePicker : Command()
    }

    val command: SingleLiveEvent<Command> = SingleLiveEvent()

    private val _viewState = MediatorLiveData<ViewState>()

    val viewState: LiveData<ViewState>
        get() = _viewState

    private var currentViewState: ViewState
        get() = _viewState.value!!
        set(value) {
            _viewState.value = value
        }

    init {
        currentViewState = ViewState(
            homeworkDateSelection = preferences.getInt(KEY_HOMEWORK_DATE, 0),
            homeworkTimeInMillis = preferences.getLong(KEY_HOMEWORK_TIME, DateUtils.HOUR_IN_MILLIS * 13),
            examDateSelection = preferences.getInt(KEY_EXAM_DATE, 0),
            examTimeInMillis = preferences.getLong(KEY_EXAM_TIME, DateUtils.HOUR_IN_MILLIS * 13)
        )
    }

    fun setHomeworkDateSelection(position: Int) {
        currentViewState = currentViewState.copy(
            homeworkDateSelection = position
        )
    }

    fun onHomeworkTimeSet(position: Int) {
        when (position) {
            0 -> setHomeworkTime(DateUtils.HOUR_IN_MILLIS * 7)
            1 -> setHomeworkTime(DateUtils.HOUR_IN_MILLIS * 12)
            2 -> setHomeworkTime(DateUtils.HOUR_IN_MILLIS * 17)
            3 -> setHomeworkTime(DateUtils.HOUR_IN_MILLIS * 19)
            else -> command.value = Command.ShowHomeworkTimePicker
        }
    }

    fun setHomeworkTime(timeInMillis: Long) {
        currentViewState = currentViewState.copy(
            homeworkTimeInMillis = timeInMillis
        )
    }

    fun setExamDateSelection(position: Int) {
        currentViewState = currentViewState.copy(
            examDateSelection = position
        )
    }

    fun onExamTimeSet(position: Int) {
        when (position) {
            0 -> setExamTime(DateUtils.HOUR_IN_MILLIS * 7)
            1 -> setExamTime(DateUtils.HOUR_IN_MILLIS * 12)
            2 -> setExamTime(DateUtils.HOUR_IN_MILLIS * 17)
            3 -> setExamTime(DateUtils.HOUR_IN_MILLIS * 19)
            else -> command.value = Command.ShowExamTimePicker
        }
    }

    fun setExamTime(timeInMillis: Long) {
        currentViewState = currentViewState.copy(
            examTimeInMillis = timeInMillis
        )
    }

    fun save() = preferences.edit {
        putInt(KEY_HOMEWORK_DATE, currentViewState.homeworkDateSelection)
        putLong(KEY_HOMEWORK_TIME, currentViewState.homeworkTimeInMillis)
        putInt(KEY_EXAM_DATE, currentViewState.examDateSelection)
        putLong(KEY_EXAM_TIME, currentViewState.examTimeInMillis)
    }
}
