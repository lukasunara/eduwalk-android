package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.databinding.FragmentWalkBinding
import hr.eduwalk.ui.dialog.WalkInfoDialog
import hr.eduwalk.ui.event.WalkEvent
import hr.eduwalk.ui.viewmodel.WalkViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalkFragment : BaseFragment(contentLayoutId = R.layout.fragment_walk) {

    override val viewModel: WalkViewModel by viewModels()

    private val args: WalkFragmentArgs by navArgs()

    private var binding: FragmentWalkBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun setupUi() {
        binding?.apply {
            toolbar.apply {
                toolbarTitle.apply {
                    isVisible = true
                    text = args.walk.title
                }
                buttonOption1.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_info))
                }
                buttonOption2.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_leaderboard))
                }
            }
        }
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            toolbar.apply {
                backButton.setOnClickListener { navController.popBackStack() }
                buttonOption1.setOnClickListener {
                    WalkInfoDialog(context = requireContext(), walk = args.walk).show()
                }
                buttonOption2.setOnClickListener {
                    // todo: show leaderboard dialog
                }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is WalkEvent.StartWalk -> TODO()
                    null -> {
                        // no-op
                    }
                }
            }
        }
    }
}
