package com.wujie.scanall.model.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wujie.scanall.R
import com.wujie.scanall.model.bean.BaikeResult
import com.wujie.scanall.view.GlideCircleTransform


/**
 * Created by wujie
 * on 2018/10/25/025.
 */
class PicturePageAdapter(val context: Context, val images: ArrayList<BaikeResult>) : PagerAdapter(){

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
        val convertView = LayoutInflater.from(context).inflate(R.layout.item_gallery, null)
        val imageView = convertView.findViewById<ImageView>(R.id.picture_iv)
        val baikeInfo = images[position]


        if (baikeInfo.baike_info != null) {
            Glide.with(context).load(baikeInfo.baike_info.image_url)
                    .apply(RequestOptions.bitmapTransform(GlideCircleTransform()).dontAnimate().placeholder(R
                            .mipmap.common_background_card))
                    .into(imageView)
        }

        container.addView(convertView)
        return convertView
    }
}