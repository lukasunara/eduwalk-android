package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.databinding.FragmentMyWalksBinding
import hr.eduwalk.ui.adapter.WalksAdapter
import hr.eduwalk.ui.viewmodel.MyWalksViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyWalksFragment : BaseFragment(contentLayoutId = R.layout.fragment_my_walks) {

    override val viewModel: MyWalksViewModel by viewModels()

    private val walksAdapter: WalksAdapter by lazy {
        WalksAdapter(onWalkClickListener = { walk ->
            navController.navigate(MyWalksFragmentDirections.navigateToRouteFragment(walk = walk))
        })
    }

    private var isCollecting = false
    private var binding: FragmentMyWalksBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyWalksBinding.inflate(inflater, container, false)
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
        binding?.myWalksRecyclerView?.adapter = walksAdapter
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
                uiState.walks.takeIf { it.isNotEmpty() }?.let { walksAdapter.submitList(it) }
            }
        }
    }
}
