package gsrv.klassenplaner.util

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

fun ExtendedFloatingActionButton.shrinkAndHide() {
    shrink(object : ExtendedFloatingActionButton.OnChangedListener() {
        override fun onShrunken(extendedFab: ExtendedFloatingActionButton?) {
            hide()
        }
    })
}

fun ExtendedFloatingActionButton.showAndExtend() {
    show(object : ExtendedFloatingActionButton.OnChangedListener() {
        override fun onShown(extendedFab: ExtendedFloatingActionButton?) {
            extend()
        }
    })
}
