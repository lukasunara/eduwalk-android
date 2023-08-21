package hr.eduwalk.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.eduwalk.data.model.WalkScore
import hr.eduwalk.databinding.ItemLeaderboardUserScoreBinding

private object WalkScoreDiffCallback : DiffUtil.ItemCallback<WalkScore>() {
    override fun areItemsTheSame(oldItem: WalkScore, newItem: WalkScore): Boolean =
        oldItem.walkId == newItem.walkId && oldItem.username == newItem.username

    override fun areContentsTheSame(oldItem: WalkScore, newItem: WalkScore): Boolean =
        oldItem == newItem
}

class LeaderboardAdapter(
    private val currentUserUsername: String,
) : ListAdapter<WalkScore, LeaderboardAdapter.WalkScoreViewHolder>(WalkScoreDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WalkScoreViewHolder(
        binding = ItemLeaderboardUserScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WalkScoreViewHolder, position: Int) = holder.bind(walkScore = getItem(position))

    inner class WalkScoreViewHolder(private val binding: ItemLeaderboardUserScoreBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(walkScore: WalkScore) = with(binding) {
            usernameText.text = walkScore.username
            userScore.text = walkScore.score.toString()
            leaderboardContentLayout.isSelected = currentUserUsername == walkScore.username
        }
    }
}
