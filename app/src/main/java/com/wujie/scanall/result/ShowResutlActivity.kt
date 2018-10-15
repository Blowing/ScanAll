package com.wujie.scanall.result

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.wujie.scanall.R
import java.lang.ref.WeakReference
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

class ShowResutlActivity : AppCompatActivity() {

    lateinit var contentTv :TextView
    lateinit var mHandler : ShowHandler
    private val searchUrl = "http://search.anccnet.com/searchResult2.aspx?keyword"+""
    //private val searchUrl = "http://www.baidu.com"
//    private val searchUrl = "http://webapi.chinatrace" +
//            ".org/api/getProductData?productCode=6953479310108&mac=5A25C3D7095971712773850B803CACAC6F3AF5240D7D4E62AA6E1EFB35A9D702"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_resutl)
        initView()
        initData()
    }

    private fun initView() {
        contentTv = findViewById(R.id.content_tv)
    }

    private fun initData() {
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
                    weakActivity.get()?.contentTv?.text = msg.obj as String
                }
            }
        }
    }

    private fun  getDataByJsoup() {
        Thread(Runnable {

            val message = mHandler.obtainMessage()
            message.what = 0x123


            val s = getMac("V7N3Xpm4jpRon/WsZ8X/63G8oMeGdUkA8Luxs1CenTY=", "/api/getProductData?productCode=6953479310108")
            message.obj = s
            Log.i("wujie", s)
            mHandler.sendMessage(message)
        }).start()

    }

    override fun onDestroy() {
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun getMac(paramString1: String, paramString2: String): String {
        var p1 = paramString1
        var p2 = paramString2
        val arrayOfByte = ByteArray(32)
        arrayOfByte[0] = 87
        arrayOfByte[1] = -77
        arrayOfByte[2] = 119
        arrayOfByte[3] = 94
        arrayOfByte[4] = -103
        arrayOfByte[5] = -72
        arrayOfByte[6] = -114
        arrayOfByte[7] = -108
        arrayOfByte[8] = 104
        arrayOfByte[9] = -97
        arrayOfByte[10] = -11
        arrayOfByte[11] = -84
        arrayOfByte[12] = 103
        arrayOfByte[13] = -59
        arrayOfByte[14] = -1
        arrayOfByte[15] = -21
        arrayOfByte[16] = 113
        arrayOfByte[17] = -68
        arrayOfByte[18] = -96
        arrayOfByte[19] = -57
        arrayOfByte[20] = -122
        arrayOfByte[21] = 117
        arrayOfByte[22] = 73
        arrayOfByte[24] = -16
        arrayOfByte[25] = -69
        arrayOfByte[26] = -79
        arrayOfByte[27] = -77
        arrayOfByte[28] = 80
        arrayOfByte[29] = -98
        arrayOfByte[30] = -99
        arrayOfByte[31] = 54
        val str = ""
        p1 = str
        try {
            val localMac = Mac.getInstance("HmacSHA256")

            var p2Byte = paramString2.toByteArray(charset("ASCII"))

            localMac.init(SecretKeySpec(arrayOfByte, "HMACSHA256"))

            p2 = toHex(localMac.doFinal(paramString2.toByteArray()))
            p1 = p2
            p2 = p2.toUpperCase()
            return p2
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return p1
    }

    fun toHex(paramArrayOfByte: ByteArray): String {
        val localStringBuffer1 = StringBuffer(paramArrayOfByte.size * 2)
        val localStringBuffer2 = StringBuffer(paramArrayOfByte.size)
        var i = 0
        while (true) {
            if (i >= paramArrayOfByte.size) {
                return localStringBuffer1.toString()
            }
            localStringBuffer1.append(Character.forDigit((paramArrayOfByte[i] and 0xF0.toByte())
                    .toInt().shr(4), 16))
            localStringBuffer1.append(Character.forDigit((paramArrayOfByte[i] and 0xF).toInt(), 16))
            localStringBuffer2.append(paramArrayOfByte[i].toInt())
            i += 1
        }
    }
}