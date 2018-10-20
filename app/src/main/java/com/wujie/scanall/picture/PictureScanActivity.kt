package com.wujie.scanall.picture

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.cameraview.CameraView
import com.wujie.scanall.R
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.bean.PictureResult
import com.wujie.scanall.util.GsonUtil
import com.wujie.scanall.util.ImageClassifyUtil
import okio.Okio
import java.io.File
import java.lang.ref.WeakReference

class PictureScanActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "PictureScanActivity"

    private lateinit var pictureLayout: LinearLayout
    private lateinit var pictureIv: ImageView
    private lateinit var nameTv: TextView
    private lateinit var descriTv: TextView

    private lateinit var mCameraView : CameraView

    private val mCallback = object : CameraView.Callback(){
        override fun onCameraOpened(cameraView: CameraView?) {
            Log.i(TAG, "onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView?) {
            Log.i(TAG, "onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
            Log.i(TAG, "onPictureTaken")
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg")
            val sink = Okio.buffer(Okio.sink(file))
            sink.write(data!!)
            sink.close()
            Thread {
                val res = ImageClassifyUtil.instance.ImageClassify(file.path)
                val pictureResult = GsonUtil.fromJson(res, PictureResult::class.java)
                val message = showHandler.obtainMessage()
                message.obj = pictureResult
                showHandler.sendMessage(message)
            }.start()
        }
    }

    private lateinit var showHandler: ShowHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_scan)
        initView()
        showHandler = ShowHandler(this)
    }

    override fun onResume() {
        mCameraView.start()
        super.onResume()
    }

    override fun onPause() {
        mCameraView.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initView() {
        mCameraView = findViewById(R.id.camera)
        mCameraView.addCallback(mCallback)

        val fab = findViewById<FloatingActionButton>(R.id.take_picture)
        fab.setOnClickListener(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

        pictureLayout = findViewById(R.id.picture_layout)
        pictureIv = findViewById(R.id.picture_iv)
        nameTv = findViewById(R.id.keyword_tv)
        descriTv = findViewById(R.id.description_tv)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.take_picture -> {
                mCameraView.takePicture()
            }
        }
    }


    private inner class ShowHandler(activity: PictureScanActivity): Handler() {
        private var weakActivity : WeakReference<PictureScanActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message?) {
            if (weakActivity.get() == null) {
                return
            }

            val picResult = msg?.obj as PictureResult
            val activity = weakActivity.get()
            activity?.pictureLayout?.visibility = View.VISIBLE
            activity?.mCameraView?.visibility = View.GONE
            activity?.nameTv?.text = picResult.result[0].keyword
            activity?.descriTv?.text = picResult.result[0].baike_info.description
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg")
            val bitmap = BitmapFactory.decodeFile(file.path)
            activity?.pictureIv?.setImageBitmap(bitmap)

        }



        }
}
