package com.wujie.scanall.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore

/**
 * Created by wujie
 * on 2018/10/31/031.
 *
 * 通过相册的Uri 获取图片的绝对路径
 */
object UriToPathUtil {
    private val isAboveKit =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT


    fun getPathByUri(context: Context, uri: Uri) : String?{

        return if (isAboveKit) {
            getPathByUriA19(context, uri)
        } else {
            getPathByUriB19(context, uri)
        }

    }

    /**
     * api 19及其以上的获取方法
     */

    @SuppressLint("NewApi")
    private fun getPathByUriA19(context: Context, uri: Uri) : String?{
        var path:String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            MediaStore.Images.Media.DATA
            val documentId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                val id  = documentId.split(":")[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)
                path = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        documentId.toLong())
                path = getDataColumn(context, contentUri, null, null)
            }
        } else if ("content" == uri.scheme) {
            path = getDataColumn(context, uri, null, null)
        } else if ("file" == uri.scheme) {
            path = uri.path
        }

        return path

    }

    /**
     * api 19以下的获取方法
     * 通过Cursor 查找
     */
    private fun getPathByUriB19(context: Context, uri: Uri) : String?{

        return getDataColumn(context, uri, null , null)
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?,
                              selectionArgs: Array<String>?): String? {
        var path: String? = null
        val projetion = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projetion, selection, selectionArgs, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projetion[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return path
    }

}