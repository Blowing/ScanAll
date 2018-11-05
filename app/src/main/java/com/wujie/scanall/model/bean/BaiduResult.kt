package com.wujie.scanall.model.bean

/**
 * Created by wujie
 * on 2018/10/20/020.
 */

data class BaikeInfo(val baike_url: String, val image_url: String, val description: String)

data class BaikeResult(val score: Double, val root: String, val name: String,
                       val baike_info: BaikeInfo, val keyword: String)
data class PictureResult(val log_id: String, val result_num: Int, val result: ArrayList<BaikeResult>)