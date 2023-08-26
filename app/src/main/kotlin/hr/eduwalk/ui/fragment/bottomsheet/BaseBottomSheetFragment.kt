package hr.eduwalk.ui.fragment.bottomsheet

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.eduwalk.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

abstract class BaseBottomSheetFragment(@LayoutRes contentLayoutId: Int) : BottomSheetDialogFragment(contentLayoutId) {

    protected abstract val viewModel: BaseViewModel<out Any, out Any>?

    protected val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        setupUi()
    }

    protected abstract fun setupUi()

    protected abstract fun setupListeners()

    @CallSuper
    protected open fun setupObservers() {
        viewModel?.apply {
            lifecycleScope.launch {
                errorMessageFlow.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        onErrorConsumed()
                    }
                }
            }
        }
    }
}
