package com.ruslan.hlushan.core.impl.utils.files

import android.content.Context
import androidx.annotation.RawRes
import com.ruslan.hlushan.core.thread.ThreadPoolSpecification
import com.ruslan.hlushan.core.thread.ThreadPoolType
import java.io.BufferedReader
import java.io.InputStreamReader

@SuppressWarnings("TooGenericExceptionCaught")
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun readRawTextFile(ctx: Context, @RawRes resId: Int): String? {
    val inputStream = ctx.resources.openRawResource(resId)

    val inputreader = InputStreamReader(inputStream)
    val buffreader = BufferedReader(inputreader)
    var line: String?
    val text = StringBuilder()

    try {
        do {
            line = buffreader.readLine()
            if (line == null) {
                break
            }
            text.append(line)
            text.append('\n')
        } while (true)
    } catch (e: Exception) {
        return null
    } finally {
        try {
            buffreader.close()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        try {
            inputreader.close()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        @Suppress("TooGenericExceptionCaught")
        try {
            inputStream.close()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }
    return text.toString()
}