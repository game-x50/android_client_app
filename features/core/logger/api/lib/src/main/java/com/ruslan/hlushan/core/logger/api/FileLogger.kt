package com.ruslan.hlushan.core.logger.api

import com.ruslan.hlushan.core.pagination.api.PaginationPagesRequest
import com.ruslan.hlushan.core.pagination.api.PaginationResponse
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface FileLogger {

    var enabled: Boolean

    fun logToFile(message: String)

    fun deleteLogFiles(): Single<Boolean>

    fun readNextFileLogs(
            pagesRequest: PaginationPagesRequest<String>,
            limitFiles: Int
    ): Single<PaginationResponse<String, String>>

    fun copyAllExistingLogsToSingleFile(destination: File): Completable
}

object EmptyFileLoggerImpl : FileLogger {

    override var enabled: Boolean
        get() = false
        set(value) {}

    override fun logToFile(message: String) = Unit

    override fun deleteLogFiles(): Single<Boolean> =
            Single.just(false)

    override fun readNextFileLogs(
            pagesRequest: PaginationPagesRequest<String>,
            limitFiles: Int
    ): Single<PaginationResponse<String, String>> =
            Single.just(
                    PaginationResponse.SinglePage(
                            result = emptyList()
                    )
            )

    override fun copyAllExistingLogsToSingleFile(destination: File): Completable =
            Completable.complete()
}