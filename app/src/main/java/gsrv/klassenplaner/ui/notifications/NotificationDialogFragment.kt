package gsrv.klassenplaner.ui.notifications

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gsrv.klassenplaner.R
import gsrv.klassenplaner.ui.common.NoFilterAdpater
import gsrv.klassenplaner.ui.notifications.NotificationViewModel.*
import gsrv.klassenplaner.util.hourOfDay
import gsrv.klassenplaner.util.minute
import kotlinx.android.synthetic.main.dialog_notifications.examDate
import kotlinx.android.synthetic.main.dialog_notifications.examTime
import kotlinx.android.synthetic.main.dialog_notifications.homeworkDate
import kotlinx.android.synthetic.main.dialog_notifications.homeworkTime
import kotlinx.android.synthetic.main.dialog_notifications.view.examDate
import kotlinx.android.synthetic.main.dialog_notifications.view.examTime
import kotlinx.android.synthetic.main.dialog_notifications.view.homeworkDate
import kotlinx.android.synthetic.main.dialog_notifications.view.homeworkTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationDialogFragment : DialogFragment() {
    private val viewModel: NotificationViewModel by viewModel()

    private var rootView: View? = null

    private val now = Calendar.getInstance()
    private val calendar = Calendar.getInstance().apply { clear() }
    private val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val homeworkTimePicker by lazy {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.hourOfDay = hourOfDay
                calendar.minute = minute
                viewModel.setHomeworkTime(calendar.timeInMillis)
                homeworkTime.dismissDropDown()
            },
            now.hourOfDay,
            now.minute,
            DateFormat.is24HourFormat(requireContext())
        )
    }

    private val examTimePicker by lazy {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.hourOfDay = hourOfDay
                calendar.minute = minute
                viewModel.setExamTime(calendar.timeInMillis)
                examTime.dismissDropDown()
            },
            now.hourOfDay,
            now.minute,
            DateFormat.is24HourFormat(requireContext())
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        rootView = View.inflate(requireContext(), R.layout.dialog_notifications, null)
        setupView()

        return MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_MaterialComponents_Dialog)
            .setView(rootView)
            .setTitle(getString(R.string.title_notifications))
            .setPositiveButton(R.string.save) { _, _ ->
                viewModel.save()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        observeViewModel()
        return rootView
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.command.observe(viewLifecycleOwner, ::processCommand)
    }

    private fun render(viewState: ViewState) {
        rootView?.apply {
            homeworkDate.setText(homeworkDate.adapter.getItem(viewState.homeworkDateSelection) as String)
            examDate.setText(examDate.adapter.getItem(viewState.examDateSelection) as String)
            homeworkTime.isEnabled = viewState.homeworkDateSelection != 0
            examTime.isEnabled = viewState.examDateSelection != 0
            homeworkTime.setText(simpleDateFormat.format(viewState.homeworkTimeInMillis))
            examTime.setText(simpleDateFormat.format(viewState.examTimeInMillis))
        }
    }

    private fun processCommand(command: Command) {
        when (command) {
            is Command.ShowHomeworkTimePicker -> homeworkTimePicker.show()
            is Command.ShowExamTimePicker -> examTimePicker.show()
        }
    }

    private fun setupView() {
        rootView?.apply {
            val dateAdapter = NoFilterAdpater<String>(requireContext())
            dateAdapter.setItems(resources.getStringArray(R.array.dropdown_notifications_date).toList())

            val timeAdapter = NoFilterAdpater<String>(requireContext())
            timeAdapter.setItems(resources.getStringArray(R.array.dropdown_notifications_time).toList())

            homeworkDate.setAdapter(dateAdapter)
            homeworkTime.setAdapter(timeAdapter)
            examDate.setAdapter(dateAdapter)
            examTime.setAdapter(timeAdapter)

            homeworkDate.setOnItemClickListener { _, _, position, _ ->
                viewModel.setHomeworkDateSelection(position)
            }

            homeworkTime.setOnItemClickListener { _, _, position, _ ->
                viewModel.onHomeworkTimeSet(position)
            }

            examDate.setOnItemClickListener { _, _, position, _ ->
                viewModel.setExamDateSelection(position)
            }

            examTime.setOnItemClickListener { _, _, position, _ ->
                viewModel.onExamTimeSet(position)
            }
        }
    }

    override fun onDestroyView() {
        rootView = null
        super.onDestroyView()
    }
}
