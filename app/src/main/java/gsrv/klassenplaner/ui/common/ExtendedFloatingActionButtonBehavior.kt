package gsrv.klassenplaner.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class ExtendedFloatingActionButtonBehavior<T : ExtendedFloatingActionButton>(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<T>(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: T, directTargetChild: View,
        target: View, axes: Int, type: Int
    ): Boolean {

        return if (axes == ViewCompat.SCROLL_AXIS_VERTICAL) {
            true
        } else {
            super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: T,
        target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        type: Int, consumed: IntArray
    ) {
        if (dyConsumed <= 0 && dyUnconsumed < 0) {
            child.extend()
        } else if (dyConsumed > 0) {
            child.shrink()
        }
    }
}
