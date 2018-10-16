package com.wujie.scanall.barcode

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

import com.wujie.scanall.R
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.zxing.CaptureActivityHandler
import com.wujie.scanall.zxing.ViewfinderView
import com.wujie.scanall.zxing.camera.CameraManager
import java.io.IOException

class ScanActivity : BaseActivity(), SurfaceHolder.Callback{

companion object {
     var TAG = ScanActivity.javaClass.simpleName
}
    lateinit var cameraManager: CameraManager

    private lateinit var handler: CaptureActivityHandler

    private lateinit var surfaceView : SurfaceView
    private lateinit var viewfinderView: ViewfinderView



    var hasSurface = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        initView()
    }

    private fun initView() {
        surfaceView = findViewById(R.id.scan_surface_view)
        viewfinderView = findViewById(R.id.viewfinder_view)
    }


    override fun onResume() {
        super.onResume()
        cameraManager = CameraManager(application)
        val surfaceHolder = surfaceView.holder
        if (hasSurface) {
            initCamera(surfaceHolder)

        } else {
            surfaceHolder.addCallback(this)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        if (surfaceHolder == null) {
            throw IllegalStateException("No SurfaceHolder provided")
        }
        if (cameraManager.isOpen) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return
        }

        try {
            cameraManager.openDriver(surfaceHolder)
            cameraManager.startPreview()
            viewfinderView.setCameraManager(cameraManager)
            // Creating the handler starts the preview, which can also throw a RuntimeException.
//            if (handler == null) {
//                handler = CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager)
//            }
//            decodeOrStoreSavedBitmap(null, null)
        } catch (ioe: IOException) {
            Log.w(TAG,ioe)
        } catch (e: RuntimeException) {
            Log.w(TAG, "Unexpected error initializing camera", e)
        }
    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        hasSurface = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!")
        }
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }



}
