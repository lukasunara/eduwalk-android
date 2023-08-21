package hr.eduwalk

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EduWalkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initGoogleMaps()
    }

    private fun initGoogleMaps() = MapsInitializer.initialize(this)
}
