package gsrv.klassenplaner.ui.addedit.homework

import gsrv.klassenplaner.R
import gsrv.klassenplaner.data.entities.Homework
import gsrv.klassenplaner.ui.addedit.base.BaseAddEditFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeworkAddEditFragment : BaseAddEditFragment<Homework>() {

    override val viewModel: HomeworkAddEditViewModel by viewModel()

    override fun getItemType() = R.string.homework
}
