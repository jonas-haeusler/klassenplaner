package gsrv.klassenplaner.ui.addedit.exams

import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.repository.ExamRepository
import gsrv.klassenplaner.data.repository.GroupRepository
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditViewModel
import java.util.*

class ExamAddEditViewModel(
    examRepository: ExamRepository,
    groupRepository: GroupRepository
) : BaseAddEditViewModel<Exam>(examRepository, groupRepository) {

    override fun setupItem(item: Exam) {
        command.value = Command.SetText(item.subject, item.exam)
        setDate(item.date.time)
        val groupsById = currentViewState.groups.associateBy { it.id }
        if (groupsById.containsKey(item.groupId)) {
            setGroup(groupsById[item.groupId])
        } else {
            command.value = Command.ShowMessage(R.string.error_loading_groups)
            command.value = Command.Abort
        }
    }

    override fun constructItem(subject: String, date: Date, group: Group, text: String, prePopulatedItem: Exam?) =
            Exam(id = 0, subject = subject, date = date, exam = text, groupId = group.id)
}
