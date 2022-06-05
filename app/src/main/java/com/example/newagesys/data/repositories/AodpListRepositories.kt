package com.example.newagesys.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData

import com.example.newagesys.data.model.AodpList
import com.example.newagesys.data.network.MyApi
import com.example.newagesys.data.network.SafeApiRequest
import com.example.newagesys.database.AppDatabase

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

    fun savePost(aodpList: AodpList){

        db?.getAodpDao()?.insert(aodpList)
    }

    fun getAllPosts() = db?.getAodpDao()?.getAllList()

}