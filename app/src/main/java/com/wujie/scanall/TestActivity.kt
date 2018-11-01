package com.wujie.scanall

import android.content.Context
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Gallery
import android.widget.ImageView
import android.widget.RelativeLayout
import com.wujie.scanall.view.ZoomPageTransformer




class TestActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var gallery: Gallery
    var imageIds = arrayOf(R.mipmap.pc1, R.mipmap.pc2, R.mipmap.pc3, R.mipmap.pc4, R.mipmap.pc5)
    lateinit var mAdapter: ImageAdapter
    var lastPostion = -1


    lateinit var mViewPager: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        initViewPager()
        mViewPager.currentItem = 2

//        initView()
   }

//    private fun initView() {
//        gallery = findViewById(R.id.gallery)
//        mAdapter = ImageAdapter(this, imageIds)
//        gallery.adapter = mAdapter
//        var lastView: View? = null
//
//        gallery.setCallbackDuringFling(false)//停止时返回位置
//
//
//        gallery.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if (lastPostion != position) {
//                    view?.let {
//                        val animator1 = ObjectAnimator.ofFloat(it, "scaleX", 0.8f, 1f)
//                        val animator2 = ObjectAnimator.ofFloat(it, "scaleY", 0.8f, 1f)
//                        val animatorSet = AnimatorSet()
//                        animatorSet.play(animator1).with(animator2)
//                        animatorSet.start()
//                    }
//                    lastView?.let {
//                        val animator1 = ObjectAnimator.ofFloat(it, "scaleX", 1f, 0.8f)
//                        val animator2 = ObjectAnimator.ofFloat(it, "scaleY", 1f, 0.8f)
//                        val animatorSet = AnimatorSet()
//                        animatorSet.play(animator1).with(animator2)
//                        animatorSet.duration = 200
//                        animatorSet.start()
//                    }
//                    lastView = view
//                    lastPostion = position
//                }
//
//            }
//
//        }
//    }


    private fun initViewPager() {
        mViewPager = findViewById(R.id.viewpager)
//        val layoutParams = mViewPager.layoutParams
//        layoutParams.width = (mViewPager.context as Activity).windowManager.defaultDisplay.width / 21 * 10
        mViewPager.pageMargin = 20
        mViewPager.offscreenPageLimit = 3
        mViewPager.adapter = ViewPagerAdaper(this, imageIds)
        mViewPager.setPageTransformer(true, ZoomPageTransformer())
        findViewById<RelativeLayout>(R.id.viewPager_layout).setOnTouchListener { _, event -> mViewPager.dispatchTouchEvent(event) }
    }


    override fun onClick(v: View?) {

    }

    inner class ViewPagerAdaper(val mContext: Context, val images: Array<Int>) : PagerAdapter() {
        override fun isViewFromObject(p0: View, p1: Any): Boolean {
            return p0 == p1
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)

        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val convertView = layoutInflater.inflate(R.layout.item_gallery, null)
            val imageView = convertView.findViewById<ImageView>(R.id.picture_iv)
            imageView.setImageResource(imageIds[position])
            container.addView(convertView)
            return convertView
        }

//        override fun getPageWidth(position: Int): Float {
//            return 0.8f
//        }

    }

    inner class ImageAdapter(val mContext: Context, val images: Array<Int>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var imageView: ImageView
            if (convertView == null) {
                imageView = ImageView(mContext)
                imageView.layoutParams = Gallery.LayoutParams(600, 1022)
                imageView.scaleType = ImageView.ScaleType.FIT_XY
            } else {
                imageView = convertView as ImageView
            }
            imageView.setImageResource(imageIds[position])

            imageView.scaleX = 0.8f
            imageView.scaleY = 0.8f

            return imageView

        }

        override fun getItem(position: Int): Any {
            return imageIds[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return imageIds.size
        }

    }
}
