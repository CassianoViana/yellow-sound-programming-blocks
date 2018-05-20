package com.viana.soundprogramming.util

import com.viana.soundprogramming.sound.stream.EndianInputStream
import java.io.InputStream
import java.nio.ShortBuffer

fun readShorts(inputStream: InputStream): ShortArray {
    val endianInputStream = EndianInputStream(inputStream)
    val shortBuffer = ShortBuffer.allocate(inputStream.available())
    while (endianInputStream.available() > 0) {
        shortBuffer.put(endianInputStream.readLittleShort())
    }
    endianInputStream.close()
    return shortBuffer.array()
}