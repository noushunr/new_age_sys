package com.example.newagesys

import android.app.Application
import android.content.Context
import com.example.newagesys.data.network.MyApi
import com.example.newagesys.data.network.NetworkConnectionInterceptor
import com.example.newagesys.data.repositories.AodpListRepositories
import com.example.newagesys.database.AppDatabase
import com.example.newagesys.ui.viewmodel.ViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/**
 * Created by Noushad N on 05-06-2022.
 */
class MyApplication : Application(), KodeinAware {
    companion object {
        lateinit var instance: MyApplication
            private set
        val appContext: Context
            get() {
                return instance.applicationContext
            }
    }

    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()

    }
    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { AodpListRepositories(instance(), instance(), instance()) }
        bind() from provider { ViewModelFactory(instance()) }

    }

}