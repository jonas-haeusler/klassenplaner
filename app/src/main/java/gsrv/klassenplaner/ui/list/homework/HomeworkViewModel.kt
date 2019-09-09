package gsrv.klassenplaner.ui.list.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.entities.Homework
import gsrv.klassenplaner.data.repository.HomeworkRepository
import gsrv.klassenplaner.ui.list.base.BaseListViewModel
import kotlinx.coroutines.launch

/**
 * [ViewModel] for [HomeworkFragment]
 */
class HomeworkViewModel(val homeworkRepository: HomeworkRepository) : BaseListViewModel<Homework>() {

    override fun getItems(groups: List<Group>) = homeworkRepository.get(groups)

    override suspend fun refetch(groups: List<Group>) = homeworkRepository.refetch(groups)

    fun setHomeworkCompleted(homework: Homework) {
        viewModelScope.launch {
            homeworkRepository.updateLocally(homework)
        }
    }
}
