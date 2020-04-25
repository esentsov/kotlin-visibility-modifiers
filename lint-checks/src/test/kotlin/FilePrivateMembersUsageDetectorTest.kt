import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestResultChecker
import io.github.esentsov.kotlinvisibility.PrivateMembersUsageDetector
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertTrue
import org.junit.Test

class FilePrivateMembersUsageDetectorTest {

    @Test
    fun `call primary constructor`() {
        //language=kotlin
        doCheck(
            """
                    class Test @FilePrivate constructor()
                """
            ,
            """
                    fun test(){
                        Test()
                    }
                """
        )
    }

    @Test
    fun `call secondary  constructor`() {
        //language=kotlin
        doCheck(
            """
                    class Test(val test: Int){
                         @FilePrivate constructor():this(1)
                    }
                """
            ,
            """
                    fun test(){
                        Test()
                    }
                """
        )
    }

    @Test
    fun `call class function on constructed object`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         fun test(){}
                    }
                """
            ,
            """
                    fun test(){
                        Test().test()
                    }
                """
        )
    }

    @Test
    fun `call class function reference`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         fun test(){}
                    }
                """
            ,
            """
                    fun test(){
                        val instance = Test()
                        instance.test()
                    }
                """
        )
    }

    @Test
    fun `call class function apply`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         fun test(){}
                    }
                """
            ,
            """
                    fun test(){
                        Test().apply{
                            test()
                        }
                    }
                """
        )
    }

    @Test
    fun `get property on constructed object`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         val test: Int  = 1
                    }
                """
            ,
            """
                    fun test(){
                        val value = Test().test
                    }
                """
        )
    }

    @Test
    fun `set property on constructed object`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         var test: Int  = 1
                    }
                """
            ,
            """
                    fun test(){
                        Test().test = 2
                    }
                """
        )
    }

    @Test
    fun `get property on reference`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         val test: Int  = 1
                    }
                """
            ,
            """
                    fun test(){
                        val instance = Test()
                        val value = instance.test
                    }
                """
        )
    }

    @Test
    fun `set property on reference`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         var test: Int  = 1
                    }
                """
            ,
            """
                    fun test(){
                        val instance = Test()
                        instance.test = 2
                    }
                """
        )
    }

    @Test
    fun `get property in apply`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         var test: Int  = 1
                    }
                """
            ,
            """
                    fun test(){
                        Test().apply{
                            val value = test
                        }
                    }
                """
        )
    }

    @Test
    fun `set property in apply`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                         @FilePrivate
                         var test: Int  = 1
                    }
                """
            ,
            """
                    fun test(){
                        Test().apply{
                            test = 2
                        }
                    }
                """
        )
    }

    @Test
    fun `call companion object function`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                        companion object{
                            @FilePrivate
                            fun test() = Unit
                         }
                    }
                """
            ,
            """
                    fun test(){
                        Test.test()
                    }
                """
        )
    }

    @Test
    fun `set companion object property`() {
        //language=kotlin
        doCheck(
            """
                    class Test{
                        companion object{
                            @FilePrivate
                            var test: Int 
                                get() = 1
                                set(value){}
                         }
                    }
                """
            ,
            """
                    fun test(){
                        Test.test = 23
                    }
                """
        )
    }

    @Test
    fun `access class as a function parameter`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass
                """
            ,
            """
                    fun test(instance:PrivateClass){}
                """
        )
    }

    @Test
    fun `access class as a constructor parameter`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass
                """
            ,
            """
                    class Test(property: PrivateClass)
                """
        )
    }

    @Test
    fun `access class as a constructor property`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass
                """
            ,
            """
                    class Test(val property: PrivateClass)
                """
        )
    }

    @Test
    fun `access class constructor`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass
                """
            ,
            """
                    val test = PrivateClass()
                """,
            2 // class ref and constructor call are counted separately
        )
    }

    @Test
    fun `access class property`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass{
                        val p = 0
                    }
                """
            ,
            """
                    fun test(instance:PrivateClass){
                        instance.p
                    }
                """,
            2 // class ref and property access
        )
    }

    @Test
    fun `access class function`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass{
                        fun test(){}
                    }
                """
            ,
            """
                    fun test(instance:PrivateClass){
                        instance.test()
                    }
                """,
            2 // class ref and fun call
        )
    }

    @Test
    fun `access companion object`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass{
                        companion object{
                            const val property = 0
                        }
                    }
                """
            ,
            """
                    val test = PrivateClass.property
                """,
            2 // class ref and property access
        )
    }

    @Test
    fun `access inner class`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass{
                        class InnerClass()
                    }
                """
            ,
            """
                    val test = PrivateClass.InnerClass()
                """,
            3 // class ref, inner class ref, inner class constructor call
        )
    }

    @Test
    fun `access inner class property`() {
        //language=kotlin
        doCheck(
            """
                    @FilePrivate
                    class PrivateClass{
                        class InnerClass{
                            val property: Int = 0
                        }
                    }
                """
            ,
            """
                    val test = PrivateClass.InnerClass().property
                """,
            4 // class ref, inner class ref, inner class constructor, inner class property
        )
    }

    private fun doCheck(declaration: String, usage: String, errorCount: Int = 1) {
        lint()
            .files(
                kotlin(annotationDeclaration),
                kotlin(
                    declarationFileName,
                    """
                    $header
                    
                    $declaration 
                """
                ).within("src"),
                kotlin(
                    usageFileName,
                    """
                    $header
                    
                    $usage
                """
                ).within("src")
            )
            .allowDuplicates()
            .issues(PrivateMembersUsageDetector.FilePrivateIssue)
            .run()
            .expectErrorCount(errorCount)
            .check(TestResultChecker {
                assertTrue(it.contains("Usage of private api [FilePrivateId]"))
            })

        lint()
            .files(
                kotlin(annotationDeclaration),
                kotlin(
                    declarationFileName,
                    """
                    $header
                    
                    $declaration 
                    
                    $usage
                """
                )
            ).issues(PrivateMembersUsageDetector.FilePrivateIssue)
            .run()
            .expectClean()
    }
}

private const val declarationFileName = "Declaration.kt"
private const val usageFileName = "Usage.kt"

@Language("kotlin")
private const val header = """
    import io.github.esentsov.FilePrivate   
"""

@Language("kotlin")
private const val annotationDeclaration = """
    package io.github.esentsov
    
    import kotlin.annotation.AnnotationTarget.*

    @Retention(AnnotationRetention.SOURCE)
    @Target(
        CONSTRUCTOR,
        FUNCTION,
        PROPERTY,
        CLASS
        )
    annotation class FilePrivate
"""