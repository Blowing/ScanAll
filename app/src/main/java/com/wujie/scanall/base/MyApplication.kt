package com.wujie.scanall.base

import android.app.Application
import cdc.sed.yff.AdManager
import com.tencent.bugly.Bugly
import com.tencent.stat.StatConfig
import com.tencent.stat.StatService

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
        // 腾讯bugly分析
        Bugly.init(this, "264912f934", false)
        // 腾讯MTA分析
        StatConfig.setDebugEnable(true)
        StatService.registerActivityLifecycleCallbacks(this)
        AdManager.getInstance(this).init("3f7eb61c9ebdf193", "f3d8aecc42bb92b3", true)

    }


}