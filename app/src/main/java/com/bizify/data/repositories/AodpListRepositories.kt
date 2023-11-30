package com.bizify.data.repositories

import android.content.Context
import com.bizify.data.model.*

import com.bizify.data.network.MyApi
import com.bizify.data.network.SafeApiRequest
import com.bizify.database.AppDatabase
import okhttp3.RequestBody

/**
 * Created by Noushad N on 02-05-2022.
 */
class AodpListRepositories(
    val appContext: Context,
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest() {
    suspend fun getList(
        apiKey: String,
        count: String
    ): List<AodpList> {
        return apiRequest {
            api.postList(apiKey, count)
        }
    }

    fun savePost(aodpList: AodpList) {

        db?.getAodpDao()?.insert(aodpList)
    }

    fun getAllPosts() = db?.getAodpDao()?.getAllList()

    suspend fun login(
        request: RequestBody
    ): User {
        return apiRequest {
            api.login(request)
        }
    }

    suspend fun getCustomers(token: String): List<Customers> {
        return apiRequest { api.getCustomerList("Bearer $token") }
    }

    suspend fun getVehicles(token: String): List<Vehicles> {
        return apiRequest { api.getVehicleList("Bearer $token") }
    }

    suspend fun addJob(
        request: RequestBody,
        token: String
    ): CreateJobResponse {
        return apiRequest {
            api.addJob(request,"Bearer $token")
        }
    }

    suspend fun getJobOrders(token: String): List<CreateJobResponse> {
        return apiRequest { api.getJobOrders("Bearer $token") }
    }

    suspend fun addCustomer(
        request: RequestBody,
        token: String
    ): Customers {
        return apiRequest {
            api.addCustomer(request,"Bearer $token")
        }
    }

    suspend fun addVehicles(
        request: RequestBody,
        token: String
    ): Vehicles {
        return apiRequest {
            api.addVehicle(request,"Bearer $token")
        }
    }

    suspend fun getServices(token: String): List<Services> {
        return apiRequest { api.getServiceList("Bearer $token") }
    }

    suspend fun getMaterial(token: String): List<Services> {
        return apiRequest { api.getMaterialList("Bearer $token") }
    }

    suspend fun getVoucherServices(token: String,voucherNo : String): List<Services> {
        return apiRequest { api.getOrderServiceList("Bearer $token",voucherNo) }
    }

    suspend fun getVoucherMaterial(token: String, voucherNo : String): List<Services> {
        return apiRequest { api.getOrderMaterialList("Bearer $token",voucherNo) }
    }
}