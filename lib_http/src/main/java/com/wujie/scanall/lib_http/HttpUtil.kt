package com.wujie.scanall.lib_http

import com.wujie.scanall.lib_http.handler.HttpReponseHandler
import okhttp3.*
import java.io.IOException

/**
 * Created by wujie
 * on 2018/10/15/015.
 */
object HttpUtil {

    fun getAsync(url: String, callback: HttpReponseHandler) {

        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(url).build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onResponse(response)
            }

        })

    }

}