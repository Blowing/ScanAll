package com.wujie.scanall

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import cdc.sed.yff.nm.cm.ErrorCode
import cdc.sed.yff.nm.sp.SplashViewSettings
import cdc.sed.yff.nm.sp.SpotListener
import cdc.sed.yff.nm.sp.SpotManager
import cdc.sed.yff.nm.sp.SpotRequestListener
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.picture.PictureScanActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

class SplashActivity : BaseActivity(){

    lateinit var splashViewSettings: SplashViewSettings
    lateinit var splashLayout: RelativeLayout
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splashLayout = findViewById(R.id.splash_layout)

        splashViewSettings = SplashViewSettings()
        splashViewSettings.isAutoJumpToTargetWhenShowFailed = true
        splashViewSettings.targetClass = PictureScanActivity::class.java

        splashViewSettings.splashViewContainer = splashLayout

        SpotManager.getInstance(this).requestSpot(object : SpotRequestListener {
            override fun onRequestFailed(p0: Int) {
                Log.i("wuwu", "获取广告失败"+p0)
                ErrorCode.NON_AD
            }

            override fun onRequestSuccess() {
                Log.i("wuwu", "获取广告成功")

            }
        })

        AndPermission.with(this).runtime()
                .permission(arrayOf(Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE))
                .onDenied { finish() }
                .onGranted {
                    SpotManager.getInstance(this).showSplash(this, splashViewSettings,
                            object : SpotListener {
                                override fun onSpotClicked(p0: Boolean) {
                                }

                                override fun onShowSuccess() {
                                    Log.i("wuwu", "显示成功")
                                }

                                override fun onShowFailed(p0: Int) {
                                    Log.i("wuwu", "显示失败"+p0)
                                }

                                override fun onSpotClosed() {
                                }

                            })

                }
                .start()





    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()

        SpotManager.getInstance(this).onDestroy()

    }
}
