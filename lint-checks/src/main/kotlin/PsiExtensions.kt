package io.github.esentsov.kotlinvisibility

import org.jetbrains.kotlin.asJava.elements.KtLightDeclaration
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.uast.kotlin.KotlinUAnnotation
import org.jetbrains.uast.toUElement

fun KtLightDeclaration<*, *>.isAnnotatedWith(qualifiedName: String): Boolean {
    if (listOf(kotlinOrigin, kotlinOrigin?.containingKtFile).any { it?.isAnnotatedWith(qualifiedName) == true }) {
        return true
    }
    var node = kotlinOrigin?.containingClass()
    while (node != null) {
        if (node.isAnnotatedWith(qualifiedName)) return true
        node = node.containingClass()
    }
    return false
}

private fun KtAnnotated.isAnnotatedWith(qualifiedName: String) =
    annotationEntries.mapNotNull {
        it.toUElement() as? KotlinUAnnotation
    }.any {
        it.qualifiedName == qualifiedName
    }