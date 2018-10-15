package com.wujie.scanall

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.zxing.client.android.CaptureActivity
import com.wujie.scanall.barcode.ScanActivity
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.result.ShowResutlActivity

class MainActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_scan).setOnClickListener(this)
        findViewById<Button>(R.id.btn_scan_all).setOnClickListener(this)
        findViewById<Button>(R.id.btn_scan_show).setOnClickListener(this)


    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_scan -> {
                startActivity(Intent(this, CaptureActivity::class.java))
            }
            R.id.btn_scan_all -> {
                startActivity(Intent(this, ScanActivity::class.java))
            }
            R.id.btn_scan_show -> {
                startActivity(Intent(this, ShowResutlActivity::class.java))
            }
        }
    }
}
