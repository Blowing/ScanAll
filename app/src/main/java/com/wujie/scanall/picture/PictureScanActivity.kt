package com.wujie.scanall.picture

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
    //拍照的view
    private lateinit var mCameraView: CameraView
    private lateinit var mTakePicBtn: ImageView
    private lateinit var mTakePicLayout: RelativeLayout
    private lateinit var mPictureLayout: RelativeLayout

    //显示识别结果的view
    private lateinit var mAfterTakePicLayout: LinearLayout
    private lateinit var mPictureIv: ImageView
    private lateinit var mResultLayout: RelativeLayout


    private val mCallback = object : CameraView.Callback() {
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
                message.what = 0x11
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

        mTakePicBtn = findViewById(R.id.take_picture_iv)
        mTakePicBtn.setOnClickListener(this)
        mTakePicLayout = findViewById(R.id.take_picture_layout)
        mPictureLayout = findViewById(R.id.picture_layout)

        mAfterTakePicLayout = findViewById(R.id.take_picture_after_layout)
        mPictureIv = findViewById(R.id.picture_iv)
        mResultLayout = findViewById(R.id.result_layout)
        showHandler = ShowHandler(this)


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.take_picture_iv -> {
                mCameraView.takePicture()
                mAfterTakePicLayout.visibility = View.VISIBLE
                mTakePicLayout.visibility = View.GONE
                val height = window.decorView.height - mTakePicLayout.height * 3
                val animator = ObjectAnimator.ofFloat(mResultLayout, "translationY", height.toFloat(), 0f)
                animator.duration = 2000
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        val message = showHandler.obtainMessage()
                        message.what = 0x12
                        showHandler.sendMessage(message)
                    }

                })
                animator.start()

            }

        }
    }


    private inner class ShowHandler(activity: PictureScanActivity) : Handler() {
        private var weakActivity: WeakReference<PictureScanActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message?) {
            if (weakActivity.get() == null) {
                return
            }

            val activity = weakActivity.get()
            when (msg?.what) {
                0x12 -> {
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg")
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    activity?.mPictureIv?.setImageBitmap(bitmap)
                }

            }

        }

    }
}

