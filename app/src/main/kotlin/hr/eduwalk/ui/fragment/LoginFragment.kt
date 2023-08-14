package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.databinding.FragmentLoginBinding
import hr.eduwalk.ui.event.LoginEvent
import hr.eduwalk.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment(contentLayoutId = R.layout.fragment_login) {

    override val viewModel: LoginViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = { mainActivity.finish() }

    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        isToolbarVisible = false
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            loginButton.setOnClickListener {
                viewModel.onLoginClicked(username = usernameEditText.text.toString())
            }
            usernameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // no-op
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // no-op
                }

                override fun afterTextChanged(s: Editable?) {
                    loginButton.isEnabled = s?.isNotBlank() ?: false
                }
            })
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is LoginEvent.FinishLogin -> navController.navigate(directions = LoginFragmentDirections.navigateToHomeFragment())
                    null -> {
                        // no-op
                    }
                }
            }
        }
    }

    override fun setupUi() {}
}
