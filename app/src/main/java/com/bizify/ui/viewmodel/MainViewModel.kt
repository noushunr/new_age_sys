package com.bizify.ui.viewmodel

import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bizify.data.model.*
import com.bizify.data.repositories.AodpListRepositories
import com.bizify.interfaces.NetworkListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject


/**
 * Created by Noushad N on 05-06-2022.
 */
class MainViewModel(private val repository: AodpListRepositories) : ViewModel() {
    val appContext by lazy {
        repository.appContext
    }
    var listener: NetworkListener? = null
    val email = ObservableField("")
    val password = ObservableField("")
    var error  = MutableLiveData<String>()
    var user  = MutableLiveData<User>()
    var aodpList = MutableLiveData<List<AodpList>>()
    var customerList = MutableLiveData<List<Customers>>()
    var vehiclesList = MutableLiveData<List<Vehicles>>()
    var createJobResponse = MutableLiveData<CreateJobResponse>()
    var addCustomerResponse = MutableLiveData<Customers>()
    var addVehicles = MutableLiveData<Vehicles>()
    var jobOrders = MutableLiveData<List<CreateJobResponse>>()
    var services  = MutableLiveData<List<Services>>()
    var materials  = MutableLiveData<List<Services>>()

    var selectedServices  = MutableLiveData<List<Services>>()
    var selectedMaterials  = MutableLiveData<List<Services>>()
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

    suspend fun getCustomerList(token: String){
        var customers = repository.getCustomers(token)
        customerList.postValue(customers)
    }

    suspend fun getVehicleList(token: String){
        var customers = repository.getVehicles(token)
        vehiclesList.postValue(customers)
    }

    suspend fun addJob(reqModel: RequestBody,token: String){
        var response = repository.addJob(reqModel,token)
        createJobResponse.postValue(response)
    }

    suspend fun addCustomer(reqModel: RequestBody,token: String){
        var response = repository.addCustomer(reqModel,token)
        addCustomerResponse.postValue(response)
    }

    suspend fun addVehicles(reqModel: RequestBody,token: String){
        var response = repository.addVehicles(reqModel,token)
        addVehicles.postValue(response)
    }
    fun getSavedPosts() = repository.getAllPosts()


    suspend fun login(userName : String,password: String){

        if (userName.isNullOrEmpty()){
            Toast.makeText(appContext,"Enter a username",Toast.LENGTH_LONG).show()
        }else if (password.isNullOrEmpty()){
            Toast.makeText(appContext,"Enter a password",Toast.LENGTH_LONG).show()
        } else{
            val doc = LoginModel(username = userName,
                password = password)

            val gson = Gson()
            val listString = gson.toJson(
                doc,
                object : TypeToken<LoginModel?>() {}.type)
            val jsonObject = JSONObject(listString)
            var JSON = MediaType.parse("application/json; charset=utf-8")

            var requestBody = RequestBody.create(JSON,listString)
            var user = repository.login(requestBody)
            this.user.postValue(user)
        }
    }

    suspend fun getJobOrders(token: String){
        var response = repository.getJobOrders(token)
        jobOrders.postValue(response)
    }

    suspend fun getServiceList(token: String){
        var response = repository.getServices(token)
        services.postValue(response)
    }
    suspend fun getMaterialList(token: String){
        var response = repository.getMaterial(token)
        materials.postValue(response)
    }

    suspend fun getOrderServiceList(token: String,voucherNo:String){
        var response = repository.getVoucherServices(token, voucherNo)
        selectedServices.value = response
    }
    suspend fun getOrderMaterialList(token: String,voucherNo:String){
        var response = repository.getVoucherMaterial(token, voucherNo)
        selectedMaterials.postValue(response)
    }
}