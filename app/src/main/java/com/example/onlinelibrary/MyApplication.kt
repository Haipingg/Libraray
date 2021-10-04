package com.example.onlinelibrary

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import kotlin.coroutines.coroutineContext

class MyApplication: Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}