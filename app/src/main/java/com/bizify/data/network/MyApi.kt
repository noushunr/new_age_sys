package com.bizify.data.network

import android.content.Context
import com.bizify.data.model.*
import com.bizify.utils.BASE_URL
import com.bizify.utils.SessionUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


/*
 *Created by Noushad on 05-06-2022
*/

interface MyApi {

    @GET("apod")
    suspend fun postList(
        @Query("api_key") api_key: String,
        @Query("count") count: String,

        ): Response<List<AodpList>>

    @POST("Authentication")
    suspend fun login(@Body requestBody: RequestBody): Response<User>

    @GET("api/Customer/SearchCustomers")
    suspend fun getCustomerList(@Header("Authorization") authHeader : String): Response<List<Customers>>

    @GET("api/Vehicle/SearchVehicle")
    suspend fun getVehicleList(@Header("Authorization") authHeader : String): Response<List<Vehicles>>

    @POST("api/VehicleJoborder/CreateJobOrder")
    suspend fun addJob(@Body requestBody: RequestBody,@Header("Authorization") authHeader : String): Response<CreateJobResponse>

    @GET("api/VehicleJoborder/SearchJobOrders")
    suspend fun getJobOrders(@Header("Authorization") authHeader : String): Response<List<CreateJobResponse>>

    @POST("api/Customer/AddCustomer")
    suspend fun addCustomer(@Body requestBody: RequestBody,@Header("Authorization") authHeader : String): Response<Customers>

    @POST("api/Vehicle/AddVehicle")
    suspend fun addVehicle(@Body requestBody: RequestBody,@Header("Authorization") authHeader : String): Response<Vehicles>

    companion object {
        operator fun invoke(
            context: Context,
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi {
            val logging = HttpLoggingInterceptor()
            val sessionUtils = SessionUtils(context)
            logging.level = HttpLoggingInterceptor.Level.BODY
            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    var newRequest: Request =
                        //                            if (!SessionUtils.token.isNullOrEmpty()) {
                        chain.request().newBuilder()
                            //                                .addHeader("Authorization", "Bearer ${SessionUtils.token}")
                            .build()
                    //                        }else{
                    //                            chain.request().newBuilder()
                    //                                .build()
                    //                        }
                    chain.proceed(newRequest)
                }
                .callTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build()
//            okkHttpclient?.newBuilder()?.addInterceptor { chain ->
//                val request: Request =
//                    chain.request().newBuilder().addHeader("api-key", "DEMO_KEY")
//                        .build()
//                chain.proceed(request)
//            }

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl(sessionUtils.BASE_URL!!)
//                .baseUrl(TEST_BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}