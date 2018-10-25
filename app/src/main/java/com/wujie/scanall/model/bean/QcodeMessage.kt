package com.wujie.scanall.model.bean

/**
 * Created by wujie
 * on 2018/10/16/016.
 */

data class QrMessage(val c: String, val d: QrData)

data class QrData(val ItemSpecification: String, val ItemName: String, val FirmName: String,
                val FirmAddress: String)