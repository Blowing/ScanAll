package com.wujie.scanall.base

import android.app.Application

/**
 * Created by wujie
 * on 2018/10/18/018.
 */
class MyApplication : Application() {

    companion object {
        lateinit var instance : MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}