package com.ruslan.hlushan.core.api.dto

import io.reactivex.Completable
import io.reactivex.Single

sealed class OperationResult<out S : Any?, out E : Any?> {

    data class Success<out S : Any?>(val result: S) : OperationResult<S, Nothing>()

    data class Error<out E : Any?>(val result: E) : OperationResult<Nothing, E>()
}

typealias VoidOperationResult<E> = OperationResult<Unit, E>

@SuppressWarnings("FunctionNaming")
fun OperationResultVoid(): OperationResult.Success<Unit> = OperationResult.Success(Unit)

inline fun <S : Any?> OperationResult<S, Throwable>.getOrThrow(): S =
        when (this) {
            is OperationResult.Success -> this.result
            is OperationResult.Error   -> throw this.result
        }

inline fun <S : Any?, E : Any?, R : Any?> OperationResult<S, E>.mapSuccess(block: (S) -> R): OperationResult<R, E> =
        when (this) {
            is OperationResult.Success -> OperationResult.Success(result = block(this.result))
            is OperationResult.Error   -> OperationResult.Error(result = this.result)
        }

inline fun <S : Any?, E : Any?, R : Any?> OperationResult<S, E>.mapError(block: (E) -> R): OperationResult<S, R> =
        when (this) {
            is OperationResult.Success -> OperationResult.Success(result = this.result)
            is OperationResult.Error   -> OperationResult.Error(result = block(this.result))
        }

inline fun <S : Any, E : Any?> Single<out S>.toOperationResult(): Single<OperationResult<S, E>> =
        this.map { result -> OperationResult.Success(result) }

inline fun <S : Any?, E : Any?, R : Any?> Single<out OperationResult<S, E>>.mapSuccess(
        crossinline block: (S) -> R
): Single<OperationResult<R, E>> =
        this.map { value -> value.mapSuccess(block) }

inline fun <S : Any?, E : Any?, R : Any> Single<out OperationResult<S, E>>.flatMapSuccess(
        crossinline block: (S) -> Single<R>
): Single<OperationResult<R, E>> =
        this.flatMap { value ->
            when (value) {

                is OperationResult.Success -> {
                    block(value.result)
                            .toOperationResult<R, E>()
                }

                is OperationResult.Error   -> {
                    Single.just(OperationResult.Error(result = value.result))
                }
            }
        }

inline fun <S : Any?, E : Any?, R : Any?> Single<out OperationResult<S, E>>.flatMapNestedSuccess(
        crossinline block: (S) -> Single<out OperationResult<R, E>>
): Single<OperationResult<R, E>> =
        this.flatMap { value ->
            when (value) {

                is OperationResult.Success -> {
                    block(value.result)
                }

                is OperationResult.Error   -> {
                    Single.just(OperationResult.Error(result = value.result))
                }
            }
        }

inline fun <S : Any?, E : Any?> Single<out OperationResult<S, E>>.flatMapCompletableSuccess(
        crossinline block: (S) -> Completable
): Single<OperationResult<S, E>> =
        this.flatMap { value ->
            when (value) {

                is OperationResult.Success -> {
                    block(value.result)
                            .toSingleDefault(value)
                }

                is OperationResult.Error   -> {
                    Single.just(OperationResult.Error(result = value.result))
                }
            }
        }