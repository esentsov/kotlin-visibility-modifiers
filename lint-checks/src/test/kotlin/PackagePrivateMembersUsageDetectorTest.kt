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

    @Test
    fun `access class as a function parameter`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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
                    @PackagePrivate
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

    @Test
    fun `access object property`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    object PrivateObject{
                        val property: Int = 0
                    }
                """
            ,
            """
                    val test = PrivateObject.property
                """,
            2 // object ref, property ref
        )
    }

    @Test
    fun `access annotation on class`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    annotation class PrivateAnnotation
                """
            ,
            """
                    @PrivateAnnotation
                    class Test()
                """
        )
    }

    @Test
    fun `access annotation on fun and property`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    annotation class PrivateAnnotation
                """
            ,
            """
                    class Test(){
                        @PrivateAnnotation
                        val property:Int = 0
                        
                        @PrivateAnnotation
                        fun test(){}
                    }
                """,
            2
        )
    }

    @Test
    fun `access annotation on constructor`() {
        //language=kotlin
        doCheck(
            """
                    @PackagePrivate
                    annotation class PrivateAnnotation
                """
            ,
            """
                    @PrivateAnnotation
                    val property:Int = 0
                    
                    @PrivateAnnotation
                    fun test(){}              
                """,
            2
        )
    }


    private fun doCheck(declaration: String, usage: String, errorCount: Int = 1) {
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
            .allowDuplicates()
            .issues(PrivateMembersUsageDetector.PackagePrivateIssue)
            .run()
            .expectErrorCount(errorCount)
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
        PROPERTY,
        CLASS
    )
    annotation class PackagePrivate
"""