package com.wujie.scanall.picture

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import cdc.sed.yff.nm.sp.SpotListener
import cdc.sed.yff.nm.sp.SpotManager
import cdc.sed.yff.nm.sp.SpotRequestListener
import com.google.android.cameraview.CameraView
import com.mingle.widget.LoadingView
import com.wujie.scanall.R
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.model.adapter.PicturePageAdapter
import com.wujie.scanall.model.bean.BaikeResult
import com.wujie.scanall.model.bean.PictureResult
import com.wujie.scanall.util.GsonUtil
import com.wujie.scanall.util.ImageClassifyUtil
import com.wujie.scanall.util.UriToPathUtil
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
    private lateinit var mFlashView: ImageView
    private lateinit var mTypeTab: TabLayout

    //显示识别结果的view
    private lateinit var mAfterTakePicLayout: LinearLayout
    private lateinit var mPictureIv: ImageView
    private lateinit var mSwitchCameraIv: ImageView
    private lateinit var mResultLayout: RelativeLayout
    private lateinit var mScanProgressBar: ProgressBar
    private lateinit var mResultViewPager: ViewPager
    private lateinit var mResultNameTv: TextView
    private lateinit var mDesTv: TextView
    private lateinit var mResultLoadingView: LoadingView
    private lateinit var mBaikeResult: ArrayList<BaikeResult>

    private var facing: Int = 0

    private var newDis = 0
    private var oldDis = 0

    private var count = 80

    private var isFlash = false // 默认关闭闪光
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
                val res = ImageClassifyUtil.instance.ImageClassify(file.path,type)
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

    private var type = ImageClassifyUtil.normal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_scan)
        SpotManager.getInstance(this).requestSpot(object :SpotRequestListener {
            override fun onRequestFailed(p0: Int) {
                Log.i("wuwu", "获取插屏广告失败")
            }

            override fun onRequestSuccess() {
                Log.i("wuwu", "获取插屏广告成功")
            }

        })
        SpotManager.getInstance(this).setImageType(SpotManager.IMAGE_TYPE_VERTICAL)
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
        SpotManager.getInstance(this).onPause()
    }

    override fun onStop() {
        super.onStop()
        SpotManager.getInstance(this).onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        SpotManager.getInstance(this).onDestroy()
        SpotManager.getInstance(this).onAppExit()
    }

    @SuppressLint("ClickableViewAccessibility")
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

        mFlashView = findViewById(R.id.camera_iv_flash)
        mFlashView.setOnClickListener(this)

        findViewById<ImageView>(R.id.album_iv).setOnClickListener(this)

        mTakePicLayout = findViewById(R.id.take_picture_layout)
        mPictureLayout = findViewById(R.id.picture_layout)

        mTypeTab = findViewById(R.id.type_table_layout)
        mTypeTab.addTab(mTypeTab.newTab().setText("通用").setTag(ImageClassifyUtil.normal))
        mTypeTab.addTab(mTypeTab.newTab().setText("植物").setTag(ImageClassifyUtil.plant))
        mTypeTab.addTab(mTypeTab.newTab().setText("动物").setTag(ImageClassifyUtil.animal))
        mTypeTab.addTab(mTypeTab.newTab().setText("菜品").setTag(ImageClassifyUtil.dish))
        mTypeTab.addTab(mTypeTab.newTab().setText("汽车").setTag(ImageClassifyUtil.car))
        //mTypeTab.addTab(mTypeTab.newTab().setText("标志").setTag(ImageClassifyUtil.logo))
        mTypeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                type = p0?.tag as String
            }

        })

    }

    private fun initResultView() {
        mAfterTakePicLayout = findViewById(R.id.take_picture_after_layout)
        mPictureIv = findViewById(R.id.picture_iv)
        mSwitchCameraIv = findViewById(R.id.picture_iv_camera)
        mSwitchCameraIv.setOnClickListener(this)
        mResultLayout = findViewById(R.id.result_layout)
        mScanProgressBar = findViewById(R.id.scan_progresBar)
        mResultNameTv = findViewById(R.id.result_tv_name)
        mDesTv = findViewById(R.id.result_tv_des)
        mDesTv.movementMethod = ScrollingMovementMethod.getInstance()
        mResultLoadingView = findViewById(R.id.result_loading_view)


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
                    mResultNameTv.text = (it[p0].name+it[p0].keyword+"(它的可信度为${it[p0].score})")
                            .replace("null", "")
                    mDesTv.text= if(it[p0].baike_info == null) {
                        ""
                    } else {
                        it[p0].baike_info.description
                    }
                    mDesTv.scrollTo(0, 0)
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

            R.id.album_iv -> {
                val intent = Intent()
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }

            R.id.camera_iv_flash -> {
                isFlash = !isFlash
                if (isFlash) {
                    mCameraView.flash = CameraView.FLASH_AUTO
                    mFlashView.setImageResource(R.mipmap.flash)
                } else {
                    mCameraView.flash = CameraView.FLASH_OFF
                    mFlashView.setImageResource(R.mipmap.flash_off)
                }
            }

            R.id.picture_iv_camera -> {
                mCameraView.stop()
                mAfterTakePicLayout.visibility = View.GONE
                mTakePicLayout.visibility = View.VISIBLE
                mSwitchCameraIv.visibility = View.GONE
                mPictureIv.visibility = View.GONE
                mResultNameTv.text = ""
                mDesTv.text =""
                mPictureIv.setImageResource(R.mipmap.common_background_card)
                mResultViewPager.removeAllViews()
                mCameraView.addCallback(mCallback)
                mCameraView.start()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            //val file = File(URI(uri?.scheme, uri?.host, uri?.path, null))
            Log.i("wuwu", uri.toString() )
            uri?.let {
                val path = UriToPathUtil.getPathByUri(applicationContext, it)
                Log.i("wuwu", path)
                path?.let {
                    Thread {
                        val res = ImageClassifyUtil.instance.ImageClassify(it, type)
                        Log.i("wuwu", res.toString())
                        val pictureResult = GsonUtil.fromJson(res, PictureResult::class.java)
                        val message = showHandler.obtainMessage()
                        message.what = 0x11
                        message.obj = pictureResult
                        showHandler.sendMessage(message)
                    }.start()
                }


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
                        message.what = 0x13
                        message.obj = path
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
                0x11 -> {
                    mResultLoadingView.visibility = View.GONE
                    count = 0
                    mScanProgressBar.progress = count
                    val resutl = msg.obj as PictureResult
                    mBaikeResult = resutl.result
                    if (mBaikeResult != null) {
                        mBaikeResult.removeAt(0)
                        activity?.mResultViewPager?.adapter = PicturePageAdapter(activity as Context,
                                mBaikeResult)
                        if(mBaikeResult.size > 0) {
                            mResultNameTv.text = mBaikeResult[0].name+mBaikeResult[0].keyword+"" +
                                    "(它的可信度为${mBaikeResult[0].score})".replace("null", "")
                            mDesTv.text = mBaikeResult[0].baike_info.description
                        } else {
                            Toast.makeText(activity, "对不起，没有识别结果",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(activity, "对不起，没有识别结果",Toast.LENGTH_SHORT).show()
                    }
                }

                0x12 -> {
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg")
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    activity?.mPictureIv?.visibility = View.VISIBLE
                    activity?.mSwitchCameraIv?.visibility = View.VISIBLE
                    activity?.mPictureIv?.setImageBitmap(bitmap)
                    activity?.mResultLoadingView?.visibility = View.VISIBLE
                    val handler = Handler()
                    val runnable = object : Runnable {
                        override fun run() {
                            // TODO Auto-generated method stub
                            //要做的事情
                            if (--count >= 0) {
                                mScanProgressBar.progress = count
                                handler.postDelayed(this, 100)
                            }

                        }
                    }
                    handler.postDelayed(runnable, 100)

                    SpotManager.getInstance(this@PictureScanActivity).showSpot(this@PictureScanActivity,
                            object : SpotListener {
                                override fun onSpotClicked(p0: Boolean) {
                                }

                                override fun onShowSuccess() {

                                }

                                override fun onShowFailed(p0: Int) {
                                    Log.i("wuwu", "插屏显示失败")
                                }

                                override fun onSpotClosed() {
                                }

                            })
                }
                0x13 -> {
                    val path = msg.obj as String
                    val file = File(path)
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    activity?.mPictureIv?.visibility = View.VISIBLE
                    activity?.mSwitchCameraIv?.visibility = View.VISIBLE
                    activity?.mPictureIv?.setImageBitmap(bitmap)

                    val handler = Handler()
                    val runnable = object : Runnable {
                        override fun run() {

                            if (--count >= 0) {
                                mScanProgressBar.progress = count
                                handler.postDelayed(this, 100)
                            }
                        }
                    }
                    handler.postDelayed(runnable, 100)
                }

            }

        }

    }
}

