package com.viana.soundprogramming.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import com.viana.soundprogramming.REQUEST_CODE_CAMERA_PERMISSION
import com.viana.soundprogramming.REQUEST_CODE_RECORD_PERMISSION
import com.viana.soundprogramming.REQUEST_CODE_VIBRATE_PERMISSION
import com.viana.soundprogramming.REQUEST_CODE_WRITE_EXTERNAL_PERMISSION

fun managePermissionCamera(activity: Activity): Boolean {
    val cameraNotPermitted = ActivityCompat
            .checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    if (cameraNotPermitted) {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_CAMERA_PERMISSION)
        return true
    }
    return false
}

fun managePermissionDirectory(activity: Activity): Boolean {
    val recordNotPermitted = ActivityCompat
            .checkSelfPermission(activity.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    if (recordNotPermitted) {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_PERMISSION)
        return true
    }
    return false
}

fun managePermissionSound(activity: Activity): Boolean {
    val recordNotPermitted = ActivityCompat
            .checkSelfPermission(activity.applicationContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
    if (recordNotPermitted) {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_RECORD_PERMISSION)
        return true
    }
    return false
}

fun managePermissionVibrate(activity: Activity): Boolean {
    val recordNotPermitted = ActivityCompat
            .checkSelfPermission(activity.applicationContext, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED
    if (recordNotPermitted) {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.VIBRATE),
                REQUEST_CODE_VIBRATE_PERMISSION)
        return true
    }
    return false
}
