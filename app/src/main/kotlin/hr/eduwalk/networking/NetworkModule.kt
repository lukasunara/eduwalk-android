package hr.eduwalk.networking

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://192.168.0.22:8080/"

    private const val CACHE_DIRECTORY_OKHTTP = "okhttp_cache"
    private const val MAX_CACHE_SIZE = 20 * 1024L * 1024L

    private val MEDIA_TYPE = "application/json".toMediaType()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache = Cache(directory = File(context.cacheDir, CACHE_DIRECTORY_OKHTTP), maxSize = MAX_CACHE_SIZE))
            .build()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType = MEDIA_TYPE))
//            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): EduWalkApiService = retrofit.create(EduWalkApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(eduWalkApiService: EduWalkApiService) = EduWalkRepository(apiService = eduWalkApiService)
}
