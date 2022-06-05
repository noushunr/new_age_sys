package com.example.newagesys.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newagesys.data.model.AodpList
import com.example.newagesys.data.repositories.AodpListRepositories
import java.lang.Exception

/**
 * Created by Noushad N on 05-06-2022.
 */
class AodpListViewModel( private val repository: AodpListRepositories) : ViewModel() {
    val appContext by lazy {
        repository.appContext
    }

    var error  = MutableLiveData<String>()
    var aodpList = MutableLiveData<List<AodpList>>()
    suspend fun getList(){
        try {
            val response = repository.getList(
                "DEMO_KEY", "10"
            )
            response?.forEach {
                repository.savePost(it)
            }

        }catch (e:Exception){

            error.postValue("Check your network connection")
        }
    }

    fun getSavedPosts() = repository.getAllPosts()


}