package gsrv.klassenplaner.ui.list

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import gsrv.klassenplaner.R
import gsrv.klassenplaner.ui.list.exams.ExamFragment
import gsrv.klassenplaner.ui.list.homework.HomeworkFragment

/**
 * Adapter class providing a [PagerAdapter] containing an instance of [HomeworkFragment] and [ExamFragment].
 */
class ListViewPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {



    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeworkFragment()
            1 -> ExamFragment()
            else -> throw IllegalArgumentException("No fragment for position: $position")
        }
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.homeworks)
            1 -> context.getString(R.string.exams)
            else -> super.getPageTitle(position)
        }
    }
}
