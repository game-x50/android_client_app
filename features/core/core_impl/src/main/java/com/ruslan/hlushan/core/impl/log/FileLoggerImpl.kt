package com.ruslan.hlushan.core.impl.log

import android.content.SharedPreferences
import com.ruslan.hlushan.core.api.dto.PaginationResponse
import com.ruslan.hlushan.core.api.log.FileLogger
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolSpecification
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolType
import com.ruslan.hlushan.core.impl.di.annotations.SettingsPrefs
import com.ruslan.hlushan.core.impl.utils.files.deleteAllFilesInFolder
import com.ruslan.hlushan.core.impl.utils.files.readListFromFileReversedBySpliterator
import com.ruslan.hlushan.core.impl.utils.files.writeToFile
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.math.min

/**
 * @author Ruslan Hlushan on 10/18/18.
 */

private const val KEY_IS_FILE_LOGS_ENABLED = "KEY_IS_FILE_LOGS_ENABLED"

private const val MAX_SIZE_IN_BYTES = 1 * 1024
private const val FILES_EXTENSION = "txt"
private const val LOGS_DIVIDER = "-/$&"

private val NAME_WITHOUT_EXTENSION_REGEX: Regex = "[0-9]+".toRegex()

internal class FileLoggerImpl
@Inject
constructor(
        initAppConfig: InitAppConfig,
        @SettingsPrefs
        private val sharedPreferences: SharedPreferences
) : FileLogger {

    private val logsFolder: File = initAppConfig.fileLogsFolder

    private val executor = Executors.newSingleThreadExecutor { runnable ->
        createSingleLoggerThread(runnable)
    }

    private val fileLoggerScheduler = Schedulers.from(executor)

    private val atomicEnabled = AtomicBoolean(sharedPreferences.getIsFileLogsEnabled())

    private val lastFile = AtomicReference<File?>(null)

    override var enabled: Boolean
        get() = atomicEnabled.get()
        set(newValue) {
            atomicEnabled.set(newValue)
            sharedPreferences.saveIsFileLogsEnabled(newValue)
        }

    override fun logToFile(message: String) {
        if (enabled) {
            val runnable = createNewWriteRunnable(message)
            executor.execute(runnable)
        }
    }

    override fun readNextFileLogs(
            lastFileNameWithoutExtension: String?,
            limitFiles: Int
    ): Single<PaginationResponse<String, String>> =
            Single.fromCallable {
                readNextFileLogsSync(
                        lastFileNameWithoutExtension = lastFileNameWithoutExtension,
                        limitFiles = limitFiles
                )
            }.subscribeOn(fileLoggerScheduler)

    override fun copyAllExistingLogsToSingleFile(destination: File): Completable =
            Completable.fromAction { rewriteAllExistingLogsToSingleFileSync(destination) }
                    .subscribeOn(fileLoggerScheduler)

    override fun deleteLogFiles(): Single<Boolean> =
            Single.fromCallable {
                lastFile.set(null)
                deleteAllFilesInFolder(logsFolder)
            }
                    .subscribeOn(fileLoggerScheduler)

    private fun createNewWriteRunnable(message: String): Runnable = Runnable {
        try {
            val logMessage = "$message$LOGS_DIVIDER"
            val byteArraySize = logMessage.toByteArray().size
            val writeFile = getLastLogsFileForNewWrite(byteArraySize)
            writeToFile(writeFile, logMessage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @ThreadPoolSpecification(ThreadPoolType.IO)
    private fun readNextFileLogsSync(
            lastFileNameWithoutExtension: String?,
            limitFiles: Int
    ): PaginationResponse<String, String> {
        val sorterFilesList: List<File> = getLogsFiles(logsFolder, sorted = true)

        return if (sorterFilesList.isEmpty()) {
            PaginationResponse.LastPage(result = emptyList())
        } else {
            val positionOfLast: Int = if (lastFileNameWithoutExtension != null) {
                sorterFilesList.indexOfFirst { file -> file.nameWithoutExtension == lastFileNameWithoutExtension }
            } else {
                -1
            }

            val positionOfNext = (positionOfLast + 1)

            if (positionOfNext > sorterFilesList.lastIndex) {
                PaginationResponse.LastPage(result = emptyList())
            } else {
                val endPosition: Int = min((positionOfNext + limitFiles), sorterFilesList.lastIndex)
                val hasMore: Boolean = (endPosition != sorterFilesList.lastIndex)

                val resultFilesList: List<File> = sorterFilesList.subList(positionOfNext, endPosition + 1)

                val result = readLogsFromFiles(resultFilesList)

                if (hasMore) {
                    val nextPageId = resultFilesList.last().nameWithoutExtension
                    PaginationResponse.MiddlePage(result = result, nextId = nextPageId)
                } else {
                    PaginationResponse.LastPage(result = result)
                }
            }
        }
    }

    @ThreadPoolSpecification(ThreadPoolType.IO)
    private fun rewriteAllExistingLogsToSingleFileSync(destination: File) {
        val existingLogFiles = getLogsFiles(logsFolder, sorted = true)
        val result: String = readLogsFromFilesAsSingleString(filesList = existingLogFiles)
        writeToFile(destination, result)
    }

    @ThreadPoolSpecification(ThreadPoolType.IO)
    private fun getLastLogsFileForNewWrite(newBytesSize: Int): File {
        var lastFileTemp: File = (lastFile.get()
                                  ?: getLogsFiles(logsFolder, sorted = false).maxByOrNull { f -> f.name }
                                  ?: createFirstLogsFile())

        val lastFileTempSizeBytes = lastFileTemp.length()

        if ((lastFileTempSizeBytes > 0) &&
            ((lastFileTempSizeBytes + newBytesSize) > MAX_SIZE_IN_BYTES)) {

            val oldFileNameWithoutExtension = lastFileTemp.nameWithoutExtension
            val newFileNameWithoutExtension = oldFileNameWithoutExtension.toInt().inc().toString()
            lastFileTemp = createLogsFileForNameWithoutExtension(nameWithoutExtension = newFileNameWithoutExtension)
        }

        lastFile.set(lastFileTemp)

        return lastFileTemp
    }

    @ThreadPoolSpecification(ThreadPoolType.IO)
    private fun createFirstLogsFile(): File = createLogsFileForNameWithoutExtension(
            nameWithoutExtension = "0"
    )

    @ThreadPoolSpecification(ThreadPoolType.IO)
    private fun createLogsFileForNameWithoutExtension(
            nameWithoutExtension: String
    ): File = File(logsFolder, "$nameWithoutExtension.$FILES_EXTENSION")
}

@ThreadPoolSpecification(ThreadPoolType.IO)
private fun readLogsFromFilesAsSingleString(filesList: List<File>): String {

    val listOfLogs = readLogsFromFiles(filesList)

    val resultBuilder = StringBuilder()

    for (singleStringLog in listOfLogs) {
        resultBuilder.append(singleStringLog)
    }

    return resultBuilder.toString()
}

@ThreadPoolSpecification(ThreadPoolType.IO)
private fun readLogsFromFiles(filesList: List<File>): List<String> =
        filesList
                .flatMap { singleFile ->
                    try {
                        readListFromFileReversedBySpliterator(singleFile, LOGS_DIVIDER)
                    } catch (e: IOException) {
                        emptyList()
                    }
                }

private fun getLogsFiles(logsFolder: File, sorted: Boolean): List<File> {
    val files: List<File> = logsFolder.listFiles().orEmpty()
            .filter { f ->
                (f.isFile
                 && (f.extension == FILES_EXTENSION)
                 && f.nameWithoutExtension.matches(NAME_WITHOUT_EXTENSION_REGEX))
            }

    return if (sorted) {
        files.sortedByDescending { file -> file.name }
    } else {
        files
    }
}

private fun createSingleLoggerThread(runnable: Runnable) =
        Thread(runnable).apply {
            name = "FileLogger-Thread"
            priority = (Thread.NORM_PRIORITY + Thread.MIN_PRIORITY) / 2
            isDaemon = true
        }

private fun SharedPreferences.saveIsFileLogsEnabled(isEnabled: Boolean) =
        this.edit()
                .putBoolean(KEY_IS_FILE_LOGS_ENABLED, isEnabled)
                .apply()

private fun SharedPreferences.getIsFileLogsEnabled(): Boolean =
        this.getBoolean(KEY_IS_FILE_LOGS_ENABLED, false)