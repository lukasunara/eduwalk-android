package hr.eduwalk.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.eduwalk.R
import hr.eduwalk.databinding.ItemAnswerBinding

private object StringDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
}

class AnswersAdapter(
    private val onAnswerSelected: ((isCorrect: Boolean) -> Unit)? = null,
) : ListAdapter<String, AnswersAdapter.AnswerViewHolder>(StringDiffCallback) {

    private val LETTERS_MAP = mapOf(0 to "a)", 1 to "b)", 2 to "c)", 3 to "d)", 4 to "e)")

    private var correctAnswer: String? = null
    private var canShowCorrectAnswer = false
    private var correctAnswerPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AnswerViewHolder(
        binding = ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) = holder.bind(answer = getItem(position), position = position)

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(answers: List<String>, correctAnswer: String) {
        canShowCorrectAnswer = false
        correctAnswerPosition = null
        this.correctAnswer = correctAnswer

        submitList(answers)
        notifyDataSetChanged()
    }

    private fun showCorrectAnswer() = correctAnswerPosition?.let { notifyDataSetChanged() }

    inner class AnswerViewHolder(private val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(answer: String, position: Int) = with(binding) {
            if (onAnswerSelected == null) root.isClickable = false

            answerText.text = answer
            answerLetter.text = LETTERS_MAP[position]!!

            if (!canShowCorrectAnswer) {
                updateUi(
                    textColorResId = R.color.black,
                    backgroundColorResId = R.color.seed_light,
                    iconDrawableResId = null,
                )
            }

            val isCorrect = answer == correctAnswer
            if (isCorrect) {
                correctAnswerPosition = position
                if (canShowCorrectAnswer) {
                    updateUi(
                        textColorResId = R.color.green,
                        backgroundColorResId = R.color.green_light,
                        iconDrawableResId = R.drawable.ic_check,
                    )
                }
            }
            root.setOnClickListener {
                if (canShowCorrectAnswer) return@setOnClickListener

                onAnswerSelected?.let {
                    it(isCorrect)
                    canShowCorrectAnswer = true
                } ?: return@setOnClickListener

                if (!isCorrect) {
                    updateUi(
                        textColorResId = R.color.red,
                        backgroundColorResId = R.color.red_light,
                        iconDrawableResId = R.drawable.ic_wrong,
                    )
                }
                showCorrectAnswer()
            }
        }

        private fun updateUi(
            @ColorRes textColorResId: Int,
            @ColorRes backgroundColorResId: Int,
            @DrawableRes iconDrawableResId: Int?,
        ) {
            binding.apply {
                val context = root.context
                root.backgroundTintList = ContextCompat.getColorStateList(context, backgroundColorResId)

                val textColor = ContextCompat.getColor(context, textColorResId)
                answerLetter.setTextColor(textColor)
                answerText.setTextColor(textColor)

                iconDrawableResId?.let {
                    resultIcon.setImageDrawable(ContextCompat.getDrawable(context, iconDrawableResId))
                    resultIcon.isVisible = true
                } ?: run {
                    resultIcon.visibility = View.INVISIBLE
                }
            }
        }
    }
}