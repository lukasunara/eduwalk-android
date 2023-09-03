package hr.eduwalk.ui.fragment.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import hr.eduwalk.R
import hr.eduwalk.databinding.BottomSheetDialogFragmentLocationBinding

class LocationBottomSheetFragment : BaseBottomSheetFragment(contentLayoutId = R.layout.fragment_home) {

    override val viewModel = null

    private val args: LocationBottomSheetFragmentArgs by navArgs()

    private var bestScore: Int? = null
    private var binding: BottomSheetDialogFragmentLocationBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetDialogFragmentLocationBinding.inflate(inflater, container, false)
        Log.d("SUKI", "LocationBottomSheetFragment -> onCreateView")
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("SUKI", "LocationBottomSheetFragment -> locationWithScore=${args.locationWithScore}")
        bestScore = args.locationWithScore.score
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SUKI", "LocationBottomSheetFragment -> onDestroy")
        binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.d("SUKI", "LocationBottomSheetFragment -> onDismiss")
        bestScore?.let {
            val bundle = bundleOf("newScore" to it, "locationId" to args.locationWithScore.location.id)
            setFragmentResult("locationBottomSheetFragmentResult", bundle)
        }
        super.onDismiss(dialog)
    }

    override fun setupListeners() {
        setFragmentResultListener("quizFragmentResult") { _, result ->
            val newScore = result.getInt("newScore")
            val oldScore = bestScore
            Log.d("SUKI", "LocationBottomSheetFragment -> fragment result -> newScore=$newScore; oldScore=$oldScore")
            if (oldScore == null || oldScore < newScore) {
                bestScore = newScore
                binding?.locationScore?.text = getString(R.string.best_score, newScore)
            }
        }
        binding?.apply {
            goToQuizButton.setOnClickListener {
                with(args.locationWithScore) {
                    navController.navigate(
                        directions = LocationBottomSheetFragmentDirections.navigateToQuizFragment(location.id, location.title, bestScore ?: -1)
                    )
                }
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
