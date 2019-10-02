import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestResultChecker
import io.github.esentsov.kotlinvisibility.PrivateMembersUsageDetector
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertTrue
import org.junit.Test

class PackagePrivateMembersUsageDetectorTest {
    @Test
    fun `get top level property`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    val variable = 1
                """
            ,
            """
                    fun test(){
                        val local = variable
                    }
                """
        )
    }

    @Test
    fun `get top level property no field`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    val variable get() = 1
                """
            ,
            """
                    fun test(){
                        val local = variable
                    }
                """
        )
    }

    @Test
    fun `set top level property`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    var variable = 1
                """
            ,
            """
                    fun test(){
                        variable = 2
                    }
                """
        )
    }

    @Test
    fun `call top level function`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    fun function(){}
                """
            ,
            """
                    fun test(){
                        function()
                    }
                """
        )
    }

    @Test
    fun `call primary constructor`() {
        //language=kotlin
        doCheck(
            """
                    class Test @PackagePrivate constructor()
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
                         @PackagePrivate constructor():this(1)
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                         @PackagePrivate
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
                            @PackagePrivate
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
                            @PackagePrivate
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
    fun `call extension function`() {
        //language=kotlin
        doCheck(
            """
                    class Test
                    
                    @PackagePrivate
                    fun Test.test(){}
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
    fun `get extension property`() {
        //language=kotlin
        doCheck(
            """
                    class Test
                    
                    @PackagePrivate
                    val Test.test: Int get() = 1
                """
            ,
            """
                    fun test(){
                        val a = Test().test
                    }
                """
        )
    }


    private fun doCheck(declaration: String, usage: String) {
        lint()
            .files(
                kotlin(annotationDeclaration),
                kotlin(
                    """
                    $anotherPackageDefinitionsHeader
                    $declaration
                """
                ),
                kotlin(
                    """
                    $usageHeader
                    $usage
                """
                )
            )
            .issues(PrivateMembersUsageDetector.PackagePrivateIssue)
            .run()
            .expectErrorCount(1)
            .check(TestResultChecker {
                assertTrue(it.contains("Usage of private api [PackagePrivateId]"))
            })

        lint()
            .files(
                kotlin(annotationDeclaration),
                kotlin(
                    """
                    $samePackageDefinitionsHeader
                    $declaration
                """
                ),
                kotlin(
                    """
                    $usageHeader
                    $usage
                """
                )
            ).issues(PrivateMembersUsageDetector.PackagePrivateIssue)
            .run()
            .expectClean()
    }
}

@Language("kotlin")
private const val anotherPackageDefinitionsHeader = """
    package com.test.definitions
    import io.github.esentsov.PackagePrivate   
"""

@Language("kotlin")
private const val samePackageDefinitionsHeader = """
    package com.test.usage
    import io.github.esentsov.PackagePrivate   
"""

@Language("kotlin")
private const val usageHeader = """
    package com.test.usage
    import com.test.definitions.* 
"""

@Language("kotlin")
private const val annotationDeclaration = """
    package io.github.esentsov
    
    import kotlin.annotation.AnnotationTarget.*

    @Retention(AnnotationRetention.SOURCE)
    @Target(
        CONSTRUCTOR,
        FUNCTION,
        PROPERTY
    )
    annotation class PackagePrivate
"""