package com.ruslan.hlushan.core.logger.impl.utils

import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolSpecification
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolType
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

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
        fileWriter.use {  }
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

@Throws(IOException::class)
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun readListFromFileReversedBySpliterator(file: File, spliterator: String): List<String> {
    val builder = readFromFileStringBuffer(file)
    return builder.split(spliterator).asReversed()
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
        reader.use {  }
    }

    return builder
}

@Throws(IOException::class)
@ThreadPoolSpecification(ThreadPoolType.IO)
internal fun readFromFileString(file: File): String = readFromFileStringBuffer(file).toString()