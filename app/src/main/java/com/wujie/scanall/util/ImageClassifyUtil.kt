package com.wujie.scanall.util

import com.baidu.aip.imageclassify.AipImageClassify
import org.json.JSONObject

/**
 * Created by wujie
 * on 2018/10/17/017.
 */
class ImageClassifyUtil private constructor() {

    private val appId = "14455212"
    private val apiKey = "HZT9Bs5Ts7FlbvIXgyrgDu1o"
    private val secretKey = "q9uaYjMYlSDFyfgMzaa3WGPML07Dw78G"


    lateinit var client: AipImageClassify

    companion object {
        val instance = ImageClassifyUtil()
        const val normal = "normal"
        const val plant = "plant"
        const val animal = "animal"
        const val dish = "dish"
        const val car = "car"
        const val logo = "logo"
    }

    init {
        client = AipImageClassify(appId, apiKey, secretKey)
        client.setConnectionTimeoutInMillis(2000)
        client.setSocketTimeoutInMillis(60000)
    }

    fun ImageClassify(filePath: String, type: String): JSONObject {
        val options = HashMap<String, String>()
        options["baike_num"] = "5"
        return when (type) {

            plant -> {
                client.plantDetect(filePath, options)
            }
            animal -> {
                client.animalDetect(filePath, options)
            }
            dish -> {
                client.dishDetect(filePath, options)
            }
            car -> {
                client.carDetect(filePath, options)
            }
            logo -> {
                client.logoDeleteByImage(filePath, options)
            }
            else -> {
                client.advancedGeneral(filePath, options)
            }
        }


        return client.advancedGeneral(filePath, options)
    }
}