package com.bizify.interfaces


/*
 *Created by Adithya T Raj on 25-06-2021
*/

interface NetworkListener {

    fun onStarted()
    fun onSuccess()
    fun onFailure()
    fun onError()
    fun onNoNetwork()

}