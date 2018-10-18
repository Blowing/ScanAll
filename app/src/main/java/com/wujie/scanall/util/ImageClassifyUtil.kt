package com.wujie.scanall.util

import android.util.Log
import com.baidu.aip.imageclassify.AipImageClassify
import com.wujie.scanall.base.MyApplication

/**
 * Created by wujie
 * on 2018/10/17/017.
 */
class ImageClassifyUtil private constructor(){

    private val appId = "14455212"
    private val apiKey = "HZT9Bs5Ts7FlbvIXgyrgDu1o"
    private val secretKey = "q9uaYjMYlSDFyfgMzaa3WGPML07Dw78G"

    lateinit var client: AipImageClassify
    companion object {
        val instance = ImageClassifyUtil()
    }

    init {
        client =  AipImageClassify(appId, apiKey, secretKey)
        client.setConnectionTimeoutInMillis(2000)
        client.setSocketTimeoutInMillis(60000)
    }

    fun ImageClassify() {
        val options =  HashMap<String, String>()
        options["baike_num"] = "5"
        val input = MyApplication.instance.assets.open("dog.jpg")
        val length = input.available()
        val buffers = ByteArray(length)
        input.read(buffers)
        input.close()
        val res = client.advancedGeneral(buffers,options)
        Log.i("wuwu", res.toString())
    }
}