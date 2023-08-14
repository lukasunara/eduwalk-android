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

    protected open var onBackPressedListener = { mainActivity.onBackPressedDispatcher.onBackPressed() }

    protected val navController by lazy { findNavController() }
    protected val mainActivity
        get() = requireActivity() as MainActivity

    protected var isToolbarVisible: Boolean = false
        get() = mainActivity.isToolbarVisible
        set(value) {
            mainActivity.isToolbarVisible = value
            field = value
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        setupUi()
    }

    protected abstract fun setupUi()

    @CallSuper
    protected open fun setupListeners() {
        mainActivity.apply {
            onBackPressedDispatcher.addCallback(owner = viewLifecycleOwner) { onBackPressedListener() }
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
