package hr.eduwalk.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.eduwalk.R
import hr.eduwalk.data.model.Walk
import hr.eduwalk.data.model.WalkWithScore
import hr.eduwalk.databinding.ItemOldWalkBinding

private object WalkWithScoreDiffCallback : DiffUtil.ItemCallback<WalkWithScore>() {
    override fun areItemsTheSame(oldItem: WalkWithScore, newItem: WalkWithScore): Boolean =
        oldItem.walk.id == newItem.walk.id

    override fun areContentsTheSame(oldItem: WalkWithScore, newItem: WalkWithScore): Boolean =
        oldItem == newItem
}

class OldWalksAdapter(
    private val onWalkClickListener: (walk: Walk) -> Unit,
) : ListAdapter<WalkWithScore, OldWalksAdapter.OldWalkViewHolder>(WalkWithScoreDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OldWalkViewHolder(
        binding = ItemOldWalkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: OldWalkViewHolder, position: Int) = holder.bind(walkWithScore = getItem(position))

    inner class OldWalkViewHolder(private val binding: ItemOldWalkBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(walkWithScore: WalkWithScore) {
            binding.apply {
                oldWalkTitle.text = walkWithScore.walk.title
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    root.tooltipText = walkWithScore.walk.description
                }
                oldWalkScore.text = root.context.getString(R.string.walk_score, walkWithScore.score, walkWithScore.maxScore)
                root.setOnClickListener { onWalkClickListener(walkWithScore.walk) }
            }
        }
    }
}
