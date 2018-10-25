package com.wujie.scanall.view

import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View

/**
 * Created by wujie
 * on 2018/10/24/024.
 */
class ZoomPageTransformer: ViewPager.PageTransformer {

    val MAX_SCALE = 1f
    val MIN_SCALE = 0.8f


    override fun transformPage(p0: View, p1: Float) {
        Log.i("wuwu", p0.toString() + p1)

        var p = p1
        if (p1 < -1) {
            p = -1f
        }
        if (p1 > 1f) {
            p = 1f
        }

        val scale = MIN_SCALE  + (1 - Math.abs(p)) * (MAX_SCALE - MIN_SCALE)
        p0.scaleX = scale
        p0.scaleY = scale

//        if (p1 <= 1) {
//
//            val scaleFactor = MIN_SCALE + (1 - Math.abs(p1)) * (MAX_SCALE - MIN_SCALE)
//
//            p0.scaleX = scaleFactor
//
//            if (p1 > 0) {
//                p0.translationX = -scaleFactor
//            } else if (p1 < 0) {
//                p0.translationX = scaleFactor
//            }
//            p0.scaleY = scaleFactor
//        } else {
//
//            p0.scaleX = MIN_SCALE
//            p0.scaleY = MIN_SCALE
//        }

    }
}