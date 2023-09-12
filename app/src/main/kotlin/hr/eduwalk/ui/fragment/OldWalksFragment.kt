package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.databinding.FragmentOldWalksBinding
import hr.eduwalk.ui.adapter.OldWalksAdapter
import hr.eduwalk.ui.viewmodel.OldWalksViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OldWalksFragment : BaseFragment(contentLayoutId = R.layout.fragment_old_walks) {

    override val viewModel: OldWalksViewModel by viewModels()

    private val oldWalksAdapter: OldWalksAdapter by lazy {
        OldWalksAdapter(onWalkClickListener = { walk ->
            navController.navigate(OldWalksFragmentDirections.navigateToWalkFragment(walk = walk))
        })
    }

    private var isCollecting = false
    private var binding: FragmentOldWalksBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOldWalksBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isCollecting = false
    }

    override fun setupUi() {
        binding?.oldWalksRecyclerView?.adapter = oldWalksAdapter
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            toolbar.backButton.setOnClickListener { mainActivity.onBackPressed() }
        }
    }

    override fun setupObservers() {
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.uiStateFlow.collect { uiState ->
                uiState.walksWithScores.takeIf { it.isNotEmpty() }?.let { oldWalksAdapter.submitList(it) }
            }
        }
    }
}
