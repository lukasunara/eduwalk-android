package hr.eduwalk.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.data.model.Question
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

    private var isCollecting = false
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
        viewModel.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isCollecting = false
    }

    override fun setupListeners() {
        super.setupListeners()
        setFragmentResultListener("editQuestionFragmentResult") { _, result ->
            val newQuestion = result.getParcelable<Question>("newQuestion")
            viewModel.start(locationId = args.locationId)
        }
        binding?.apply {
            addQuestionButton.setOnClickListener {
                navController.navigate(
                    directions = AddQuestionsFragmentDirections.navigateToEditQuestion(
                        question = Question(
                            id = -1,
                            questionText = "",
                            answers = listOf(),
                            correctAnswer = "",
                            locationId = args.locationId,
                        ),
                    )
                )
            }
            previousQuestionButton.setOnClickListener {
                it.isEnabled = false
                nextQuestionButton.isEnabled = false
                viewModel.onPreviousQuestionClicked()
            }
            nextQuestionButton.setOnClickListener {
                it.isEnabled = false
                previousQuestionButton.isEnabled = false
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
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is AddQuestionsEvent.NavigateToEditQuestion -> navController.navigate(
                        directions = AddQuestionsFragmentDirections.navigateToEditQuestion(question = event.question)
                    )
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { uiState ->
                binding?.apply {
                    val questionOkay = uiState.question?.let { it.id != -1L } ?: false

                    uiState.question?.let { question ->
                        questionText.text = question.questionText
                        answersAdapter.updateList(answers = question.answers, correctAnswer = question.correctAnswer)
                    }
                    toolbar.apply {
                        buttonOption1.isVisible = questionOkay
                        buttonOption2.isVisible = questionOkay

                        toolbarSubtitle.apply {
                            isVisible = uiState.questionIndex != null
                            text = getString(R.string.question_number, (uiState.questionIndex ?: 0) + 1)
                        }
                    }
                    questionsContent.isVisible = uiState.isContentVisible && questionOkay
                    noQuestionsLabel.isVisible = uiState.isContentVisible && !questionOkay

                    nextQuestionButton.apply {
                        isEnabled = true
                        isInvisible = uiState.isNextButtonVisible.not()
                    }
                    previousQuestionButton.apply {
                        isEnabled = true
                        isInvisible = uiState.isPrevButtonVisible.not()
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
                buttonOption1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))
                buttonOption2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete))
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
