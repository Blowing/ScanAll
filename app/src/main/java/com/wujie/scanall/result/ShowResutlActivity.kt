package com.wujie.scanall.result

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.wujie.scanall.R
import com.wujie.scanall.bean.QrMessage

import com.wujie.scanall.lib_http.HttpUtil
import com.wujie.scanall.lib_http.handler.HttpReponseHandler
import com.wujie.scanall.util.DataFormatUtil
import com.wujie.scanall.util.GsonUtil
import okhttp3.Call
import okhttp3.Response
import java.lang.ref.WeakReference
class ShowResutlActivity : AppCompatActivity() {


    lateinit var nameTv: TextView
    lateinit var weightTv: TextView
    lateinit var firmNameTv: TextView
    lateinit var firmAdrTv: TextView
    lateinit var mHandler : ShowHandler
    lateinit var productCode: String
    private val searchUrl = "http://search.anccnet.com/searchResult2.aspx?keyword"+""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_resutl)
        initView()
        initData()
    }

    private fun initView() {
        nameTv = findViewById(R.id.name_tv)
        weightTv = findViewById(R.id.weight_tv)
        firmNameTv = findViewById(R.id.firmName_tv)
        firmAdrTv = findViewById(R.id.fireAddress)
    }

    private fun initData() {
        productCode = intent.getStringExtra("code")
        mHandler = ShowHandler(this)
        getDataByJsoup()
    }
    inner class ShowHandler(activity: ShowResutlActivity): Handler() {
        private var weakActivity : WeakReference<ShowResutlActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message?) {
            if (weakActivity.get() == null) {
                return
            }

            when(msg?.what) {
                0x123 -> {
                    var qrMessage = GsonUtil.fromJson(msg.obj as String, QrMessage::class.java)
                    weakActivity.get()?.nameTv?.text = qrMessage?.d?.ItemName
                    weakActivity.get()?.weightTv?.text = qrMessage?.d?.ItemSpecification
                    weakActivity.get()?.firmNameTv?.text = qrMessage?.d?.FirmName
                    weakActivity.get()?.firmAdrTv?.text = qrMessage?.d?.FirmAddress
                }
            }
        }
    }

    private fun  getDataByJsoup() {
        Thread(Runnable {
            var s = DataFormatUtil.getMac("V7N3Xpm4jpRon/WsZ8X/63G8oMeGdUkA8Luxs1CenTY=",
                    "/api/getProductData?productCode=$productCode")
            Log.i("wuwu", s)

            HttpUtil.getAsync("http://webapi.chinatrace" +
            ".org/api/getProductData?productCode=$productCode&mac="+s, object:
                    HttpReponseHandler {
                override fun onFailure(request: Call, e: Exception) {
                }

                override fun onResponse(response: Response) {
                    val json = response.body()?.string()
                    val message = mHandler.obtainMessage()
                    message.what = 0x123
                    message.obj = json
                    Log.i("wuwu", json)
                    mHandler.sendMessage(message)
                }

            })

        }).start()

    }

    override fun onDestroy() {
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }


}
