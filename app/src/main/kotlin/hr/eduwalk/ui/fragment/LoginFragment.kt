package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.common.DefaultTextWatcher
import hr.eduwalk.databinding.FragmentLoginBinding
import hr.eduwalk.ui.event.LoginEvent
import hr.eduwalk.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment(contentLayoutId = R.layout.fragment_login) {

    override val viewModel: LoginViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = { mainActivity.finish() }

    private var isCollecting = false
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isCollecting = false
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            loginButton.setOnClickListener {
                viewModel.onLoginClicked(username = usernameEditText.text.toString())
            }
            usernameEditText.addTextChangedListener(object : DefaultTextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    loginButton.isEnabled = s?.isNotBlank() ?: false
                }
            })
        }
    }

    override fun setupObservers() {
        if (isCollecting) return
        super.setupObservers()
        lifecycleScope.launch {
            isCollecting = true
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is LoginEvent.FinishLogin -> navController.navigate(directions = LoginFragmentDirections.navigateToHomeFragment())
                    null -> {} // no-op
                }
                viewModel.onEventConsumed()
            }
        }
    }

    override fun setupUi() {}
}
