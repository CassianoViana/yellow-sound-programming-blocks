package com.viana.soundprogramming

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LogsRecorder : BlocksManager.Listener {
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("PT", "br"))
    override fun updateBlocksList(blocks: List<Block>) {
        val filesPermissionGranted = ActivityCompat
                .checkSelfPermission(appInstance.applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        if (filesPermissionGranted) {
            val now = Date()
            val line = blocks.joinToString("\n") {
                "${sdf.format(now)}, ${it.javaClass.simpleName}, ${it.centerX}, ${it.centerY}, ${it.diameter}, ${it.degree}"
            }
            val externalStorageDirectory: File = Environment.getExternalStorageDirectory()
            val file = File(externalStorageDirectory.absolutePath + "/logs.txt"); file.appendText(line + "\n")
        }
    }

}
