package hr.eduwalk.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.eduwalk.R
import hr.eduwalk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
