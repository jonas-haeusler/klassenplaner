package gsrv.klassenplaner.ui.addedit.base

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.picker.MaterialDatePickerDialogFragment
import com.google.android.material.snackbar.Snackbar
import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.BaseItem
import gsrv.klassenplaner.ui.addedit.DateAdapter
import gsrv.klassenplaner.ui.addedit.GroupAdapter
import gsrv.klassenplaner.ui.base.BaseFragment
import gsrv.klassenplaner.util.dayOfMonth
import gsrv.klassenplaner.util.getWeekday
import gsrv.klassenplaner.util.month
import gsrv.klassenplaner.util.year
import kotlinx.android.synthetic.main.fragment_addedit.container
import kotlinx.android.synthetic.main.fragment_addedit.date
import kotlinx.android.synthetic.main.fragment_addedit.dateContainer
import kotlinx.android.synthetic.main.fragment_addedit.groups
import kotlinx.android.synthetic.main.fragment_addedit.groupsContainer
import kotlinx.android.synthetic.main.fragment_addedit.save
import kotlinx.android.synthetic.main.fragment_addedit.subject
import kotlinx.android.synthetic.main.fragment_addedit.subjectContainer
import kotlinx.android.synthetic.main.fragment_addedit.text
import kotlinx.android.synthetic.main.fragment_addedit.textContainer
import kotlinx.android.synthetic.main.fragment_addedit.toolbar
import java.util.Calendar

abstract class BaseAddEditFragment<T : BaseItem> : BaseFragment(), DatePickerDialog.OnDateSetListener {
    protected abstract val viewModel: BaseAddEditViewModel<T>

    private val calendar = Calendar.getInstance()
    private val now = Calendar.getInstance()

    private lateinit var groupAdapter: GroupAdapter

    private val itemType by lazy { getString(getItemType()) }

    private val newItem: Boolean
        get() = arguments == null

    companion object {
        const val ARGUMENT_ITEM_ID = "ARGUMENT_ITEM_ID"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_addedit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar, hasOptionsMenu = true)
        toolbar.setNavigationIcon(R.drawable.ic_close_theme)
        setupUi()
        observeViewModel()

        viewModel.setup(arguments?.getString(ARGUMENT_ITEM_ID))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_addedit, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if (newItem) {
            menu.findItem(R.id.menu_delete).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_delete -> {
            showDeleteDialog()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.setDate(year, month, dayOfMonth)
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_item).format(itemType))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.delete()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setupUi() {
        // Remove the error text when the input changes
        subject.doAfterTextChanged { subjectContainer.error = null }

        textContainer.hint = itemType
        toolbar.title = if (newItem) {
            getString(R.string.title_add_item, itemType)
        } else {
            getString(R.string.title_edit_item, itemType)
        }

        save.text = getString(R.string.save_item, itemType)

        setupDateInput()
        setupGroupInput()

        save.setOnClickListener {
            viewModel.save(subject.text.toString(), text.text.toString())
        }
    }

    private fun setupDateInput() {
        val dateArray = resources.getStringArray(R.array.dropdown_dates)
        dateArray[dateArray.size - 2] = dateArray[dateArray.size - 2].format(now.getWeekday())

        val dateAdapter = DateAdapter(requireContext(), items = dateArray.toList())
        date.setAdapter(dateAdapter)

        date.setOnItemClickListener { _, _, position, _ ->
            val now = Calendar.getInstance()
            when (position) {
                0 -> viewModel.setDate(now.year, now.month, now.dayOfMonth)
                1 -> viewModel.setDate(now.year, now.month, now.dayOfMonth + 1)
                2 -> viewModel.setDate(now.year, now.month, now.dayOfMonth + 2)
                3 -> viewModel.setDate(now.year, now.month, now.dayOfMonth + 7)
                4 -> showDatePicker()
            }
        }

        // Remove the error text when the input changes
        date.doAfterTextChanged { dateContainer.error = null }

        // Default date to `tomorrow`
        viewModel.setDate(now.year, now.month, now.dayOfMonth + 1)
    }

    private fun setupGroupInput() {
        groupAdapter = GroupAdapter(requireContext())
        groups.setAdapter(groupAdapter)
        groups.setOnItemClickListener { _, _, position, _ ->
            viewModel.setGroup(groupAdapter.getItem(position))
        }

        // Remove the error text when the input changes
        groups.doAfterTextChanged { groupsContainer.error = null }
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, ::render)
        viewModel.command.observe(viewLifecycleOwner, ::processCommand)
    }

    private fun processCommand(command: BaseAddEditViewModel.Command) {
        when (command) {
            is BaseAddEditViewModel.Command.Abort ->
                findNavController().navigateUp()
            is BaseAddEditViewModel.Command.ShowMessage ->
                Toast.makeText(requireContext(), command.messageId, Toast.LENGTH_SHORT).show()
            is BaseAddEditViewModel.Command.InvalidInput -> {
                when (command.inputError) {
                    is BaseAddEditViewModel.InputError.InvalidSubject ->
                        subjectContainer.error = getString(R.string.input_error_subject)
                    is BaseAddEditViewModel.InputError.InvalidDate ->
                        dateContainer.error = getString(R.string.input_error_date)
                    is BaseAddEditViewModel.InputError.InvalidGroup ->
                        groupsContainer.error = getString(R.string.input_error_group)
                }
            }
            is BaseAddEditViewModel.Command.SaveFailed ->
                Snackbar.make(container, getString(R.string.error_save_failed, itemType), Snackbar.LENGTH_LONG).show()
            is BaseAddEditViewModel.Command.SaveSuccessful ->
                findNavController().navigateUp()
            is BaseAddEditViewModel.Command.DeletionSuccessful ->
                findNavController().navigateUp()
            is BaseAddEditViewModel.Command.DeletionFailed ->
                Snackbar.make(
                    container,
                    getString(R.string.error_deletion_failed, itemType),
                    Snackbar.LENGTH_LONG
                ).show()
            is BaseAddEditViewModel.Command.SetText -> {
                subject.setText(command.subject)
                text.setText(command.text)
            }
        }
    }

    private fun render(viewState: BaseAddEditViewModel.ViewState) {
        groupAdapter.setItems(viewState.groups)
        if (viewState.group == null && viewState.groups.isNotEmpty()) {
            // Default group to first in the adapter
            viewModel.setGroup(viewState.groups[0])
        }
        groups.setText(viewState.group?.name)

        if (viewState.date != null) {
            date.setText(
                DateUtils.getRelativeTimeSpanString(
                    viewState.date.time,
                    now.timeInMillis,
                    DateUtils.DAY_IN_MILLIS
                )
            )
            date.dismissDropDown()
        }

        save.isEnabled = !viewState.isSaving
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * The type of item being processed inside this class. Will be used for the toolbar title and input hint
     */
    @StringRes
    protected abstract fun getItemType(): Int
}
