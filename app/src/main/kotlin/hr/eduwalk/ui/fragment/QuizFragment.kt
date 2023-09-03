package hr.eduwalk.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.databinding.FragmentQuizBinding
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.adapter.AnswersAdapter
import hr.eduwalk.ui.event.QuizEvent
import hr.eduwalk.ui.viewmodel.QuizViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizFragment : BaseFragment(contentLayoutId = R.layout.fragment_quiz) {

    override val viewModel: QuizViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = {
        when {
            binding?.startQuizGroup?.isVisible == true -> navController.popBackStack()
            binding?.nextQuestionButton?.isEnabled == true && binding?.nextQuestionButton?.text == getString(R.string.quit_quiz) -> {
                binding?.nextQuestionButton?.callOnClick()
            }
            else -> showAlertDialog()
        }
    }

    private val args: QuizFragmentArgs by navArgs()
    private val answersAdapter = AnswersAdapter(onAnswerSelected = {
        viewModel.onAnswerSelected(isCorrect = it)
        binding?.nextQuestionButton?.isEnabled = true
    })

    private var binding: FragmentQuizBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(locationId = args.locationId, currentScore = args.currentScore)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            startQuizButton.setOnClickListener { viewModel.onStartQuizClicked() }
            nextQuestionButton.setOnClickListener {
                nextQuestionButton.isEnabled = false
                viewModel.onNextQuestionClicked()
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    QuizEvent.StartQuiz -> {
                        binding?.apply {
                            startQuizGroup.isVisible = false
                            questionsGroup.isVisible = true
                            toolbar.toolbarSubtitle.isVisible = true
                        }
                    }
                    is QuizEvent.FinishQuiz -> {
                        val bundle = bundleOf("newScore" to event.correctAnswers, "locationId" to args.locationId)
                        setFragmentResult("quizFragmentResult", bundle)
                        navController.navigateUp()
                    }
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { uiState ->
                uiState.question?.let { question ->
                    binding?.apply {
                        questionText.text = question.questionText
                        toolbar.toolbarSubtitle.text = getString(R.string.question_number, (uiState.questionIndex ?: 0) + 1)
                        nextQuestionButton.apply {
                            uiState.questionIndex?.let {
                                if (it == EduWalkRepository.NUMBER_OF_QUESTIONS_PER_QUIZ - 1) {
                                    text = getString(R.string.quit_quiz)
                                }
                            }
                        }
                    }
                    answersAdapter.updateList(answers = question.answers, correctAnswer = question.correctAnswer)
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
                backButton.setOnClickListener { mainActivity.onBackPressed() }
            }
            answersRecycleView.adapter = answersAdapter
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.do_you_want_to_exit_quiz)
            .setPositiveButton(R.string.quit_quiz) { _, _ -> navController.popBackStack() }
            .setNegativeButton(R.string.continue_quiz) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
