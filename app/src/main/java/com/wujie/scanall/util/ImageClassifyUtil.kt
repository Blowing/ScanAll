package com.wujie.scanall.util

import com.baidu.aip.imageclassify.AipImageClassify
import org.json.JSONObject

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

    fun ImageClassify(filePath: String): JSONObject {
        val options =  HashMap<String, String>()
        options["baike_num"] = "5"
        return client.advancedGeneral(filePath,options)
    }
}