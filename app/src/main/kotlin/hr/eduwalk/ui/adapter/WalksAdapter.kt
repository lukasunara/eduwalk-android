package hr.eduwalk.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import hr.eduwalk.data.model.Walk
import hr.eduwalk.databinding.ItemWalkBinding

private object WalkDiffCallback : DiffUtil.ItemCallback<Walk>() {
    override fun areItemsTheSame(oldItem: Walk, newItem: Walk): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Walk, newItem: Walk): Boolean =
        oldItem == newItem
}

class WalksAdapter(
    private val onWalkClickListener: (walkId: String) -> Unit,
) : ListAdapter<Walk, WalksAdapter.WalkViewHolder>(WalkDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = WalkViewHolder(
        binding = ItemWalkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: WalkViewHolder, position: Int) = holder.bind(walk = getItem(position))

    inner class WalkViewHolder(private val binding: ItemWalkBinding) : ViewHolder(binding.root) {

        fun bind(walk: Walk) {
            binding.walkButton.apply {
                text = walk.title
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tooltipText = walk.description
                }
                setOnClickListener { onWalkClickListener(walk.id) }
            }
        }
    }
}
