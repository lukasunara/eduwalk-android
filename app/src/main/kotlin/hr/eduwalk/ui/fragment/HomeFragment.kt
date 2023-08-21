package hr.eduwalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.data.model.User
import hr.eduwalk.data.model.UserRole
import hr.eduwalk.data.sharedprefs.SharedPreferencesRepository
import hr.eduwalk.databinding.FragmentHomeBinding
import hr.eduwalk.ui.event.HomeEvent
import hr.eduwalk.ui.viewmodel.HomeViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(contentLayoutId = R.layout.fragment_home) {

    override val viewModel: HomeViewModel by viewModels()

    override var onBackPressedListener: (() -> Unit)? = { mainActivity.finish() }

    private var binding: FragmentHomeBinding? = null

    private lateinit var user: User

    @Inject
    protected lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = requireNotNull(value = sharedPreferencesRepository.getObject<User>(key = SharedPreferencesRepository.KEY_USER))

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun setupListeners() {
        super.setupListeners()
        binding?.apply {
            startNewWalkButton.setOnClickListener {
                navController.navigate(directions = HomeFragmentDirections.navigateToStartNewWalkFragment())
            }
            logoutButton.setOnClickListener { viewModel.onLogoutClicked() }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        lifecycleScope.launch {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is HomeEvent.UserLogout -> navController.navigate(directions = HomeFragmentDirections.navigateToLoginFragment())
                    null -> {
                        // no-op
                    }
                }
                viewModel.onEventConsumed()
            }
        }
    }

    override fun setupUi() {
        binding?.apply {
            teacherGroup.isVisible = user.role == UserRole.TEACHER

            val welcomeHomeTextResId = when (user.role) {
                UserRole.STUDENT -> R.string.welcome_student_title
                UserRole.TEACHER -> R.string.welcome_teacher_title
            }
            welcomeHomeTitle.text = getString(welcomeHomeTextResId, user.username)
        }
    }
}
