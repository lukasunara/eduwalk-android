package hr.eduwalk.data.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesRepository @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String = ""): String = sharedPreferences.getString(key, defaultValue)!!

    fun setString(key: String, value: String) = sharedPreferences.edit().putString(key, value).commit()

    inline fun <reified T> getObject(key: String, defaultValue: T? = null): T? =
        try {
            getString(key = key).takeIf { it.isNotEmpty() }?.let { Gson().fromJson(it, T::class.java) } ?: defaultValue
        } catch (e: Exception) {
            Log.e(this::class.java.simpleName, "getObject, key=$key, e = $e")
            defaultValue
        }

    inline fun <reified T> setObject(key: String, value: T?) {
        val encoded = value?.let { Gson().toJson(value) } ?: ""
        setString(key = key, value = encoded)
    }

    companion object {
        const val KEY_USER = "keyUser"
    }
}
