package com.ruslan.hlushan.core.api.log

import com.ruslan.hlushan.core.api.dto.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

/**
 * @author Ruslan Hlushan on 10/18/18.
 */
interface FileLogger {

    var enabled: Boolean

    fun logToFile(message: String)

    fun deleteLogFiles(): Single<Boolean>

    fun readNextFileLogs(
            lastFileNameWithoutExtension: String?,
            limitFiles: Int
    ): Single<PaginationResponse<String, String>>

    fun copyAllExistingLogsToSingleFile(destination: File): Completable
}