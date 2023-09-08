package hr.eduwalk.ui.fragment

import android.app.AlertDialog
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
import hr.eduwalk.databinding.FragmentAddQuestionsBinding
import hr.eduwalk.ui.adapter.AnswersAdapter
import hr.eduwalk.ui.event.AddQuestionsEvent
import hr.eduwalk.ui.viewmodel.AddQuestionsViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddQuestionsFragment : BaseFragment(contentLayoutId = R.layout.fragment_add_questions) {

    override val viewModel: AddQuestionsViewModel by viewModels()

    private val args: AddQuestionsFragmentArgs by navArgs()
    private val answersAdapter = AnswersAdapter()

    private var binding: FragmentAddQuestionsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddQuestionsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(locationId = args.locationId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            addQuestionButton.setOnClickListener {
                // TODO navigate to create question
            }
            previousQuestionButton.setOnClickListener {
                it.isEnabled = false
                viewModel.onPreviousQuestionClicked()
            }
            nextQuestionButton.setOnClickListener {
                it.isEnabled = false
                viewModel.onNextQuestionClicked()
            }
            toolbar.apply {
                buttonOption1.setOnClickListener { viewModel.onEditQuestionClicked() }
                buttonOption2.setOnClickListener { showAlertDialog() }
                backButton.setOnClickListener { navController.popBackStack() }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is AddQuestionsEvent.NavigateToEditQuestion -> {
                        // TODO navigate to edit question
                    }
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { uiState ->
                binding?.apply {
                    uiState.question?.let { question ->
                        questionText.text = question.questionText
                        answersAdapter.updateList(answers = question.answers, correctAnswer = question.correctAnswer)
                    }
                    toolbar.apply {
                        buttonOption1.isVisible = uiState.question != null
                        buttonOption2.isVisible = uiState.question != null

                        toolbarSubtitle.apply {
                            isVisible = uiState.questionIndex != null
                            text = getString(R.string.question_number, (uiState.questionIndex ?: 0) + 1)
                        }
                    }
                    questionsContent.isVisible = uiState.isContentVisible && uiState.question != null
                    noQuestionsLabel.isVisible = uiState.isContentVisible && uiState.question == null

                    nextQuestionButton.apply {
                        isEnabled = true
                        isVisible = uiState.isNextButtonVisible
                    }
                    previousQuestionButton.apply {
                        isEnabled = true
                        isVisible = uiState.isPrevButtonVisible
                    }
                }
            }
        }
    }

    override fun setupUi() {
        binding?.apply {
            toolbar.apply {
                toolbarTitle.apply {
                    text = args.locationTitle
                    isVisible = true
                }
                buttonOption1.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
                }
                buttonOption2.apply {
                    isVisible = true
                    setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete))
                }
            }
            answersRecycleView.adapter = answersAdapter
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.do_you_want_to_delete_question)
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.onDeleteQuestionClicked() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
