package gsrv.klassenplaner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gsrv.klassenplaner.data.database.converter.DateConverter
import gsrv.klassenplaner.data.database.daos.ExamDao
import gsrv.klassenplaner.data.database.daos.GroupDao
import gsrv.klassenplaner.data.database.daos.HomeworkDao
import gsrv.klassenplaner.data.entities.Exam
import gsrv.klassenplaner.data.entities.Group
import gsrv.klassenplaner.data.entities.Homework

/**
 * [AppDatabase] database for the application including
 *   - a table for [Homework] with the DAO [HomeworkDao]
 *   - a table for [Exam] with the DAO [ExamDao]
 *   - a table for [Group] with the DAO [GroupDao]
 *   - a date type converter [DateConverter]
 */
@Database(entities = [Homework::class, Exam::class, Group::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // The associated DAOs for the database
    abstract fun homeworkDao(): HomeworkDao
    abstract fun examDao(): ExamDao
    abstract fun groupDao(): GroupDao

    // For Singleton instantiation
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "application-database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
