package com.wujie.scanall.util

import com.google.gson.Gson
import com.google.gson.JsonParseException
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * Created by wujie
 * on 2018/10/16/016.
 */
object GsonUtil {
    var gson = Gson()


    fun <T> toJson(t: T): String {
        return gson.toJson(t)

    }

    fun <T> fromJson(json: String?, clazz: Class<T>) : T? {
        if (json == null || "" == json) {
            return null
        }
        var t : T? = null
        try {
            t = gson.fromJson(json, clazz)
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        return t
    }

    fun <T> fromJson(json: String?, type: Type) : T? {
        if (json == null || "" == json) {
            return  null
        }
        var t: T? = null
        try {
            t = gson.fromJson(json, type)
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        return t
    }

    fun <T> fromJson(json: JSONObject, clazz: Class<T>): T? {
        return fromJson(json.toString(), clazz)
    }

    fun <T> fromJson(json: JSONObject, type: Type): T? {
        return fromJson(json.toString(), type)
    }

}