package hr.eduwalk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import hr.eduwalk.R
import hr.eduwalk.data.model.User
import hr.eduwalk.data.sharedprefs.SharedPreferencesRepository
import hr.eduwalk.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    protected lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewBinding()
        setupStartDestinationId()
    }

    private fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupStartDestinationId() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val currentUser = sharedPreferencesRepository.getObject<User>(key = SharedPreferencesRepository.KEY_USER)
        val startDestinationId = if (currentUser == null) {
            R.id.fragment_login
        } else {
            R.id.fragment_home
        }
        val navGraph = navController.navInflater
            .inflate(graphResId = R.navigation.nav_graph_main)
            .apply {
                setStartDestination(startDestinationId)
            }
        navController.graph = navGraph
    }
}
