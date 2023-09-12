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
import hr.eduwalk.databinding.FragmentEditQuestionBinding
import hr.eduwalk.ui.event.EditQuestionEvent
import hr.eduwalk.ui.viewmodel.EditQuestionViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditQuestionFragment : BaseFragment(contentLayoutId = R.layout.fragment_edit_question) {

    override val viewModel: EditQuestionViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = {
        when {
            binding?.editQuestionButton?.isEnabled == true -> showLeaveAlertDialog()
            else -> navController.popBackStack()
        }
    }

    private val args: EditQuestionFragmentArgs by navArgs()

    private var binding: FragmentEditQuestionBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditQuestionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(question = args.question)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            editQuestionButton.setOnClickListener {
                viewModel.onEditQuestionClicked(
                    questionText = questionTextEditText.text.toString(),
                    correctAnswer = answerCorrectEditText.text.toString(),
                    answer2 = answer2EditText.text?.toString()?.takeIf { it.isNotBlank() },
                    answer3 = answer3EditText.text?.toString()?.takeIf { it.isNotBlank() },
                    answer4 = answer4EditText.text?.toString()?.takeIf { it.isNotBlank() },
                    answer5 = answer5EditText.text?.toString()?.takeIf { it.isNotBlank() },
                )
                clearInputAnswers()
            }
            questionTextEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editQuestionButton.isEnabled = s?.isNotBlank() == true &&
                        answerCorrectEditText.text?.isNotBlank() == true &&
                        areThereTwoAnswers()
                }
            })
            answerCorrectEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editQuestionButton.isEnabled = s?.isNotBlank() == true &&
                        questionTextEditText.text?.isNotBlank() == true &&
                        areThereTwoAnswers()
                }
            })
            answer2EditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editQuestionButton.isEnabled = areThereTwoAnswers() &&
                        answerCorrectEditText.text?.isNotBlank() == true &&
                        questionTextEditText.text?.isNotBlank() == true
                }
            })
            answer3EditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editQuestionButton.isEnabled = areThereTwoAnswers() &&
                        answerCorrectEditText.text?.isNotBlank() == true &&
                        questionTextEditText.text?.isNotBlank() == true
                }
            })
            answer4EditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editQuestionButton.isEnabled = areThereTwoAnswers() &&
                        answerCorrectEditText.text?.isNotBlank() == true &&
                        questionTextEditText.text?.isNotBlank() == true
                }
            })
            answer5EditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editQuestionButton.isEnabled = areThereTwoAnswers() &&
                        answerCorrectEditText.text?.isNotBlank() == true &&
                        questionTextEditText.text?.isNotBlank() == true
                }
            })
            toolbar.backButton.setOnClickListener { mainActivity.onBackPressed() }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is EditQuestionEvent.FinishEditQuestion -> {
                        val bundle = bundleOf("newQuestion" to event.question)
                        setFragmentResult("editQuestionFragmentResult", bundle)
                        navController.navigateUp()
                    }
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { uiState ->
                val question = uiState.question ?: return@collect
                binding?.apply {
                    with(question) {
                        questionText.takeIf { it.isNotBlank() }?.let { questionTextEditText.setText(it) }
                        correctAnswer.takeIf { it.isNotBlank() }?.let { answerCorrectEditText.setText(it) }

                        answers.filter { it != correctAnswer }.apply {
                            getOrNull(0)?.let { answer2EditText.setText(it) }
                            getOrNull(1)?.let { answer3EditText.setText(it) }
                            getOrNull(2)?.let { answer4EditText.setText(it) }
                            getOrNull(3)?.let { answer5EditText.setText(it) }
                        }
                    }

                    val editQuestionButtonTextResId = if (question.id == -1L) R.string.create_question else R.string.save_changes
                    editQuestionButton.apply {
                        text = getString(editQuestionButtonTextResId)
                        isEnabled = false
                    }
                }
            }
        }
    }

    override fun setupUi() {}

    private fun FragmentEditQuestionBinding.clearInputAnswers() {
        answer2EditText.text = null
        answer3EditText.text = null
        answer4EditText.text = null
        answer5EditText.text = null
    }

    private fun FragmentEditQuestionBinding.areThereTwoAnswers() = answerCorrectEditText.text?.isNotBlank() == true &&
        (answer2EditText.text?.isNotBlank() == true ||
            answer3EditText.text?.isNotBlank() == true ||
            answer4EditText.text?.isNotBlank() == true ||
            answer5EditText.text?.isNotBlank() == true)

    private fun showLeaveAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.edit_walk_do_you_want_to_exit)
            .setPositiveButton(R.string.quit) { _, _ -> navController.popBackStack() }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
