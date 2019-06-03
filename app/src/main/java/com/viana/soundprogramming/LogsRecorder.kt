package com.viana.soundprogramming

import android.os.Environment
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LogsRecorder : BlocksManager.Listener {
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("PT", "br"))
    override fun updateBlocksList(blocks: List<Block>) {
        val now = Date()
        val line = blocks.joinToString("\n") {
            "${sdf.format(now)}, ${it.javaClass.simpleName}, ${it.centerX}, ${it.centerY}, ${it.diameter}, ${it.degree}"
        }
        val externalStorageDirectory: File = Environment.getExternalStorageDirectory()
        val file = File(externalStorageDirectory.absolutePath + "/logs.txt")
        file.appendText(line + "\n")
    }

}
