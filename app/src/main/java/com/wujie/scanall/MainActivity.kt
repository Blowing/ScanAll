package com.wujie.scanall

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.wujie.scanall.base.BaseActivity
import com.wujie.scanall.picture.PictureScanActivity
import com.wujie.scanall.result.ShowResutlActivity
import com.wujie.scanall.zxing.CaptureActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

class MainActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_scan).setOnClickListener(this)
        findViewById<Button>(R.id.btn_scan_all).setOnClickListener(this)
        findViewById<Button>(R.id.btn_scan_show).setOnClickListener(this)
        findViewById<Button>(R.id.btn_image_classify).setOnClickListener(this)

        AndPermission.with(this).runtime()
                .permission(arrayOf(Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE))
                .onDenied { finish() }
                .start()

    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_scan -> {
                startActivityForResult(Intent(this, CaptureActivity::class.java), 1000)
//                startActivity(Intent(this, CaptureActivity::class.java))
            }

            R.id.btn_scan_show -> {
                startActivity(Intent(this, ShowResutlActivity::class.java))
            }
            R.id.btn_image_classify -> {
                startActivity(Intent(this, PictureScanActivity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000) {
            val intent =  Intent(this, ShowResutlActivity::class.java)
            intent.putExtra("code", data?.getStringExtra("result"))
            startActivity(intent)
        }
    }
}
