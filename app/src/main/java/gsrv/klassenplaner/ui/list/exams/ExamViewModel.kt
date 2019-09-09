package gsrv.klassenplaner.ui.list.exams

import androidx.lifecycle.ViewModel
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.repository.ExamRepository
import gsrv.klassenplaner.ui.list.base.BaseListViewModel

/**
 * [ViewModel] for [ExamFragment]
 */
class ExamViewModel(val examRepository: ExamRepository) : BaseListViewModel<Exam>() {

    override fun getItems(groups: List<Group>) = examRepository.get(groups)

    override suspend fun refetch(groups: List<Group>) = examRepository.refetch(groups)
}
