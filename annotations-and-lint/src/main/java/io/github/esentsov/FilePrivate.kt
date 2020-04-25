package io.github.esentsov

import kotlin.annotation.AnnotationTarget.*

/**
 * Defines member visibility as a single file.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY,
    CLASS
)
@MustBeDocumented
annotation class FilePrivate