package com.dragnell.a2p_shop

import android.app.Application

class App : Application() {
    var storage: Storage = Storage()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getStorageInstance(): Storage {
        return storage
    }

    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }
}
