package com.wujie.scanall.picture

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.google.android.cameraview.CameraView
import com.wujie.scanall.R
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.model.adapter.PicturePageAdapter
import com.wujie.scanall.model.bean.BaikeResult
import com.wujie.scanall.model.bean.PictureResult
import com.wujie.scanall.util.GsonUtil
import com.wujie.scanall.util.ImageClassifyUtil
import com.wujie.scanall.view.ZoomPageTransformer
import okio.Okio
import java.io.File
import java.lang.ref.WeakReference

class PictureScanActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "PictureScanActivity"
    //拍照的view
    private lateinit var mCameraView: CameraView
    private lateinit var mTakePicBtn: ImageView
    private lateinit var mToggleFaceIv: ImageView
    private lateinit var mTakePicLayout: RelativeLayout
    private lateinit var mPictureLayout: RelativeLayout

    //显示识别结果的view
    private lateinit var mAfterTakePicLayout: LinearLayout
    private lateinit var mPictureIv: ImageView
    private lateinit var mResultLayout: RelativeLayout
    private lateinit var mScanProgressBar: ProgressBar
    private lateinit var mResultViewPager: ViewPager
    private lateinit var mResultNameTv: TextView
    private lateinit var mBaikeResult: ArrayList<BaikeResult>

    private var facing: Int = 0

    private var newDis = 0
    private var oldDis = 0

    private var count = 60
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
                Log.i("wuwu", res.toString())
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
        initResultView()
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

        mCameraView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(event?.pointerCount == 2 ) {

                    when (event.action and MotionEvent.ACTION_MASK) {

                        MotionEvent.ACTION_POINTER_DOWN -> {
                            val x = event?.getX(0) - event?.getX(1)
                            val y = event.getY(0) - event.getY(1)
                            oldDis = Math.sqrt((x*x + y*y).toDouble()).toInt()
                            Log.i("wuwu", "new"+ oldDis + mCameraView.width)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val x = event?.getX(0) - event?.getX(1)
                            val y = event.getY(0) - event.getY(1)
                            newDis = Math.sqrt((x*x + y*y).toDouble()).toInt()
                            val zoom = (newDis - oldDis)
                            oldDis = newDis

                            mCameraView.setZoom(zoom < 0)
                        }
                    }


                }
                return true
            }

        })

        facing = mCameraView.facing
        mTakePicBtn = findViewById(R.id.take_picture_iv)
        mTakePicBtn.setOnClickListener(this)

        mToggleFaceIv = findViewById(R.id.picture_toggle_iv)
        mToggleFaceIv.setOnClickListener(this)

        mTakePicLayout = findViewById(R.id.take_picture_layout)
        mPictureLayout = findViewById(R.id.picture_layout)


    }

    private fun initResultView() {
        mAfterTakePicLayout = findViewById(R.id.take_picture_after_layout)
        mPictureIv = findViewById(R.id.picture_iv)
        mResultLayout = findViewById(R.id.result_layout)
        mScanProgressBar = findViewById(R.id.scan_progresBar)
        mResultNameTv = findViewById(R.id.result_tv_name)
        mResultViewPager = findViewById(R.id.result_viewpager)
        mResultViewPager.pageMargin = 20
        mResultViewPager.offscreenPageLimit = 3
        mResultViewPager.setPageTransformer(false, ZoomPageTransformer())


        mResultLayout.setOnTouchListener { _, event ->
                mResultViewPager.dispatchTouchEvent(event)

        }
        mResultViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                mBaikeResult?.let {
                    mResultNameTv.text = it[p0].keyword+"(它的可信度为${it[p0].score})"
                }

            }

        })

        showHandler = ShowHandler(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.take_picture_iv -> { // 拍照
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

            R.id.picture_toggle_iv -> { //切换前后摄像头

                var animator = ObjectAnimator.ofFloat(mToggleFaceIv, "rotationY", 0f, 180f)
                animator.duration = 500
                animator.start()
                facing = if (facing == CameraView.FACING_FRONT) {
                    CameraView.FACING_BACK
                } else {
                    CameraView.FACING_FRONT
                }
                mCameraView.facing = facing

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
                0x11 -> {
                    val resutl = msg.obj as PictureResult
                    mBaikeResult = resutl.result
                    mBaikeResult.removeAt(0)
                    activity?.mResultViewPager?.adapter = PicturePageAdapter(activity as Context,
                            resutl.result)
                    mResultNameTv.text = mBaikeResult[0].keyword+"(它的可信度为${mBaikeResult[0].score})"
                }

                0x12 -> {
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg")
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    activity?.mPictureIv?.setImageBitmap(bitmap)

                    val handler = Handler()
                    val runnable = object : Runnable {
                        override fun run() {
                            // TODO Auto-generated method stub
                            //要做的事情
                            mScanProgressBar.progress = --count
                            handler.postDelayed(this, 100)
                        }
                    }
                    handler.postDelayed(runnable, 100)
                }

            }

        }

    }
}

