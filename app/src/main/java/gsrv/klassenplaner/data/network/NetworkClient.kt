package gsrv.klassenplaner.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gsrv.klassenplaner.BuildConfig
import gsrv.klassenplaner.data.network.converter.DateJsonConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*

private const val BASE_URL = BuildConfig.BASE_URL

fun createNetworkClient(baseUrl: String = BASE_URL) = retrofitClient(baseUrl, httpClient(), gson())

private fun httpClient(): OkHttpClient {
    Timber.d("Created OkHttpClient client")
    return OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            Timber.tag("OkHttp").d(message)
        }).setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()
}

private fun gson(): Gson {
    Timber.d("Created Gson client")
    return GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateJsonConverter())
        .excludeFieldsWithoutExposeAnnotation()
        .create()
}

private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient, gson: Gson): Retrofit {
    Timber.d("Created Retrofit client")
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}
