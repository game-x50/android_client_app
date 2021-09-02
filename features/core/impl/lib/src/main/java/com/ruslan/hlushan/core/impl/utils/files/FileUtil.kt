package com.ruslan.hlushan.core.impl.utils.files

import android.content.Context
import androidx.annotation.RawRes
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolSpecification
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolType
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by User on 02.02.2018.
 */

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

@Throws(IOException::class)
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun readFromFileStringBuffer(file: File): StringBuilder {
    var reader: BufferedReader? = null
    val builder = StringBuilder()
    @Suppress("TooGenericExceptionCaught")
    try {
        reader = BufferedReader(FileReader(file))

        while (true) {
            val line = reader.readLine() ?: break
            builder.append(line)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        close(reader)
    }

    return builder
}

@Throws(IOException::class)
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun readFromFileString(file: File): String = readFromFileStringBuffer(file).toString()

@Throws(IOException::class)
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun readListFromFileReversedBySpliterator(file: File, spliterator: String): List<String> {
    val builder = readFromFileStringBuffer(file)
    return builder.split(spliterator).asReversed()
}

@SuppressWarnings("TooGenericExceptionCaught")
internal fun close(closeable: Closeable?) {
    if (closeable != null) {
        try {
            closeable.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Throws(java.io.IOException::class)
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun writeToFile(file: File, text: String, append: Boolean = true) {

    if (!file.parentFile.exists() && !file.parentFile.mkdirs()) {
//            "Could not create parent directory"
        return
    }

    if (!file.exists() && !file.createNewFile()) {
//            "Could not create file"
        return
    }

    var fileWriter: FileWriter? = null
    try {
        fileWriter = FileWriter(file, append)
        fileWriter.write(text)
    } finally {
        close(fileWriter)
    }
}

@SuppressWarnings("TooGenericExceptionCaught")
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun deleteAllFilesInFolder(folder: File): Boolean {
    val filesInFolder = folder.listFiles()?.toList().orEmpty()
    val filesDeletedResults = filesInFolder
            .map { fileToDelete ->
                try {
                    fileToDelete.delete()
                } catch (e: Exception) {
                    false
                }
            }

    return filesDeletedResults.all { singleResult -> singleResult }
}