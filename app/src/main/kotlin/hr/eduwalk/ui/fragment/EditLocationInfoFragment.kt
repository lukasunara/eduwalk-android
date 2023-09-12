package hr.eduwalk.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.common.DefaultTextWatcher
import hr.eduwalk.databinding.FragmentEditLocationInfoBinding
import hr.eduwalk.ui.event.EditLocationEvent
import hr.eduwalk.ui.viewmodel.EditLocationViewModel
import kotlin.properties.Delegates
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditLocationInfoFragment : BaseFragment(contentLayoutId = R.layout.fragment_edit_location_info) {

    override val viewModel: EditLocationViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = {
        when {
            binding?.editLocationButton?.isEnabled == true -> showLeaveAlertDialog()
            else -> navController.popBackStack()
        }
    }

    private val args: EditLocationInfoFragmentArgs by navArgs()

    private var isCollecting = false
    private var binding: FragmentEditLocationInfoBinding? = null
    private var thresholdDistance by Delegates.notNull<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditLocationInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        thresholdDistance = args.location.thresholdDistance
        viewModel.start(location = args.location)
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

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            editLocationButton.setOnClickListener {
                viewModel.onEditLocationClicked(
                    locationTitle = locationTitleEditText.text.toString(),
                    locationDescription = locationDescriptionEditText.text?.toString()?.takeIf { it.isNotBlank() },
                    imageBase64 = null,
                    thresholdDistance = thresholdDistance,
                )
            }
            addQuestionsButton.setOnClickListener {
                viewModel.onAddQuestionClicked(
                    locationTitle = locationTitleEditText.text.toString(),
                    locationDescription = locationDescriptionEditText.text?.toString()?.takeIf { it.isNotBlank() },
                    imageBase64 = null,
                    thresholdDistance = thresholdDistance,
                )
            }
            locationTitleEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editLocationButton.isEnabled = s?.isNotBlank() ?: false
                }
            })
            locationDescriptionEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editLocationButton.isEnabled = true
                }
            })
            locationThresholdEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    thresholdDistance = s?.toString()?.toIntOrNull() ?: 20

                    val newText = getString(R.string.edit_location_threshold_text, thresholdDistance)
                    val startIndex = newText.indexOf(thresholdDistance.toString())
                    val spannable = SpannableString(newText).apply {
                        setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)),
                            startIndex,
                            startIndex + thresholdDistance.toString().length,
                            0,
                        )
                    }
                    binding?.locationThresholdInputLayout?.hint = spannable
                }

                override fun afterTextChanged(s: Editable?) {
                    editLocationButton.isEnabled = true
                }
            })
            addLocationImage.setOnClickListener {
                // TODO add image (pripazi na enablanje i disablanje buttona)
            }
            locationImage.setOnClickListener {
                // TODO add image (pripazi na enablanje i disablanje buttona)
            }
            toolbar.apply {
                backButton.setOnClickListener { mainActivity.onBackPressed() }
                buttonOption2.setOnClickListener { showDeleteAlertDialog() }
            }
        }
    }

    override fun setupObservers() {
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is EditLocationEvent.FinishDeleteLocation -> {
//                        val bundle = bundleOf("locationId" to event.locationId)
//                        setFragmentResult("deleteLocationFragmentResult", bundle)
                        navController.navigateUp()
                    }
                    is EditLocationEvent.NavigateToAddQuestions -> {
                        navController.navigate(
                            directions = EditLocationInfoFragmentDirections.navigateToAddQuestionsFragment(
                                locationId = event.locationId,
                                locationTitle = event.locationTitle,
                            )
                        )
                    }
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { uiState ->
                val location = uiState.location ?: return@collect
                binding?.apply {
                    locationThresholdEditText.setText(location.thresholdDistance.toString())
                    location.title.takeIf { it.isNotBlank() }?.let { locationTitleEditText.setText(it) }
                    location.description?.let { locationDescriptionEditText.setText(it) }

                    location.imageBase64?.let {
                        Glide
                            .with(this@EditLocationInfoFragment)
                            .load(Base64.decode(it, Base64.DEFAULT))
                            .into(locationImage)
                        addLocationImage.isVisible = false
                        locationImage.isVisible = true
                    } ?: run {
                        locationImage.isVisible = false
                        addLocationImage.isVisible = true
                    }

                    val editLocationButtonTextResId = if (location.id == -1L) R.string.create_location else R.string.save_changes
                    editLocationButton.apply {
                        text = getString(editLocationButtonTextResId)
                        isEnabled = false
                    }
                    addQuestionsButton.isEnabled = location.id != -1L
                    toolbar.buttonOption2.isVisible = location.id != -1L
                }
            }
        }
    }

    override fun setupUi() {
        binding?.toolbar?.buttonOption2?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete))
    }

    private fun showDeleteAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.do_you_want_to_delete_location)
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.onDeleteLocationClicked() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showLeaveAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.edit_walk_do_you_want_to_exit)
            .setPositiveButton(R.string.quit) { _, _ -> navController.popBackStack() }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
