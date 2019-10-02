package io.github.esentsov.kotlinvisibility

import org.jetbrains.kotlin.asJava.elements.KtLightDeclaration
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.uast.kotlin.KotlinUAnnotation
import org.jetbrains.uast.toUElement

fun KtLightDeclaration<*, *>.isAnnotatedWith(qualifiedName: String): Boolean = kotlinOrigin?.isAnnotatedWith(qualifiedName) == true

private fun KtAnnotated.isAnnotatedWith(qualifiedName: String) =
    annotationEntries.mapNotNull {
        it.toUElement() as? KotlinUAnnotation
    }.any {
        it.qualifiedName == qualifiedName
    }