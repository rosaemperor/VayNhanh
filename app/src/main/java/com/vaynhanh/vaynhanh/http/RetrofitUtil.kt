package club.rosaemperor.myeyesopen.http

import android.util.Log
import com.vaynhanh.vaynhanh.commons.config
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitUtil private constructor() {
    lateinit var retrofit: Retrofit
    lateinit var help: HttpService
        private set
    private var client: OkHttpClient? = null
    private var loggingInterceptor: HttpLoggingInterceptor? = null

    init {
        init()
    }

    private fun init() {
        loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor!!.level = HttpLoggingInterceptor.Level.BODY
        client = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): Response {
                val response = chain!!.proceed(chain.request())
                Log.d("TAG", "" + response.body()!!.string())

                val request = chain.request().newBuilder()
                        .addHeader("Content-Type", "text/html; charset=UTF-8")
                        .build()
                Log.d("TAG",""+request.body().toString())
                return chain.proceed(request)            }
        })
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()


        retrofit = Retrofit.Builder()
                //                .baseUrl( "http://api.ih2ome.cn/")
                .baseUrl(config.HOST_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        help = retrofit.let { retrofit.create(HttpService::class.java) }

    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    companion object {
        val instance = RetrofitUtil()
    }
}