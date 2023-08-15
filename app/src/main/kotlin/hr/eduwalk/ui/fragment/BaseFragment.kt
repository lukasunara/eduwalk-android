package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import hr.eduwalk.ui.activity.MainActivity
import hr.eduwalk.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected abstract val viewModel: BaseViewModel<out Any, out Any>?

    protected open var onBackPressedListener: (() -> Unit)? = null

    protected val navController by lazy { findNavController() }
    protected val mainActivity
        get() = requireActivity() as MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        setupUi()
    }

    protected abstract fun setupUi()

    @CallSuper
    protected open fun setupListeners() {
        onBackPressedListener?.let {
            mainActivity.onBackPressedDispatcher.addCallback(owner = viewLifecycleOwner) { it() }
        }
    }

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
