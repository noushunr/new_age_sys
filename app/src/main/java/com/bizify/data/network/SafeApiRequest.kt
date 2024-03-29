package com.bizify.data.network


import android.util.Log
import com.bizify.utils.ApiException
import com.bizify.utils.ErrorBodyException
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

/*
 *Created by Noushad N on 05-06-2022
*/

abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {
        try {
            val response = call.invoke()
            if (response.isSuccessful) {

                response?.body()?.toString()?.let { Log.d("response", it) }
                return response.body()!!
            } else if(response.code() == 401) {

                var error = response.errorBody()?.use {
                    it.string()
                }

                if (response.body() != null) {
                    error = Gson().toJson(response.body())
                }
                throw ErrorBodyException("401")
            } else if(response.code() == 401 || response.code() == 400 || response.code() == 100 || response.code() == 404) {
                var error = response.errorBody()?.use {
                    it.string()
                }
                if (response.body() != null) {
                    error = Gson().toJson(response.body())
                }
                throw ErrorBodyException(error)
            } else {
                val error = response.errorBody()?.use {
                    it.string()
                }

                val message = StringBuilder()
                error?.let {
                    try {
                        message.append(JSONObject(it).getString("message"))
                    } catch (e: JSONException) {
                    }
                    message.append("\n")
                }
                message.append("Error Code: ${response.code()}")
                throw ApiException("Unable to connect now. Please check your connectivity and try again..")
            }
        }catch (e:Exception){

            throw ApiException("Unable to connect now. Please check your connectivity and try again..")
        }

    }

}