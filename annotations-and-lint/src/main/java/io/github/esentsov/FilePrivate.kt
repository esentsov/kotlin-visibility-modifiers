package io.github.esentsov

import kotlin.annotation.AnnotationTarget.*

@Retention(AnnotationRetention.SOURCE)
@Target(
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY
)
annotation class FilePrivate