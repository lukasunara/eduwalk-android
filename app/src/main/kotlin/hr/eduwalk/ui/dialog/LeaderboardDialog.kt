package hr.eduwalk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import hr.eduwalk.data.model.WalkScore
import hr.eduwalk.databinding.DialogLeaderboardBinding
import hr.eduwalk.ui.adapter.LeaderboardAdapter

class LeaderboardDialog(
    context: Context,
    private val walkScores: List<WalkScore>,
    private val currentUserScore: Int,
    private val currentUserUsername: String,
) : Dialog(context) {

    private val binding = DialogLeaderboardBinding.inflate(LayoutInflater.from(context))
    private val leaderboardAdapter = LeaderboardAdapter(currentUserUsername = currentUserUsername)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() = with(binding) {
        leaderboardRecyclerView.apply {
            adapter = leaderboardAdapter
            leaderboardAdapter.submitList(walkScores)
        }
        setupViewsForCurrentUser()
    }

    private fun DialogLeaderboardBinding.setupViewsForCurrentUser() {
        if (walkScores.none { it.username == currentUserUsername }) {
            currentUserScoreView.apply {
                usernameText.text = currentUserUsername
                userScore.text = currentUserScore.toString()
                leaderboardContentLayout.isSelected = true
            }
            currentUserScoreGroup.isVisible = true
        } else {
            currentUserScoreGroup.isVisible = false
        }
    }
}
