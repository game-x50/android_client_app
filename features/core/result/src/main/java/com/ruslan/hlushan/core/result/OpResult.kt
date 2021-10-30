package com.ruslan.hlushan.core.result

import io.reactivex.Completable
import io.reactivex.Single

sealed class OpResult<out S : Any?, out E : Any?> {

    data class Success<out S : Any?>(val result: S) : OpResult<S, Nothing>()

    data class Error<out E : Any?>(val result: E) : OpResult<Nothing, E>()
}

typealias VoidOperationResult<E> = OpResult<Unit, E>

@SuppressWarnings("FunctionNaming")
fun OperationResultVoid(): OpResult.Success<Unit> = OpResult.Success(Unit)

inline fun <S : Any?> OpResult<S, Throwable>.getOrThrow(): S =
        when (this) {
            is OpResult.Success -> this.result
            is OpResult.Error   -> throw this.result
        }

inline fun <S : Any?, E : Any?, R : Any?> OpResult<S, E>.mapSuccess(block: (S) -> R): OpResult<R, E> =
        when (this) {
            is OpResult.Success -> OpResult.Success(result = block(this.result))
            is OpResult.Error   -> OpResult.Error(result = this.result)
        }

inline fun <S : Any?, E : Any?, R : Any?> OpResult<S, E>.mapError(block: (E) -> R): OpResult<S, R> =
        when (this) {
            is OpResult.Success -> OpResult.Success(result = this.result)
            is OpResult.Error   -> OpResult.Error(result = block(this.result))
        }

inline fun <S : Any, E : Any?> Single<out S>.toOperationResult(): Single<OpResult<S, E>> =
        this.map { result -> OpResult.Success(result) }

inline fun <S : Any?, E : Any?, R : Any?> Single<out OpResult<S, E>>.mapSuccess(
        crossinline block: (S) -> R
): Single<OpResult<R, E>> =
        this.map { value -> value.mapSuccess(block) }

inline fun <S : Any?, E : Any?, R : Any> Single<out OpResult<S, E>>.flatMapSuccess(
        crossinline block: (S) -> Single<R>
): Single<OpResult<R, E>> =
        this.flatMap { value ->
            when (value) {

                is OpResult.Success -> {
                    block(value.result)
                            .toOperationResult<R, E>()
                }

                is OpResult.Error   -> {
                    Single.just(OpResult.Error(result = value.result))
                }
            }
        }

inline fun <S : Any?, E : Any?, R : Any?> Single<out OpResult<S, E>>.flatMapNestedSuccess(
        crossinline block: (S) -> Single<out OpResult<R, E>>
): Single<OpResult<R, E>> =
        this.flatMap { value ->
            when (value) {

                is OpResult.Success -> {
                    block(value.result)
                }

                is OpResult.Error   -> {
                    Single.just(OpResult.Error(result = value.result))
                }
            }
        }

inline fun <S : Any?, E : Any?> Single<out OpResult<S, E>>.flatMapCompletableSuccess(
        crossinline block: (S) -> Completable
): Single<OpResult<S, E>> =
        this.flatMap { value ->
            when (value) {

                is OpResult.Success -> {
                    block(value.result)
                            .toSingleDefault(value)
                }

                is OpResult.Error   -> {
                    Single.just(OpResult.Error(result = value.result))
                }
            }
        }