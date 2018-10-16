package com.wujie.scanall.lib_http.handler

import okhttp3.Call
import okhttp3.Response

/**
 * Created by wujie
 * on 2018/10/15/015.
 * 回调的接口
 */
interface HttpReponseHandler {
    fun onFailure(request: Call, e: Exception)
    fun onResponse(response: Response)
}