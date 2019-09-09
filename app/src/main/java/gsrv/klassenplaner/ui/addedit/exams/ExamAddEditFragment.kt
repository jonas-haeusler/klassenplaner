package gsrv.klassenplaner.ui.addedit.exams

import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExamAddEditFragment : BaseAddEditFragment<Exam>() {

    override val viewModel: ExamAddEditViewModel by viewModel()

    override fun getItemType() = R.string.exam
}
