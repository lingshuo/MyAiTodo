package com.lingshuo.myaitodo.data.remote

import com.lingshuo.myaitodo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds the app's single [OkHttpClient] / [Retrofit] pair.
 *
 * Deliberately a plain object instead of a DI module: the graph is tiny and the
 * rest of the app already uses the manual [com.lingshuo.myaitodo.AppContainer].
 */
object NetworkModule {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private const val TIMEOUT_SECONDS = 10L

    /**
     * Lenient enough to survive jsonplaceholder's occasional extra fields, and
     * `explicitNulls = false` keeps request bodies tidy if we ever POST.
     */
    val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                // Request/response bodies are only logged for debuggable builds.
                if (BuildConfig.ENABLE_HTTP_LOGGING) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        },
                    )
                }
            }
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val todoApiService: TodoApiService by lazy { retrofit.create(TodoApiService::class.java) }
}
