package com.wujie.scanall.picture

import android.os.Bundle
import com.wujie.scanall.R
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.util.ImageClassifyUtil

class PictureScanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_scan)
        ImageClassifyUtil.get()
    }


}
