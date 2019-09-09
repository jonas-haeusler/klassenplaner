package gsrv.klassenplaner

import androidx.preference.PreferenceManager
import gsrv.klassenplaner.data.database.AppDatabase
import gsrv.klassenplaner.data.network.ExamApi
import gsrv.klassenplaner.data.network.GroupApi
import gsrv.klassenplaner.data.network.HomeworkApi
import gsrv.klassenplaner.data.network.createNetworkClient
import gsrv.klassenplaner.data.repository.ExamRepository
import gsrv.klassenplaner.data.repository.GroupRepository
import gsrv.klassenplaner.data.repository.HomeworkRepository
import gsrv.klassenplaner.ui.addedit.exams.ExamAddEditViewModel
import gsrv.klassenplaner.ui.addedit.homework.HomeworkAddEditViewModel
import gsrv.klassenplaner.ui.groupmanager.GroupManagerViewModel
import gsrv.klassenplaner.ui.list.GlobalStateViewModel
import gsrv.klassenplaner.ui.list.ListHostViewModel
import gsrv.klassenplaner.ui.list.exams.ExamViewModel
import gsrv.klassenplaner.ui.list.homework.HomeworkViewModel
import gsrv.klassenplaner.ui.notifications.NotificationViewModel
import gsrv.klassenplaner.ui.setup.SetupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

val viewModelModule = module {
    viewModel { SetupViewModel(groupRepository = get()) }
    viewModel { ListHostViewModel(groupRepository = get()) }
    viewModel { GlobalStateViewModel() }
    viewModel { HomeworkViewModel(homeworkRepository = get()) }
    viewModel { ExamViewModel(examRepository = get()) }
    viewModel { HomeworkAddEditViewModel(homeworkRepository = get(), groupRepository = get()) }
    viewModel { ExamAddEditViewModel(examRepository = get(), groupRepository = get()) }
    viewModel { GroupManagerViewModel(groupRepository = get()) }
    viewModel { NotificationViewModel(preferences = get()) }
}

val databaseModule = module {
    single { AppDatabase.getDatabase(androidContext()) }

    single { (get() as AppDatabase).groupDao() }
    single { (get() as AppDatabase).homeworkDao() }
    single { (get() as AppDatabase).examDao() }
}

val networkModule = module {
    single { createNetworkClient() }
    single { (get() as Retrofit).create() as GroupApi }
    single { (get() as Retrofit).create() as HomeworkApi }
    single { (get() as Retrofit).create() as ExamApi }
}

val repositoryModule = module {
    single { GroupRepository(groupApi = get(), groupDao = get()) }
    single { HomeworkRepository(homeworkApi = get(), homeworkDao = get()) }
    single { ExamRepository(examApi = get(), examDao = get()) }
}

val preferenceModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
}
