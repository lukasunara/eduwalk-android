package hr.eduwalk.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.common.DefaultTextWatcher
import hr.eduwalk.databinding.FragmentEditWalkInfoBinding
import hr.eduwalk.ui.event.EditWalkEvent
import hr.eduwalk.ui.viewmodel.EditWalkViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditWalkInfoFragment : BaseFragment(contentLayoutId = R.layout.fragment_edit_walk_info) {

    override val viewModel: EditWalkViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = {
        when {
            binding?.editWalkButton?.isEnabled == true -> showAlertDialog()
            else -> navController.popBackStack()
        }
    }

    private val args: EditWalkInfoFragmentArgs by navArgs()

    private var isCollecting = false
    private var binding: FragmentEditWalkInfoBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditWalkInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(walk = args.walk)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isCollecting = false
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            editWalkButton.setOnClickListener {
                viewModel.onEditWalkClicked(
                    walkTitle = walkTitleEditText.text.toString(),
                    walkDescription = walkDescriptionEditText.text?.toString()?.takeIf { it.isNotBlank() },
                )
            }
            walkTitleEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editWalkButton.isEnabled = s?.isNotBlank() ?: false
                }
            })
            toolbar.backButton.setOnClickListener { mainActivity.onBackPressed() }
        }
    }

    override fun setupObservers() {
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is EditWalkEvent.FinishCreatingWalk -> {
                        navController.navigate(directions = EditWalkInfoFragmentDirections.navigateToRouteFragment(walk = event.walk))
                    }
                    is EditWalkEvent.FinishEditingWalkInfo -> {
                        val bundle = bundleOf("walk" to event.walk)
                        setFragmentResult("editWalkInfoFragmentResult", bundle)
                        navController.navigateUp()
                    }
                    is EditWalkEvent.SetupWalkId -> binding?.walkIdLabel?.text = getString(R.string.walk_id, event.walkId)
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
    }

    override fun setupUi() {
        binding?.apply {
            val editWalkButtonTextResId = if (args.walk == null) R.string.create_walk else R.string.save_changes
            editWalkButton.text = getString(editWalkButtonTextResId)

            args.walk?.let { walk ->
                walkIdLabel.text = getString(R.string.walk_id, walk.id)
                walkTitleEditText.setText(walk.title)
                walk.description?.let { walkDescriptionEditText.setText(it) }
            }
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.edit_walk_do_you_want_to_exit)
            .setPositiveButton(R.string.quit) { _, _ -> navController.popBackStack() }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
