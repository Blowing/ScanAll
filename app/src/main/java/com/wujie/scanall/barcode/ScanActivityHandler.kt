package com.wujie.scanall.barcode

import android.os.Handler
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.client.android.camera.CameraManager

/**
 * Created by wujie
 * on 2018/10/12/012.
 */
class ScanActivityHandler(activity: ScanActivity, decodeFormats: Collection<BarcodeFormat>,
                          baseHints: Map<DecodeHintType, *>, characterSet: String,
                          cameraManager: CameraManager) : Handler() {







}