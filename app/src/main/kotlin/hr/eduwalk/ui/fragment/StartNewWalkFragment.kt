package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.common.DefaultTextWatcher
import hr.eduwalk.databinding.FragmentStartNewWalkBinding
import hr.eduwalk.ui.adapter.WalksAdapter
import hr.eduwalk.ui.event.StartNewWalkEvent
import hr.eduwalk.ui.viewmodel.StartNewWalkViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartNewWalkFragment : BaseFragment(contentLayoutId = R.layout.fragment_start_new_walk) {

    override val viewModel: StartNewWalkViewModel by viewModels()

    private val walksAdapter: WalksAdapter by lazy {
        WalksAdapter(onWalkClickListener = { walk -> viewModel.onDefaultWalkClicked(walkId = walk.id) })
    }

    private var isCollecting = false
    private var binding: FragmentStartNewWalkBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStartNewWalkBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isCollecting = false
    }

    override fun setupUi() {
        binding?.defaultWalksRecyclerView?.adapter = walksAdapter
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            toolbar.backButton.setOnClickListener { mainActivity.onBackPressed() }
            startWalkButton.setOnClickListener {
                viewModel.onStartWalkClicked(walkId = walkIdEditText.text.toString())
            }
            walkIdEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    startWalkButton.isEnabled = s?.trim()?.length == WALK_ID_LENGTH
                }
            })
        }
    }

    override fun setupObservers() {
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is StartNewWalkEvent.StartWalk -> {
                        navController.navigate(StartNewWalkFragmentDirections.navigateToWalkFragment(walk = event.walk))
                    }
                    is StartNewWalkEvent.ShowDefaultWalks -> walksAdapter.submitList(event.walks)
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
    }

    private companion object {
        const val WALK_ID_LENGTH = 8
    }
}
