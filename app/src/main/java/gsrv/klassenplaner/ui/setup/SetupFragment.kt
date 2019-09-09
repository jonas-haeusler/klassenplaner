package gsrv.klassenplaner.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import gsrv.klassenplaner.R
import gsrv.klassenplaner.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_setup.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class SetupFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
            .setDuration(200)
            .setInterpolator(FastOutSlowInInterpolator())
        sharedElementReturnTransition = sharedElementEnterTransition
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar)

        meta.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                backdrop to "backdrop"
            )
            findNavController().navigate(R.id.action_setupFragment_to_loginFragment, null, null, extras)
        }
        register.setOnClickListener { findNavController().navigate(R.id.action_setupFragment_to_registerFragment) }
    }
}
