package io.github.esentsov.kotlinvisibility

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.kotlin.asJava.elements.KtLightDeclaration
import org.jetbrains.uast.*

private const val filePrivateAnnotationName = "io.github.esentsov.FilePrivate"
private const val packagePrivateAnnotationName = "io.github.esentsov.PackagePrivate"

/**
 * Reports usages of members marked with [filePrivateAnnotationName] annotation or [packagePrivateAnnotationName] outside of the respective scopes.
 */
class PrivateMembersUsageDetector : Detector(), UastScanner {

    /**
     * Currently we check the raw expressions, they cover all members usage.
     * The [UastScanner] api seems to not cover references to properties, declared in primary constructors.
     * It also resolves property assignment expressions into getter call, checking for getter annotations
     */
    override fun getApplicableUastTypes(): List<Class<out UElement>>? =
        listOf(UExpression::class.java, UParameter::class.java, UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? =
        object : UElementHandler() {

            override fun visitAnnotation(node: UAnnotation) {
                check(node, node.resolve())
            }

            override fun visitParameter(node: UParameter) {
                check(node, (node.typeReference?.type as? PsiClassReferenceType)?.resolve())
            }

            override fun visitExpression(node: UExpression) {
                if (node is UQualifiedReferenceExpression) {
                    // Avoid duplication by handling lower level part of the reference
                    // e.g. instance.value will be handled as just value
                    return
                }
                check(node, node.tryResolve())
            }

            private fun check(node: UElement, resolved: PsiElement?) {
                (resolved as? KtLightDeclaration<*, *>)?.let {
                    if (resolved.isAnnotatedWith(filePrivateAnnotationName) && areInDifferentFiles(node, resolved)) {
                        context.report(FilePrivateIssue, node, context.getLocation(node), "Usage of private api")
                    }
                    if (resolved.isAnnotatedWith(packagePrivateAnnotationName) && areInDifferentPackages(context, node, resolved)) {
                        context.report(PackagePrivateIssue, node, context.getLocation(node), "Usage of private api")
                    }
                }
            }
        }

    private fun areInDifferentFiles(node: UElement, resolved: PsiElement): Boolean {
        val declarationFile = resolved.containingFile.virtualFile.path
        val referenceFile = node.getContainingUFile()?.getIoFile()?.absolutePath
        return referenceFile != null && declarationFile != referenceFile
    }

    private fun areInDifferentPackages(context: JavaContext, node: UElement, resolved: KtLightDeclaration<*, *>): Boolean {
        val declarationPackage = context.evaluator.getPackage(resolved)
        val referencePackage = context.evaluator.getPackage(node)
        return declarationPackage?.qualifiedName != referencePackage?.qualifiedName
    }

    companion object {
        val PackagePrivateIssue = Issue.create(
            "PackagePrivateId",
            "Kotlin package visibility",
            "This check highlights usage of members marked private in package they are declared in",
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(PrivateMembersUsageDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
        val FilePrivateIssue = Issue.create(
            "FilePrivateId",
            "Kotlin file visibility",
            "This check highlights usage of members marked private for files they are declared in",
            Category.CORRECTNESS,
            10,
            Severity.ERROR,
            Implementation(PrivateMembersUsageDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}

