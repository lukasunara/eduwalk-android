package hr.eduwalk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import hr.eduwalk.databinding.DialogPermissionRationaleBinding

class PermissionRationaleDialog(
    context: Context,
    private val showEnablePermissionText: Boolean,
    private val onRationaleButtonClicked: (() -> Unit)? = null,
) : Dialog(context) {

    private val binding = DialogPermissionRationaleBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUi()
        setupListeners()
    }

    private fun setupUi() {
        binding.rationalePleaseEnablePermission.isVisible = showEnablePermissionText
    }

    private fun setupListeners() {
        binding.rationaleButton.setOnClickListener {
            dismiss()
            onRationaleButtonClicked?.invoke()
        }
        setCancelable(false)
    }
}
