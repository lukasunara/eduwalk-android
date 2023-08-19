package hr.eduwalk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import hr.eduwalk.R
import hr.eduwalk.data.model.Walk
import hr.eduwalk.databinding.DialogWalkInfoBinding

class WalkInfoDialog(
    context: Context,
    private val walk: Walk,
) : Dialog(context) {

    private val binding = DialogWalkInfoBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() = with(binding) {
        walkTitle.text = walk.title
        walkId.text = context.getString(R.string.walk_id, walk.id)
        walkDescription.text = walk.description
    }
}
