package io.github.esentsov

import kotlin.annotation.AnnotationTarget.*

/**
 * Defines member visibility as package.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY
)
@MustBeDocumented
annotation class PackagePrivate