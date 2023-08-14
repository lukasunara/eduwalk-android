package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hr.eduwalk.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

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
