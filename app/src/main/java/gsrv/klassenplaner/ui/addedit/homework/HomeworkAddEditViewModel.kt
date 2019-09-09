package gsrv.klassenplaner.ui.addedit.homework

import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.entities.Homework
import gsrv.klassenplaner.data.repository.GroupRepository
import gsrv.klassenplaner.data.repository.HomeworkRepository
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditViewModel
import timber.log.Timber
import java.util.*

class HomeworkAddEditViewModel(
    homeworkRepository: HomeworkRepository,
    groupRepository: GroupRepository
) : BaseAddEditViewModel<Homework>(homeworkRepository, groupRepository) {

    override fun setupItem(item: Homework) {
        command.value = Command.SetText(item.subject, item.homework)
        setDate(item.date.time)
        val groupsById = currentViewState.groups.associateBy { it.id }
        if (groupsById.containsKey(item.groupId)) {
            setGroup(groupsById[item.groupId])
        } else {
            command.value = Command.ShowMessage(R.string.error_loading_groups)
            command.value = Command.Abort
        }
    }

    override fun constructItem(subject: String, date: Date, group: Group, text: String, prePopulatedItem: Homework?) =
        Homework(
            id = 0,
            subject = subject,
            date = date,
            homework = text,
            groupId = group.id,
            completed = prePopulatedItem?.completed == true
        )
}
