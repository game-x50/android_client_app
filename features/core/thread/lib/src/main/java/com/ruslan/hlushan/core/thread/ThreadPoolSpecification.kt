package com.ruslan.hlushan.core.thread

@Retention(AnnotationRetention.SOURCE)
@Target(
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.TYPEALIAS
)
annotation class ThreadPoolSpecification(val value: ThreadPoolType)

enum class ThreadPoolType {
    IO, COMPUTATION
}

/**
 * Class should be used just is single thread,
 * and avoid sharing int between another threads
 * */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class SingleThreadSafety

@Retention(AnnotationRetention.SOURCE)
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.PROPERTY, AnnotationTarget.FIELD,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.TYPEALIAS
)
annotation class UiMainThread