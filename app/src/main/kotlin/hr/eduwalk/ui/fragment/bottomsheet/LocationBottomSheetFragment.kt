package hr.eduwalk.ui.fragment.bottomsheet

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import hr.eduwalk.R
import hr.eduwalk.databinding.BottomSheetDialogFragmentLocationBinding

class LocationBottomSheetFragment : BaseBottomSheetFragment(contentLayoutId = R.layout.fragment_home) {

    override val viewModel = null

    private val args: LocationBottomSheetFragmentArgs by navArgs()

    private var binding: BottomSheetDialogFragmentLocationBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetDialogFragmentLocationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun setupListeners() {
        binding?.apply {
            startQuizButton.setOnClickListener {
//                navController.navigate(directions = )
            }
        }
    }

    override fun setupUi() {
        binding?.apply {
            with(args.locationWithScore) {
                locationTitle.text = location.title
                locationDescription.text = location.description
                locationScore.text = score?.let {
                    getString(R.string.best_score, it)
                } ?: getString(R.string.best_score_unknown)

                Glide
                    .with(this@LocationBottomSheetFragment)
                    .load(location.imageBase64?.let { Base64.decode(it, Base64.DEFAULT) })
                    .placeholder(R.drawable.illustration_location_placeholder)
                    .error(R.drawable.illustration_location_placeholder)
                    .into(locationImage)
            }
        }
    }
}
